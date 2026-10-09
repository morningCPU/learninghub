package com.morning;

@MyComponent("userService")
public class UserService {
    public UserService() {
        System.out.println("  [构造] UserService 被实例化");
    }
    public void hello() { System.out.println("  UserService.hello()"); }
}
