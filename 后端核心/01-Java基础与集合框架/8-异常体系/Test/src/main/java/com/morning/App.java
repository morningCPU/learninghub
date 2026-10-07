package com.morning;

import java.util.Arrays;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        ReturnTest r = new ReturnTest();
        System.out.println(r.re());

        try{
            r.diy();
        }catch (BizException e){
            System.out.println(e.getMessage());
        }
    }
}
