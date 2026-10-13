package com.morning;

import java.util.HashSet;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Person p1 = new Person("person1",1);
        Person p2 = new Person("person1",1);
        HashSet<Person> set = new HashSet<>();
        set.add(p1);
        set.add(p2);
        for(Person p : set){
            System.out.println(p);
        }
    }
}
