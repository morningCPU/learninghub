package com.morning;

//使用 extends 进行继承
public class Dog extends Animal{
    public Dog(String name){
        //使用super进行父类的构造
        super(name);
        System.out.println("子类Dog");
    }

    @Override
    public void speak() {
        //使用super可以调用父类的元素
        super.speak();
        System.out.println("汪汪汪");
    }

    //父类中标记为final，不能进行重写，重写会报错
    //@Override
    //public void sleep(){
    //
    //}

    //重写方法，正是重写让多态有了意义，对一个方法不同对象有不同的实现
    @Override
    public void eat() {
        System.out.println("小狗啃骨头");
    }

    //子类独有方法
    public void watchHouse(){
        System.out.println("小狗看家");
    }
}
