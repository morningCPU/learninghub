package com.morning;

public class Person {
    private int age;

    public int getAge(){
        return age;
    }

    public void setAge(int age){
        if(age > 0 && age <= 150){
            this.age = age;
        }else{
            System.out.println("年龄不合法！");
        }
    }
}
