package com.morning;

abstract class Animal {
    //实例成员变量，可以保存对象的状态
    String name;

    public Animal(String name){
        this.name = name;
    }

    public abstract void shout();

    public void sleep(){
        System.out.println("睡觉");
    }
}
