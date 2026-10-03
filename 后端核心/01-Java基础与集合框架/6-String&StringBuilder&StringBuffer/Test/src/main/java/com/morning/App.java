package com.morning;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
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
    }
}
