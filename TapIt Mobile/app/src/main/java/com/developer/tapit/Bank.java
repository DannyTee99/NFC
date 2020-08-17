package com.developer.tapit;

public class Bank {

    private String name;
    private int amount;
    private int lastwithdrawal;

    public Bank() {

    }

    public Bank(String name, int amount, int lastwithdrawal) {
        this.name = name;
        this.amount = amount;
        this.lastwithdrawal = lastwithdrawal;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getLastwithdrawal() { return lastwithdrawal; }

    public void setLastwithdrawal(int lastwithdrawal) {
        this.lastwithdrawal = lastwithdrawal;    }



}
