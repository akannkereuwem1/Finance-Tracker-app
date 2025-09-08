package com.example.financeapp;

import javafx.beans.property.*;

public class Expense {
    private final StringProperty date;
    private final StringProperty category;
    private final DoubleProperty amount;
    private final StringProperty notes;

    public Expense(String date, String category, double amount, String notes) {
        this.date = new SimpleStringProperty(date);
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleDoubleProperty(amount);
        this.notes = new SimpleStringProperty(notes);
    }

    public StringProperty dateProperty() { return date; }
    public StringProperty categoryProperty() { return category; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty notesProperty() { return notes; }
}
