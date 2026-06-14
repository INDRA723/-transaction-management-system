package com.tms.model;

public class Transaction {
    private int id;
    private int accountId;
    private String type;
    private double amount;
    private String description;
    private String relatedAccount;
    private String transactionDate;

    public Transaction(int id, int accountId, String type, double amount,
                       String description, String relatedAccount, String transactionDate) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.relatedAccount = relatedAccount;
        this.transactionDate = transactionDate;
    }

    public int getId() { return id; }
    public int getAccountId() { return accountId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getRelatedAccount() { return relatedAccount; }
    public String getTransactionDate() { return transactionDate; }

    public String toJson() {
        return String.format("{\"id\":%d,\"accountId\":%d,\"type\":\"%s\",\"amount\":%.2f,\"description\":\"%s\",\"relatedAccount\":\"%s\",\"transactionDate\":\"%s\"}",
                id, accountId, type, amount,
                description != null ? description : "",
                relatedAccount != null ? relatedAccount : "",
                transactionDate);
    }
}
