# 6-String&StringBuilder&StringBuffer

## 一、学什么

> 1. String的不可变性
> 2. 字符串常量池与intern
> 3. 三种字符串类的性能与线程安全差异



## 二、做什么

### 2.1 知识学习

**1. String基础知识**
jdk8及之前用 `private final char[] value` 存储字符
之后改为使用 `private final byte[] value` 存储字符
用编码压缩节省内存

+ 通过final char[]可以发现，数字引用不可变，但是数组内容可以改变，String是通过私有封装，让外部无法修改数组，实现字符串不可变
+ 一旦String创建内容便不可变，看似的修改其实是创建了一个新的String对象

**2. 两种创建String的方式**
**(1) 字面量创建**
`String s = "abcdef"`
通过这种方式，JVM会先去字符串常量池查找有没有这个字符串"abcdef"。如果有的话就会直接返回常量池内对象引用，没有的话会先在常量池中创建这个字符串对象然后返回引用

```java
String s1 = "abc";
String s2 = "abc";
System.out.println(s1 == s2); // true
```

**(2) 通过new创建**
`String s = new String("abcdef")`
1.首先会看常量池中有没有这个字符串，如果没有会创建
2.通过new进行创建一定会在堆中创建一个对象，这个对象的value数组引用指向的是常量池中的数据，最终返回的是堆对象的引用

> 可以看出使用 new String("abcdef") 可能创建1个或2个对象
> 当常量池中有这个字符串时就只是堆中创建一个对象
> 如果常量池中没有，那常量池和堆都会创建对象

```java
String s3 = new String("abc");
String s4 = new String("abc");
System.out.println(s3 == s4); // false
System.out.println(s3.equals(s4)); //true
```

+ `==` 比较的是引用，`equals`比较的才是字符串内容

**3. 字符串拼接**

**(1) 常量的拼接**

```java
String s = "a" + "b" + "c";
// 编译器直接优化成 String s = "abc";
```

**(2) 变量拼接**

```java
String a = "a";
String b = "b";
String c = a + b;
```

编译器会自动新建`StringBuilder`，调用`append`拼接，最后使用`toString`生成新的String

```java
String c = new StringBuilder(a).append(b).toString();
```

+ 这里要注意`toString`只会在堆上创建对象，并不在常量池中创建

**(3) 循环拼接**

```java
String str = "";
for(int i=0;i<1000;i++){
    str += i; // 每次循环新建StringBuilder + new String，大量对象，GC压力大。所以循环拼接要手动StringBuilder
}
```

改进为：

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String str = sb.toString();
```

**(4) String、StringBuffer、StringBuilder的对比**

| 类            | 可变性 | 线程安全                           | 效率                 | 底层                   |
| ------------- | ------ | ---------------------------------- | -------------------- | ---------------------- |
| String        | 不可变 | 安全（不可改）                     | 低，频繁拼接大量对象 | char[] / byte[] final  |
| StringBuilder | 可变   | ❌ 非线程安全                       | 最高                 | 可变 char []，自动扩容 |
| StringBuffer  | 可变   | ✅ 线程安全（方法加`synchronized`） | 略低                 | 可变 char []           |

> 扩容规则
> 默认初始容量16
> new = old*2+2

**4. 字符串常量池**
**(1) 放置位置**
JDK7及之后就放在堆中，之前在永久代
**(2) String.intern()方法**
在常量池中查找，如果已经存在就返回这个字符串的引用，没有的话会把堆中的这个字符串的引用放入常量池中，然后返回该引用

**5. 字符串不可变的好处**

+ 字符串常量池实现的前提
  只有不可变，多个引用才能安全共享同一个池内对象
+ 哈希值可以缓存
  String重写hashCode,创建时计算一次hash,存入对象，频繁作为hashmap key,效率高
+ 线程安全
  不可变对象天然线程安全，多线程并发访问无需同步
+ 安全
  不会被恶意篡改

> 缺点:
> 修改字符串产生大量中间对象，占用内存，频繁拼接性能差

## 2.2 代码实现

```java
// 这个 "ab" 在常量池中
String a = "ab";
// 直接是字面量的拼接，直接就是 "ab" ，在常量池中
String b = "a" + "b";
System.out.println(a == b); //true

//使用new，返回的是堆中创建的
String c = new String("ab");
//a在常量池
System.out.println(a == c); // false

//这里有以下几个创建
//1.常量池中创建"a","b"
//2.堆中创建"a","b"
//new String()这是一个变量，所以是变量的拼接，结果放在堆中
String s = new String("a") +new String("b");
//"ab"在常量池
System.out.println(s == "ab"); //false

//这里返回的是"ab"在常量池的引用
String ss = s.intern();
System.out.println(ss == "ab"); //true
```

### 2.3 循环拼接

```java
@Test
public void timeTest(){
    int n = 100000;
    //String
    Long startTime = nanoTime();
    String s = "";
    for(int i = 0;i<n;++i){
        s = s + "a";
    }
    Long endTime = nanoTime();
    Long time = endTime - startTime;
    System.out.println(time);
    //StringBuffer
    startTime = nanoTime();
    StringBuffer ss = new StringBuffer();
    for(int i = 0;i<n;++i){
        ss.append("a");
    }
    String res = ss.toString();
    endTime = nanoTime();
    time = endTime - startTime;
    System.out.println(time);
}

//output
//551219200
//2442000
//相差200多倍
```



## 三、实践

**1. 用 == 判断三个字符串比较题并解释原因**

```java
// 这个 "ab" 在常量池中
String a = "ab";
// 直接是字面量的拼接，直接就是 "ab" ，在常量池中
String b = "a" + "b";
System.out.println(a == b); //true

//使用new，返回的是堆中创建的
String c = new String("ab");
//a在常量池
System.out.println(a == c); // false

//这里有以下几个创建
//1.常量池中创建"a","b"
//2.堆中创建"a","b"
//new String()这是一个变量，所以是变量的拼接，结果放在堆中
String s = new String("a") +new String("b");
//"ab"在常量池
System.out.println(s == "ab"); //false

//这里返回的是"ab"在常量池的引用
String ss = s.intern();
System.out.println(ss == "ab"); //true
```

**2 . 说出String不可变的4个原因**

- 字符串常量池实现的前提
  只有不可变，多个引用才能安全共享同一个池内对象
- 哈希值可以缓存
  String重写hashCode,创建时计算一次hash,存入对象，频繁作为hashmap key,效率高
- 线程安全
  不可变对象天然线程安全，多线程并发访问无需同步
- 安全
  不会被恶意篡改

**3. 解释intern()的作用**

在常量池中查找，如果已经存在就返回这个字符串的引用，没有的话会把堆中的这个字符串的引用放入常量池中，然后返回该引用

## 四、问题

**1. String为什么设计出不可变**

- 字符串常量池实现的前提
  只有不可变，多个引用才能安全共享同一个池内对象
- 哈希值可以缓存
  String重写hashCode,创建时计算一次hash,存入对象，频繁作为hashmap key,效率高
- 线程安全
  不可变对象天然线程安全，多线程并发访问无需同步
- 安全
  不会被恶意篡改

**2. String s = new String("abc")创建了几个对象**

1个或者2个
当"abc"已经在常量池中，就只会在堆中创建一个对象
如果常量池中没有，还会现在常量池中创建一个对象

**3. StringBuilder与StringBuffer的区别**

主要是是StringBuffer加了synchronized保证线程安全

