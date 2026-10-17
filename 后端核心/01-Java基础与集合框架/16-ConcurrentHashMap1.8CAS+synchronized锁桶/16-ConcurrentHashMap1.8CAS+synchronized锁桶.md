# 16-ConcurrentHashMap  
  
## 一、学什么  
  
> 1. 1.8放弃分段锁  
> 2. 桶空CAS无锁插入  
> 3. 桶非空synchronized锁头节点  
> 4. sizeCtl 的并发控制  
  
  
  
## 二、做什么  
  
### 2.1 JDK1.7版本  
  
Segment分段锁  
**1. 核心结构**  
`ConcurrentHashMap = Segment数组 + HashEntry数组 + 链表`  
  
具体结构：  
  
```text  
ConcurrentHashMap  
└── Segment[] segments （Segment数组）  
    ├─ Segment[0]  
    │   └── HashEntry[] table（哈希桶数组）  
    │       ├ HashEntry0 → HashEntry1 → HashEntry2（单向链表）  
    │       ├ HashEntry3  
    │       └ ...  
    ├─ Segment[1]  
    │   └── HashEntry[] table  
    └── ...直到 Segment[15]（默认长度16）  
```  
  
1. Segment继承ReentrantLock，本质就是一把独立的可重入锁  
  
   > ReentrantLock 可重入独占锁  
   > 1.独占：同一时间只能有一个线程拿到这把锁，其他线程阻塞等待  
   > 2.可重入：同一个线程拿到锁之后，可以再次重复获取这把锁  
   >  
   > 不可重入锁：进门凭证只能领一次，你人在屋里，再去领凭证，系统告诉你：凭证被占用，排队。你卡在屋里门口出不去。  
   > 可重入锁：同一个人，可以多次申领同一把门的凭证，记录申领次数；离开时归还对应次数的凭证，次数归零门才对外人开放。  
   > 同一个线程，已经持有锁，代码嵌套进入另一段同样需要这把锁保护的代码，再次执行 lock () 申请同一把锁  
  
2. 默认Segment数量为16，也就是默认16把分段锁  
  
   > 分段锁的核心：不同Segment是不同的锁，写Segment0不会锁住Segment1,实现分段并发  
  
3. 每个Segment内部维护一个HashEntry[]哈希桶数组，桶内是单向链表  
  
4. 先hash定位Segment，再在Segment内部hash定位桶  
  
**2. 读写特点**  
**(1) 写操作**  
put/remove必须获取对应Segment锁，只锁住这一段，其他Segment不受影响  
并发度上限 = Segment数量(默认16)  
**(2)读操作**  
大部分读不加锁，依靠volatile保证可见性  
  
> volatile  
> 1.保证可见性  
> 一个线程修改volatile变量，其他线程立即能够看到最新值，不会停留在CPU缓存  
> 2.禁止指令重排  
>  
> 只能保证拿到最新的HashEntry引用，不能保证读取和修改是原子的，极端场景读到旧数据，这时会加锁重读  
  
读到过期数据时会加锁重读  
**(3)扩容**  
只对当前Segment单独扩容，不会全局扩容  
**(4)缺点**  
1.分段锁粒度依然粗，一个Segment下面很多桶共用一把锁。同一个Segment下多个桶不能并发写。  
2.Segment是独立对象，占用额外内存  
3.扩容麻烦，全局size统计需要遍历所有Segment  
  
### 2.2 JDK1.8版本  
  
数组+链表+红黑树+CAS+synchronized桶锁  
**(1) CAS**  
Compare And Swap  
  
> 在要写的时候  
> 对要跟新的值，先读取得到一个值except  
> 在要写的时候读取写的时候的值update  
> 如果except == update 就正常写入，否则返回失败  
  
**(2) initTable()初始化哈希表**  
table延迟初始化，第一次put才创建数组，靠sizeCtl控制  
  
> sizeCtl  
> 0 : 表未初始化  
> -1 ： 正在初始化  
> 负数 ：正在扩容  
> 正数 ：下一次扩容阈值  
  
判断sizeCtl == 0，尝试CAS把sizeCtl从0改为-1  
CAS成功，计算初始容量，计算新sizeCtl  
CAS失败，自旋等待  
**(3) putVal完整流程**  
  
> 当前桶为空，直接调用casTabAt无锁插入新Node，成功直接返回，失败则自旋重试  
> 如果桶头节点f.hash==MOVED(-1),代表当前表正在扩容，当前线程协助扩容，扩容完成后再继续put  
> 其他情况会synchronized加锁锁住桶头节点  
  
**(4) 扩容要点**  
**多线程协助扩容**  
  
+ 一个线程触发扩容后，其他线程put/get时发现MOVED节点，会一起帮忙迁移数据，提高扩容效率  
+ size统计，用baseCount+CounterCell(这个是增量)数组，分散计数，解决高并发下计数竞争  
  
### 2.3 并发对比  
  
HashMap,Hashtable,ConcurrentHashMap  
  
- HashMap：并发 put，丢失数据，size < n，极端情况抛出数组越界异常（线程不安全）  
- Hashtable：全表 synchronized 锁，结果 size 正确，但是速度很慢  
- ConcurrentHashMap：线程安全，size 正确，性能远高于 Hashtable  
  
### 2.4 volatile是如何运用与CAS和synchronized  
  
1. **CAS**  
   **目标变量必须 volatile**   
   CAS 硬件指令保证原子性，volatile 负责：读拿到最新值、写对外可见；没有 volatile，CAS 读到缓存脏数据，逻辑出错。  
2. **synchronized**  
   **自带 volatile 内存屏障，变量不用 volatile**   
   不需要共享变量标记 volatile。加锁时刷新读、释放锁强制写回主存，自带 volatile 的内存语义  
   锁的获取 / 释放自动完成内存刷新，提供可见性、禁止重排，并且提供互斥，保证代码块原子性。  
  
  
  
## 三、实践  
  
**1. putVal流程**  
  
> 当前桶为空，直接调用casTabAt无锁插入新Node，成功直接返回，失败则自旋重试  
> 如果桶头节点f.hash==MOVED(-1),代表当前表正在扩容，当前线程协助扩容，扩容完成后再继续put  
> 其他情况会synchronized加锁锁住桶头节点  
  
**2. 桶空CAS**  
  
Compare And Swap  
  
> 在要写的时候  
> 对要跟新的值，先读取得到一个值except  
> 在要写的时候读取写的时候的值update  
> 如果except == update 就正常写入，否则返回失败  
  
**3. 桶非空锁头节点**  
  
> 如果桶头节点f.hash==MOVED(-1),代表当前表正在扩容，当前线程协助扩容，扩容完成后再继续put  
> 其他情况会synchronized加锁锁住桶头节点  
  
**4. 解释1.7分段锁与1.8锁桶的区别**  
  
1.JDK1.7 使用 Segment 分段锁，Segment 继承 ReentrantLock，默认 16 段。一把锁保护多个哈希桶，同一个 Segment 下不同桶的写操作会互斥，并发上限固定 16  
  
2.JDK1.8 去掉 Segment，改用 CAS + synchronized 锁桶头。锁粒度降到单个哈希桶，不同桶可以并发写入。空桶场景还可以直接 CAS 无锁插入，并发能力更强。并且 JDK1.6 之后 synchronized 经过锁优化，性能足够，代码更简洁  
  
  
  
## 四、问题  
  
**1. ConcurrentHashMap 1.8 的线程安全是怎么实现的**  
JDK1.8 CHM(ConcurrentHashMap) 依靠 CAS + synchronized 锁桶头 + volatile实现线程安全。桶为空时，用 CAS 无锁插入；桶存在元素时，锁住桶头节点，仅锁住单个桶，不同桶写操作可以并发。Node 的 value、next 用 volatile 保证可见性。get 操作不加锁，依靠 volatile 读取最新数据；扩容时多线程协助迁移，计数采用 baseCount+CounterCell 分散计数缓解竞争。  
  
**2. 为什么放弃分段锁**  
1.7 分段锁粒度依然偏粗，一个 Segment 下多个桶共用一把锁，同 Segment 不同桶写操作也会互斥，并发上限被限制为 16  
Segment 对象带来额外内存开销。同时 JDK1.6 之后 synchronized 经过锁优化，性能足够好。改成锁桶头，锁粒度降到单个桶，并发能力大幅提升，代码更简洁，还支持多线程协助扩容。  
  
**3. CAS 和 synchronized 分别用在什么场景**  
CAS 用于桶为空的场景，乐观无锁原子插入，低竞争性能好；addCount 更新计数器也使用 CAS。  
当桶已有节点，发生 hash 冲突，多个线程竞争同一个桶时，使用 synchronized 锁住桶头。  
简单说：没有冲突用 CAS，有冲突用 synchronized 悲观锁。  
  
  
  
  
  
  
  
