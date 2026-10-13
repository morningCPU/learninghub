package com.morning;

import static java.lang.System.nanoTime;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void timeTest(){
        int n = 100000;

        //String
        Long startTime = nanoTime();
        String s = "";
        for(int i = 0;i<n;++i){
            s = s + "a";
        }
        Long endTime = nanoTime();
        Long time = endTime - startTime;
        System.out.println(time);

        //StringBuffer
        startTime = nanoTime();
        StringBuffer ss = new StringBuffer();
        for(int i = 0;i<n;++i){
            ss.append("a");
        }
        String res = ss.toString();
        endTime = nanoTime();
        time = endTime - startTime;
        System.out.println(time);
    }
}
