package com.morning;

public abstract class Pay {
    private int balance;

    public Pay(int balance){
        this.balance = balance;
    }

    public int checkBalance(){
        return balance;
    }

    public abstract void pay(int money);

    public int getBalance(){
        return this.balance;
    }

    public void setBalance(int balance){
        this.balance = balance;
    }
}
