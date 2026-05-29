package com.bank.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity // <-- Tells Spring this is a Database Table
public class Account {
    
    @Id // <-- Tells Spring this is the Primary Key (Unique ID)
    private String accountNumber;
    
    private String accountHolder;
    private BigDecimal balance;
    private String type;
    
    // <-- Tells Spring to create a separate database table just to store this history list!
    @ElementCollection(fetch = FetchType.EAGER) 
    private List<TransactionRecord> history = new ArrayList<>();

    // REQUIRED BY JPA DATABASE: An empty constructor
    public Account() {} 

    public Account(String a, String h, BigDecimal b, String t) {
        this.accountNumber = a;
        this.accountHolder = h;
        this.balance = b;
        this.type = t;
        // Record the initial deposit
        recordTransaction("OPENING_BALANCE", b, "Account Opened");
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public BigDecimal getBalance() { return balance; }
    public String getType() { return type; }
    public List<TransactionRecord> getHistory() { return history; }

    public synchronized void deposit(BigDecimal amt, String description) {
        this.balance = this.balance.add(amt);
        recordTransaction("DEPOSIT", amt, description);
    }

    public synchronized void withdraw(BigDecimal amt, String description) {
        if (this.balance.compareTo(amt) >= 0) {
            this.balance = this.balance.subtract(amt);
            recordTransaction("WITHDRAWAL", amt, description);
        } else {
            throw new RuntimeException("Insufficient funds");
        }
    }

    private void recordTransaction(String type, BigDecimal amount, String desc) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.history.add(new TransactionRecord(time, type, amount, desc));
    }

    @Embeddable // <-- Tells Spring that this custom object is safe to store in the database
    public static class TransactionRecord {
        public String date;
        public String type;
        public BigDecimal amount;
        public String description;

        // REQUIRED BY JPA DATABASE: An empty constructor
        public TransactionRecord() {}

        public TransactionRecord(String date, String type, BigDecimal amount, String description) {
            this.date = date;
            this.type = type;
            this.amount = amount;
            this.description = description;
        }
    }
}