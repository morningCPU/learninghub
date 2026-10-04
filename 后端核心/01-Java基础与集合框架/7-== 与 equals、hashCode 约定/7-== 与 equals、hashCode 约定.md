# 7-== 与 equals、hashCode 约定  

## 一、学什么  

> 1. ==比较地址  
> 2. equals可重写  
> 3. hashCode与equals的契约及其在集合(HashMap/HashSet)中的意义  

  

## 二、做什么  

### 2.1 基础知识  

**1. ==和equals的区别**  
**(1) ==运算符**  
对基本数据类型是比较值是否相等  
引用数据类型是比较内存地址是否相等  

> 其实都是比较变量里面的值，对基本数据类型来说值就是本身的数值，而引用数据类型的值就是对象地址  

**(2) equals()方法**  
`equals()`是`Object`的实例方法，只能用于引用类型，不能用于基本类型  
源码：  

```java  
public boolean equals(Object obj){  
    return (this == obj);  
}  
```

+ 可见如果没有重写equals那就相当于==  
  

**2. hashCode()和equals()**  

+ hashCode() 返回对象的int哈希码，为哈希表集合服务(HashMap,HashSet)  
+ equals() 判断两个对象逻辑是否相等  
  

**(1) 哈希契约**  
1.如果equals相等，那么hashCode就要相等  
2.hashCode相等，equals不一定相等，可能发生hash冲突  
3.同一个对象，在用于equals进行比较的字段没有修改的情况下，多次调用hashCode返回值要相等  

> 这里说的是equals使用的字段，这是因为重写了equals的话hashCode也要重写，他们使用的字段要一样  
> 例子：  
> Person p1 = new Person("morning")  
> Person p2 = new Person("morning")  
> 将p1,p2存入hashCode的话，他们会被看作是两个人，这个时候就要对equals和hashCode进行重写，让他们逻辑一致  

**(2) HashSet/HashMap底层判断流程**  
先用hashCode进行判断，相同再判断equals，不同就放入  

+ hashCode相同equals不一定相同，可能发生hash冲突  
  

**(3) equals()规范**  
其实就是满足等价关系  

1. 自反性 ：x.equals(x) //true  
2. 对称性： x.equals(y) == y.equals(x) //true  
3. 传递性： x.equals(y) && y.equals(z) && x.equals(z) //true  
4. 一致性：多次调用，只要对象相关字段没有变结果就不会变  
5. 非空性：x.equals(null) //false  

+ 写的时候一定要注意equals和hashCode要使用同一个字段  
  
### 2.2 缓存  

```java  
Integer x = 127,y = 127；  
System.out.println(x == y); //true  
Integer m = 128,n = 128;  
System.out.println(m == n); //false  
```

`Interger`对象有缓存，值的范围在-128到127的范围的话会直接使用缓存  
对于Byte、Short、Interger、Long都是如此，范围也一样  
对Character是0到127  
对Boolean是true和false  

### 2.3 代码练习  

用 IDEA `Alt+Insert` 生成 `equals + hashCode`（只选 name 字段），  
把对象放进 `HashSet` 验证按 name 去重；然后删掉 `hashCode` 只留 `equals`，  
观察 HashSet 出现内容相同但重复的元素，写清原因（桶定位靠 hashCode，equals 只在同桶内比较）。  

```java  
package com.morning;  
  
import java.util.Objects;  
  
public class Person {  
    private String name;  
    private Integer id;  
  
    public Person(String name,Integer id){  
        this.name=name;  
        this.id=id;  
    }  
  
    @Override  
    public boolean equals(Object o) {  
        if (!(o instanceof Person person)) return false;  
        return Objects.equals(name, person.name) && Objects.equals(id, person.id);  
    }  
  
    @Override  
    public int hashCode() {  
        return Objects.hash(name, id);  
    }  
  
    @Override  
    public String toString() {  
        return "name:"+name+",id:"+id;  
    }  
}  
```

  

  

## 三、实践  

**1. 解释为什么重写 equals 必须重写 hashCode**  
因为哈希表是先用 hashCode 定位到桶，再用 equals 在桶内精确比较。如果只重写 equals 不重写 hashCode，两个逻辑相等的对象会因为默认 hashCode（内存地址）不同而落到不同的桶，equals 根本没机会执行，导致 HashSet 去重失败、HashMap 出现重复 key，违反equals 相同则 hashCode 必须相同的契约。  

**2. Integer 缓存范围**  
-128到127  

  

## 四、问题  

**1. == 和equals的区别**  
==对于基本数据类型比较的就是具体的数 ，对于引用类型比较的就是地址  
equals是自定义的比较，默认就是==  

**2. hashCode相同，equals一定相同吗**  
不一定，因为有哈希冲突  

**3. HashMap为什么用hashCode定位**  
因为 hashCode 能把任意类型的 key 瞬间映射成一个数组下标，实现 O(1) 的存取。如果用 equals 逐个比较，时间复杂度会退化到 O(n)，HashMap 就失去了存在的意义。  

