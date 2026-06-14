package com.tms.model;

public class Account {
    private int id;
    private String accountNumber;
    private String holderName;
    private double balance;
    private String createdAt;

    public Account(int id, String accountNumber, String holderName, double balance, String createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public String getCreatedAt() { return createdAt; }

    public String toJson() {
        return String.format("{\"id\":%d,\"accountNumber\":\"%s\",\"holderName\":\"%s\",\"balance\":%.2f,\"createdAt\":\"%s\"}",
                id, accountNumber, holderName, balance, createdAt);
    }
}
