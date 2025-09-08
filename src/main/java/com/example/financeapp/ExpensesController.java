package com.example.financeapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ExpensesController {

    @FXML private TableView<Expense> expensesTable;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, Number> amountColumn;
    @FXML private TableColumn<Expense, String> notesColumn;

    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private int userId;


    public void setUserId(int userId) {   // 👈 add this setter
        this.userId = userId;
    }
    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        categoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        amountColumn.setCellValueFactory(data -> data.getValue().amountProperty());
        notesColumn.setCellValueFactory(data -> data.getValue().notesProperty());

        loadExpenses();
    }

    private void loadExpenses() {
        expenses.clear();
        String sql = "SELECT date, category, amount, notes FROM expenses WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Session.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getString("date"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("notes")
                ));
            }
            expensesTable.setItems(expenses);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddExpenseClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("expense-form.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Add Expense");
        stage.showAndWait();

        loadExpenses(); // refresh after adding
    }

    // Navigation
    @FXML protected void onDashboardClick() throws IOException { switchScene("dashboard-view.fxml"); }
    @FXML protected void onIncomeClick() throws IOException { switchScene("income-view.fxml"); }
    @FXML protected void onExpensesClick() throws IOException { switchScene("expenses-view.fxml"); }
    @FXML protected void onLogoutClick() throws IOException {
        Session.clear();
        switchScene("login-view.fxml");
    }

    private void switchScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Stage stage = (Stage) expensesTable.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }
}
