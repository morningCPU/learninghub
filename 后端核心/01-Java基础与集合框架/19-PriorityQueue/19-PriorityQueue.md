# 19-PriorityQueue  
  
## 一、学什么  
  
> 1. 二叉堆的数组实现  
> 2. 上浮/下沉  
> 3. 小顶堆做TopK  
  
  
  
## 二、做什么  
  
### 2.1 PriorityQueue  
  
**(1) 底层存储**  
  
```java  
transient Object[] queue; //堆的底层数组  
private int size; //当前堆内元素个数  
private final Comparator<? super E> comparator;  
```  
  
+ 默认是小顶堆  
+ 如果传入自定义Compatator,可以变为大顶堆  
+ 二叉堆是完全二叉树，用数组紧凑存储  
  
**(2) 下标公式**  
  
假设当前节点下标为i  
  
> 左孩子：2 * i+ 1  
> 右孩子：2 * i+ 2  
> 父节点：(i - 1)/2  
  
+ 堆数组的下标从0开始，不是从1开始  
  
**(3) offer入队**  
`siftUp上浮`  
  
> 1. 扩容判断，元素放到数组size的位置，堆的最后一个空位  
> 2. size++  
> 3. siftUp上浮  
  
**(4) poll出队**  
`siftDown下沉`  
  
> 1. 取出堆顶元素  
> 2. 把数组最后一个元素挪到堆顶位置  
> 3. size--  
> 4. siftDoen下沉  
  
  
  
## 三、实践  
  
**1. 画出 5 个元素堆插入的完整过程**  
  
堆的插入和删除  
  
**2. 如何用 PriorityQueue 实现大顶堆**  
  
传入比较器  
  
```java  
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);  
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());  
```  
  
  
  
## 四、问题  
  
**1. PriorityQueue 是最大堆还是最小堆**  
  
小根堆  
  
**2. 怎么实现大顶堆**  
  
传入比较器  
  
```java  
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);  
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());  
```  
  
**3. TopK 有哪些解法，为什么选堆**  
  
内存足够、一次性拿到全部数组、只跑一次查询，QuickSelect 平均更快；  
海量 / 流式数据场景，QuickSelect 直接不能用，堆更好  
