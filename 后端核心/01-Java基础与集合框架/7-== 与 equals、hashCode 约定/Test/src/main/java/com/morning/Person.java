package com.morning;

import java.util.Objects;

public class Person {
    private String name;
    private Integer id;

    public Person(String name,Integer id){
        this.name=name;
        this.id=id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Person person)) return false;
        return Objects.equals(name, person.name) && Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public String toString() {
        return "name:"+name+",id:"+id;
    }
}
