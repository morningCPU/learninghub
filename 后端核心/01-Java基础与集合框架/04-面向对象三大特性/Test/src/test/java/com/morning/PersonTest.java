package com.morning;
import org.junit.Test;

public class PersonTest {
    @Test
    public void testSetAge(){
        Person person = new Person();
        person.setAge(-10);
        person.setAge(10);
        System.out.println(person.getAge());
    }
}
