package com.morning;

public final class Cat extends Animal{
    public String name;

    public Cat(String name){
        super(name);
    }

    @Override
    public void eat() {
        System.out.println("小猫吃鱼");
    }

    public void catchMouse(){
        System.out.println("小猫抓老鼠");
    }
}
