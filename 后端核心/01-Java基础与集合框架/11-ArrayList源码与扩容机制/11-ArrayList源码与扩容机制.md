# 11-ArrayList源码与扩容机制  
  
## 一、学什么  
  
> 1. 动态数组结构  
> 2. grow()扩容1.5倍  
> 3. 增删查的时间复杂度  
  
  
  
## 二、做什么  
  
看 JavaGuide "ArrayList 源码分析（JDK1.8）" 建立整体认知；  
打开 IDEA `Ctrl+N` 搜 `ArrayList`，从 `add(E)` → `ensureCapacityInternal` → `grow()` 逐行跟读，重点看 `newCapacity = oldCapacity + (oldCapacity >> 1)` 与 `Arrays.copyOf`。  
  
### 2.1 整体认识  
  
**1. 基础特性**  
**(1) 底层结构**  
`Object[] elementData` 动态数组，连续内存  
支持随机访问，查询快，中间插入/删除慢(需要移动元素)  
**(2) 线程不安全**  
多线程并发修改会抛 `ConcurrentModificationException`  
线程安全可选 `Vector` 、`CopyOnWriteArrayList`  
**(3) 默认容量**  
默认初始容量10  
创建ArrayList时不会立即初始化长度为10的数组，而是赋值静态空数组常量 `DEFUALTCAPACITY_EMPTY_ELEMENTDATA`  
第一次add才真正扩容到10  
通过这样的懒加载可以节省内存  
**(4) 扩容机制**  
容量不够时自动扩容，扩容为原来的1.5倍  
通过移位运算 `oldCapacity >> 1`  
**(5) 允许null**  
元素有序，可重复  
**(6) size**  
`size` 是实际元素个数  
`elementData.length` 是数组容量  
size <= capacity  
**2. 核心常量和成员变量**  
  
```java  
//默认初始容量  
private static final int DEFAULT_CAPACITY = 10;  
  
//空数组，用户指定容量为0时使用  
private static final Object[] EMPTY_ELEMENTDATA = {}  
  
//空数组，无参构造器函数使用，标记默认空数组，第一次add会扩容到10  
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {}  
  
//存储元素的底层数组，transient不参与序列化  
transient Object[] elementData;  
  
//实际存放元素数量  
private int size;  
  
//最大数组容量，放置OOM  
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;  
```  
  
### 2.2 add的流程  
  
java8  
  
**1. add(E e)入口方法**  
  
```java  
public boolean add(E e){  
    ensureCapacityInternal(size + 1);  
    elementData[size++] = e;  
    return true;  
}  
```  
  
+ 可见add一定会成功，除非OOM  
  
**2. ensureCapacityInternal(int minCapacity)**  
  
```java  
private void ensureCapacityInternal(int minCapacity){  
    ensureExplicitCapacity(calculateCapacity(elementData,minCapacity));  
}  
```  
  
+ `calculateCapacity(elementData,minCapacity)`  
  计算实际需要的最小容量  
+ `ensureExplicitCapacity()`  
  判断是否需要扩容  
  
**3. calculateCapacity**  
  
```java  
private static int calculateCapacity(Object[] elementData,int minCapacity){  
    if(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA){  
        return Math.max(DEFAULT_CAPACITY, minCapacity)  
    }  
    return minCapacity;  
}  
```  
  
**4. ensureExplicitCapacity**  
  
```java  
private void ensuerExplicitCapacity(int minCapacity){  
    modCount++;  
    if(minCapacity - elementData.length > 0){  
        grow(minCapacity);  
    }  
}  
```  
  
**5. grow**  
  
```java  
private void grow(int minCapacity){  
    int oldCapacity = elementData.length;  
    int newCapacity = oldCapacity + (oldCapacity >> 1);  
      
    if(newCapacity - minCapacity < 0)  
        newCapacity = minCapacity;  
      
    if(newCapacity - MAX_ARRAY_SIZE > 0)  
        newCapacity = hugeCapacity(minCapacity);  
      
    elementData = Arrays.copyOf(elementData,newCapacity);  
}  
```  
  
**6. hugeCapacity**  
  
```java  
private static int hugeCapacity(int minCapacity){  
    if(minCapacity < 0)  
        throw new OutOfMemoryError();  
    return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;  
}  
```  
  
### 2.3 扩容取1.5  
  
扩容取 1.5 倍是兼顾空间利用与拷贝次数，均摊复杂度 O(1)。  
  
大部分 add 直接尾部赋值 O (1)；少数触发扩容时需要拷贝全部元素 O (n)。使用摊还分析，把扩容拷贝代价分摊到所有 add 操作，得到均摊 O (1)。  
选择 1.5 倍扩容是空间和拷贝次数的权衡：倍数太大浪费内存；倍数太小扩容太频繁。1.5 倍减少扩容次数同时控制空间闲置，移位运算效率高。  
  
  
  
## 三、实践  
  
**1. 写出 grow() 的 3 行核心代码**  
  
```java  
int oldCapacity = elementData.length;  
int newCapacity = oldCapacity + (oldCapacity >> 1);  
elementDate = Array.copyOf(elementData,newCapacity);  
```  
  
**2. 说清 get/insert/remove 的复杂度**  
  
**(1) get**  
O(1)  
支持随机访问  
**(2) insert**  
O(n)  
插入点之后所有元素向后移动一位  
**(3) remove**  
O(n)  
index后面所有元素向前移动一位  
  
  
  
## 四、问题  
  
**1. ArrayList 的扩容机制？为什么是 1.5 倍？**  
1.底层是Object[] elementData数组，无参构造懒加载，创建时指向静态空数组DEFAULTCAPACITY_EMPTY_ELEMENTDATA,数组长度0  
第一次add才真正初始化容量10  
2.调用`add(E e)`时先执行`ensureCapacityInternal(size+1)`，计算本次添加需要的最小容量 `minCapacity`  
3.`ensureExplicitCapacity`判断：`minCapacity > elementData.length`,满足则调用`grow()`扩容  
4.grow核心  
  
```java  
int oldCapacity = elementData.length;  
int newCapacity = oldCapacity + (oldCapacity >> 1);  
elementData = Array.copyOf(elementData,newCapacity);  
```  
  
有两个边界判断：如果算出的1.5倍依然小于minCapacity，直接取minCapacity。超过最大数组上限则调用hugeCapacity  
5.扩容本质：数组内存连续，不能原地扩容，只能新建更大数组+拷贝元素，拷贝代价高  
  
扩容取 1.5 倍是兼顾空间利用与拷贝次数，均摊复杂度 O(1)。  
  
大部分 add 直接尾部赋值 O (1)；少数触发扩容时需要拷贝全部元素 O (n)。使用摊还分析，把扩容拷贝代价分摊到所有 add 操作，得到均摊 O (1)。  
选择 1.5 倍扩容是空间和拷贝次数的权衡：倍数太大浪费内存；倍数太小扩容太频繁。1.5 倍减少扩容次数同时控制空间闲置，移位运算效率高。  
  
**2. ArrayList 和普通数组的区别**  
普通数组长度固定，ArrayList在普通数组之上做了一层封装，提供自动扩容、常用集合操作 API，但是底层依然依赖数组。  
  
**3. Arrays.asList返回的 List 有什么坑**  
  
`List<Integer> list = Arrays.asList(1,2,3);`  
返回的是Arrays的内部静态类`ArrayList`，不是`java.util.ArrayList`  
`java.util.Arrays.ArrayList`知识一个视图包装，底层直接复用传入的原数组，不是新建数组  
**(1) 大小固定，不能add/remove**  
底层数组固定长度，不支持扩容  
调用`add() / remove()` 会直接抛出 `UnsupportedOperationException`  
  
**(2) 和原数组引用共享，互相影响**  
修改原数组元素，asList返回的list会跟着变，修改list元素，原数组也变  
  
**(3) 不能传入基本类型数组**  
