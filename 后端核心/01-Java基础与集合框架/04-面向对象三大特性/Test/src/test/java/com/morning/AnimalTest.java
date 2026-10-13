package com.morning;

import org.junit.Test;

public class AnimalTest {
    @Test
    public void testEat(){
        Animal a = new Dog("dog");
        Animal b = new Cat("cat");
        System.out.println(a.name);
        System.out.println(b.name);
        a.eat();
        b.eat();

        Dog dog = (Dog)a;
        Cat cat = (Cat)b;
        System.out.println(dog.name);
        System.out.println(cat.name);
        dog.watchHouse();
        cat.catchMouse();
    }
}
