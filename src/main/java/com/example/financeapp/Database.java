package com.example.financeapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:sqlite:financeapp.db";

    // ------------------ CONNECTION ------------------
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ------------------ INITIALIZATION ------------------
    public static void initializeDatabase() {
        String usersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            );
        """;

        String expensesTable = """
            CREATE TABLE IF NOT EXISTS expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                category TEXT NOT NULL,
                amount REAL NOT NULL,
                notes TEXT,
                user_id INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
        """;

        String incomeTable = """
            CREATE TABLE IF NOT EXISTS income (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                source TEXT NOT NULL,
                amount REAL NOT NULL,
                notes TEXT,
                user_id INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(expensesTable);
            stmt.execute(incomeTable);
            System.out.println("All tables ready.");
        } catch (SQLException e) {
            System.out.println("Table creation failed: " + e.getMessage());
        }
    }

    // ------------------ EXPENSES ------------------
    public static void addExpense(String date, String category, double amount, String notes, int userId) {
        String sql = "INSERT INTO expenses (date, category, amount, notes, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, notes);
            pstmt.setInt(5, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Expense> getAllExpenses(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT date, category, amount, notes FROM expenses WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getString("date"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    // ------------------ INCOME ------------------
    public static void addIncome(String date, String source, double amount, String notes, int userId) {
        String sql = "INSERT INTO income (date, source, amount, notes, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.setString(2, source);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, notes);
            pstmt.setInt(5, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ BALANCE ------------------
    public static double getBalance(int userId) {
        double income = 0, expenses = 0;
        String incomeSql = "SELECT COALESCE(SUM(amount), 0) AS total FROM income WHERE user_id = ?";
        String expensesSql = "SELECT COALESCE(SUM(amount), 0) AS total FROM expenses WHERE user_id = ?";

        try (Connection conn = connect();
             PreparedStatement inStmt = conn.prepareStatement(incomeSql);
             PreparedStatement exStmt = conn.prepareStatement(expensesSql)) {

            inStmt.setInt(1, userId);
            try (ResultSet rsIncome = inStmt.executeQuery()) {
                if (rsIncome.next()) income = rsIncome.getDouble("total");
            }

            exStmt.setInt(1, userId);
            try (ResultSet rsExpenses = exStmt.executeQuery()) {
                if (rsExpenses.next()) expenses = rsExpenses.getDouble("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return income - expenses;
    }
}
