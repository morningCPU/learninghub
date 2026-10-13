package com.morning;

public class Animal {
    public String name;

    public Animal(String name){
        this.name = name;
        System.out.println("父类Animal构造器执行");
    }

    public void speak(){
        System.out.println("动物发出声音");
    }

    //使用final子类不能重写override
    public final void sleep(){
        System.out.println("动物睡觉");
    }

    public void eat(){
        System.out.println("动物吃东西");
    }
}
