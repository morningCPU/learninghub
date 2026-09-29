package com.morning;

public interface Payable {
    int MAX_AMOUNT = 100;

    default void log(){
        System.out.println("log");
    }

    static void tip() {
        System.out.println("tip");
    }
}
