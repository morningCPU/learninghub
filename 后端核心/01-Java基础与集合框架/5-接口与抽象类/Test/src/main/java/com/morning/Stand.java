package com.morning;

public interface Stand {
    //默认类型是public static final
    String NAME = "test";

    //java7 抽象方法，默认 public abstract
    void say();

    //默认方法
    default void hello(){
        System.out.println("default");
    }

    //java8 静态方法，属于接口本身
    static void staticFunc(){
        System.out.println("static");
    }

    //java9 私有方法，接口内部代码复用
    private void privateFunc(){

    }
}
