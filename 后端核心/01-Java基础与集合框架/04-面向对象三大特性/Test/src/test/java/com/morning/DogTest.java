package com.morning;

import org.junit.Test;

public class DogTest {
    @Test
    public void testSpeak(){
        Dog dog = new Dog("haha");
        dog.speak();
        dog.sleep();
    }
}
