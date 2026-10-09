package com.morning;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception{
        Class<?> clazz = Class.forName("com.morning.User");
        Constructor<?> con = clazz.getDeclaredConstructor(String.class);
        con.setAccessible(true);
        User obj = (User) con.newInstance("morning");

        Field nameField = clazz.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(obj,"tom");
        System.out.println(nameField.get(obj));

        Method sayHelloMethod = clazz.getDeclaredMethod("sayHello");
        sayHelloMethod.setAccessible(true);
        sayHelloMethod.invoke(obj);
    }
}
