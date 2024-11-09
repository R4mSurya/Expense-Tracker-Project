package me.ram.triptracker;

public class Expense {
    private String expenseId;
    private String expenseName;
    private double amount;
    private String category;
    private String date;

    // Default constructor required for Firebase
    public Expense() {}

    // Constructor with parameters
    public Expense(String expenseId, String expenseName, double amount, String category, String date) {
        this.expenseId = expenseId;
        this.expenseName = expenseName;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return expenseId;
    }

    public void setId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
