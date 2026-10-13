package com.morning;

public class Alipay extends Pay{
    public Alipay(int balance){
        super(balance);
    }

    @Override
    public void pay(int money) {
        System.out.println("剩余："+getBalance());
        System.out.println("花费："+money);
        setBalance(getBalance()-money);
        System.out.println("结算："+getBalance());
    }
}
