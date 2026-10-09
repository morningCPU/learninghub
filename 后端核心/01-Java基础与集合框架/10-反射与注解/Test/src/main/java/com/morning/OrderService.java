package com.morning;

@MyComponent
public class OrderService {
    public OrderService() {
        System.out.println("  [构造] OrderService 被实例化");
    }
    public void hello() { System.out.println("  OrderService.hello()"); }
}
