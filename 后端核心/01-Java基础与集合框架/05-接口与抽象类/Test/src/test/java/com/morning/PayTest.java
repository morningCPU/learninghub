package com.morning;

import org.junit.jupiter.api.Test;

public class PayTest {
    @Test
    public void testPay(){
        Pay pay = new Alipay(100);
        pay.pay(20);
    }
}
