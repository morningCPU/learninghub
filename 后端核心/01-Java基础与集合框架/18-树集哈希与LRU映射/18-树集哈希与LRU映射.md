# 18-树集哈希与LRU映射  
  
## 一、学什么  
  
> 1. TreeMap的红黑树有序性  
> 2. HashSet/TreeSet的内部实现  
> 3. LinkedHashMap双向链表  
> 4. LRU缓存  
  
  
  
## 二、做什么  
  
### 2.1 TreeMap  
  
底层是红黑树  
  
**(1) 核心字段**  
  
```java  
private transient Entry<K,V> root; //红黑树根节点  
private final Comparator<? supre K> Comparator;//比较器  
  
//红黑树内部节点类 Entry  
//key,value,left,right,parent,color  
```  
  
**(2) put核心流程**  
  
> 1. 如果 root == null , 新建节点作为根，根置黑  
> 2. 不为空，从根向下，通过 comparator/comparaTo 比较key,找到新节点的父结点  
> 3. 判断新key比父key大小，挂到左/右孩子  
> 4. 插入新节点默认红色  
> 5. 调用fixAfterInsertion()进行调整  
  
**(3) get方法**  
从根节点二分查找，实践复杂度是 O(nlogn)  
  
**(4) 红黑树的五条性质**  
  
1. 每个节点都是红色或黑色的  
2. 根节点是黑色的  
3. 所有非空叶子节点是黑色的  
4. 红色节点的两个子节点一定是黑色的  
5. 任意节点到它所有叶子节点的路径上黑色节点的个数相等  
  
最长路径 <= 最短路径的两倍  
  
### 2.2 HashSet  
  
底层是HashMap，加上了去重  
  
```java  
private transient HashMap<E,Object> map;  
private static final Object PRESENT = new Object(); // 规定占位value  
  
public boolean add(E e){  
    return map.put(e,PRESET) == null;  
}  
```  
  
+ HashSet元素 = HashMap的key  
  所有元素共享静态常量PRESENT  
+ 本质是复用HashMap的key的唯一性  
+ map.put  
  key不存在返回null  
  key存在返回旧value  
  
### 2.3 TreeSet  
  
底层TreeMap,有序加去重  
  
```java  
private transient NavigableMap<E,Object> m;  
private static final Object PRESENT = new Object();  
```  
  
TreeSet是TreeMap的包装，存入元素作为TreeMap的key,value固定PRESENT  
  
**关键规则**  
  
1. 元素必须可以比较  
   无参构造：元素实现Comparable自然排序  
   带参构造：传入Comparator定制排序  
2. 不可比较会直接抛出ClassCastException  
3. 去重规则，比较器返回0即判定重复  
4. 遍历是红黑树中序遍历，天然升序  
  
### 2.4 LinkedHashMap  
  
HashMap + 双向链表  
LRU基础  
  
继承HashMap,节点增加before,after双向指针，维护一条贯穿全部节点的双向链表  
head最旧，tail最新  
  
### 2.5 手写LRU  
  
```java  
package com.morning;  
  
import java.util.LinkedHashMap;  
import java.util.Map;  
  
public class LRUCache<K,V> extends LinkedHashMap<K,V> {  
    private final int capacity;  
  
    public LRUCache(int capacity){  
        super(capacity,0.75f,true);  
        this.capacity = capacity;  
    }  
  
    @Override  
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {  
        return size() > capacity;  
    }  
}  
```  
  
**LinkedHashMap构造函数**  
  
```java  
public LinkedHashMap(int initalCapacity,float loadFactor,boolean accessOrder)  
```  
  
+ 这里accessOrder就是LRU实现的关键  
  为false查询不会改变顺序  
  为true会把查询到的放到最前面  
  
**removeEldestEntry**  
默认不会淘汰，LRU要重写为超过数量淘汰最后一个  
  
  
  
## 三、实践  
  
**1. 红黑树的5条性质**  
  
> 1. 每个节点都是红色或黑色的  
> 2. 根节点是黑色的  
> 3. 所有非空叶子节点是黑色的  
> 4. 红色节点的两个子节点一定是黑色的  
> 5. 任意节点到它所有叶子节点的路径上黑色节点的个数相等  
  
**2. HashSet去重与TreeSet排序的实现原理**  
  
HashSet 底层封装了 HashMap。存入元素作为 HashMap 的 key，value 统一用一个静态常量 PRESENT 占位。添加元素时调用`map.put(e, PRESENT)`：put 方法先计算 key 的 hashCode 定位桶位，再用 equals 判断 key 是否存在；key 已存在就返回旧值，add 返回 false，实现去重。去重依赖 hashCode() + equals()。  
  
TreeSet 底层封装 TreeMap，存入元素作为 TreeMap 的 key，value 同样是占位对象 PRESENT。底层是红黑树，添加时依靠元素的`Comparable`自然序，或者构造传入的 Comparator 比较器。两个元素比较返回 0 就判定重复，同时红黑树中序遍历保证元素有序。去重与排序都依赖比较器，不是 equals；元素不可比较会抛出`ClassCastException`。  
  
**3. LinkedHashMap版LRU缓存**  
  
```java  
package com.morning;  
  
import java.util.LinkedHashMap;  
import java.util.Map;  
  
public class LRUCache<K,V> extends LinkedHashMap<K,V> {  
    private final int capacity;  
  
    public LRUCache(int capacity){  
        super(capacity,0.75f,true);  
        this.capacity = capacity;  
    }  
  
    @Override  
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {  
        return size() > capacity;  
    }  
}  
```  
  
  
  
## 四、问题  
  
**1. HashSet怎么去重**  
  
HashSet 底层封装了 HashMap。存入元素作为 HashMap 的 key，value 统一用一个静态常量 PRESENT 占位。添加元素时调用`map.put(e, PRESENT)`：put 方法先计算 key 的 hashCode 定位桶位，再用 equals 判断 key 是否存在；key 已存在就返回旧值，add 返回 false，实现去重。去重依赖 hashCode() + equals()。  
  
**2. TreeSet的元素为什么必须可比较**  
  
TreeSet 底层是 TreeMap，TreeMap 底层是红黑树（二叉搜索树）。二叉搜索树插入、查找元素时，****必须比较 key 的大小，决定往左子树还是右子树走。**  
所以元素必须具备比较能力：要么元素实现`Comparable`接口（自然比较），要么创建 TreeSet 时传入`Comparator`定制比较规则。如果两者都没有，添加元素就会抛出`ClassCastException`。  
  
**3. 手写一个LRU缓存**  
  
LinkedHashMap 版，再答双链表+HashMap 版  
  
LinkedHashMap  
  
```java  
import java.util.LinkedHashMap;  
import java.util.Map;  
  
public class LRUCache<K, V> extends LinkedHashMap<K, V> {  
    private final int capacity;  
  
    public LRUCache(int capacity) {  
        // initialCapacity, loadFactor, accessOrder=true 开启访问顺序  
        super(capacity, 0.75f, true);  
        this.capacity = capacity;  
    }  
  
    // 淘汰钩子：put新增元素后自动回调  
    @Override  
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {  
        // 超过容量就淘汰最久未使用节点  
        return size() > capacity;  
    }  
  
    public V get(K key) {  
        return super.get(key);  
    }  
  
    public void put(K key, V value) {  
        super.put(key, value);  
    }  
}  
```  
  
双向链表+HashMap  
  
```java  
import java.util.HashMap;  
  
public class LRUCacheSelf<K,V> {  
    // 双向链表节点  
    static class Node<K,V> {  
        K key;  
        V value;  
        Node<K,V> before;  
        Node<K,V> after;  
        Node(K key, V value){  
            this.key = key;  
            this.value = value;  
        }  
    }  
  
    private final int capacity;  
    private final HashMap<K, Node<K,V>> map;  
    // 虚拟头、虚拟尾，哨兵节点，简化空指针边界  
    private final Node<K,V> head;  
    private final Node<K,V> tail;  
  
    public LRUCacheSelf(int capacity) {  
        this.capacity = capacity;  
        map = new HashMap<>();  
        head = new Node<>(null,null);  
        tail = new Node<>(null,null);  
        head.after = tail;  
        tail.before = head;  
    }  
  
    public V get(K key) {  
        Node<K,V> node = map.get(key);  
        if(node == null){  
            return null;  
        }  
        // 访问成功，移动到尾部  
        moveToTail(node);  
        return node.value;  
    }  
  
    public void put(K key, V value) {  
        Node<K,V> node = map.get(key);  
        if(node != null){  
            // key存在：更新值，挪到尾部  
            node.value = value;  
            moveToTail(node);  
        } else {  
            // 新建节点  
            Node<K,V> newNode = new Node<>(key, value);  
            map.put(key, newNode);  
            addToTail(newNode);  
            // 容量满，淘汰最久未使用  
            if(map.size() > capacity){  
                Node<K,V> eldest = head.after;  
                removeNode(eldest);  
                map.remove(eldest.key);  
            }  
        }  
    }  
  
    // 将节点从链表摘除  
    private void removeNode(Node<K,V> node){  
        node.before.after = node.after;  
        node.after.before = node.before;  
    }  
  
    // 添加到tail前面（最新位置）  
    private void addToTail(Node<K,V> node){  
        node.before = tail.before;  
        node.after = tail;  
        tail.before.after = node;  
        tail.before = node;  
    }  
  
    // 移动节点到尾部：先摘，再加  
    private void moveToTail(Node<K,V> node){  
        removeNode(node);  
        addToTail(node);  
    }  
}  
```  
  
