package com.example.financeapp;

import javafx.beans.property.*;

public class Income {
    private final StringProperty date;
    private final StringProperty source;
    private final DoubleProperty amount;
    private final StringProperty notes;

    public Income(String date, String source, double amount, String notes) {
        this.date = new SimpleStringProperty(date);
        this.source = new SimpleStringProperty(source);
        this.amount = new SimpleDoubleProperty(amount);
        this.notes = new SimpleStringProperty(notes);
    }

    public StringProperty dateProperty() { return date; }
    public StringProperty sourceProperty() { return source; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty notesProperty() { return notes; }
}
