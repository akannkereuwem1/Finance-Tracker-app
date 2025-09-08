package com.example.financeapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label balanceLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpensesLabel;

    @FXML private TableView<Expense> expensesTable;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, Double> amountColumn;
    @FXML private TableColumn<Expense, String> notesColumn;

    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private int userId = 0;

    @FXML
    public void initialize() {
        // Table column bindings
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        categoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        amountColumn.setCellValueFactory(data -> data.getValue().amountProperty().asObject());
        notesColumn.setCellValueFactory(data -> data.getValue().notesProperty());


        if (Session.getUserId() > 0) {
            setUser(Session.getUserId(), Session.getUsername());
        }
    }

    /** Set the logged-in user explicitly */
    public void setUser(int userId, String username) {
        this.userId = userId;
        if (username != null && !username.isBlank()) {
            welcomeLabel.setText("Welcome, " + username + "!");
        }
        refresh();
    }

    /** Resolve a valid userId (prefer field, fallback to Session) */
    private int requireUserId() {
        int uid = (userId > 0) ? userId : Session.getUserId();
        if (uid <= 0) {
            System.err.println("DashboardController: userId is not set. Did you call setUser(...) after login?");
        }
        return uid;
    }

    /** Reload table + summary */
    public void refresh() {
        loadExpenses();
        updateSummary();
    }

    /** Load expenses for this user only */
    private void loadExpenses() {
        expenses.clear();
        int uid = requireUserId();
        if (uid <= 0) return;

        String sql = "SELECT date, category, amount, notes " +
                "FROM expenses WHERE user_id = ? ORDER BY id DESC";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, uid);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(new Expense(
                            rs.getString("date"),
                            rs.getString("category"),
                            rs.getDouble("amount"),
                            rs.getString("notes")
                    ));
                }
            }
            expensesTable.setItems(expenses);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Recompute totals for the current user */
    public void updateSummary() {
        int uid = requireUserId();
        if (uid <= 0) return;

        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        String incomeSql   = "SELECT COALESCE(SUM(amount),0) AS total FROM income   WHERE user_id = ?";
        String expensesSql = "SELECT COALESCE(SUM(amount),0) AS total FROM expenses WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement in = conn.prepareStatement(incomeSql);
             PreparedStatement ex = conn.prepareStatement(expensesSql)) {

            in.setInt(1, uid);
            ex.setInt(1, uid);

            try (ResultSet ri = in.executeQuery()) {
                if (ri.next()) totalIncome = ri.getDouble("total");
            }
            try (ResultSet re = ex.executeQuery()) {
                if (re.next()) totalExpenses = re.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        double balance = totalIncome - totalExpenses;

        totalIncomeLabel.setText(String.format("$%.2f", totalIncome));
        totalExpensesLabel.setText(String.format("$%.2f", totalExpenses));
        balanceLabel.setText(String.format("$%.2f", balance));
        balanceLabel.setStyle(balance >= 0
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                : "-fx-text-fill: #c0392b; -fx-font-weight: bold;");
    }

    /** Open the Add Expense form */
    @FXML
    protected void onAddExpenseClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("expense-form.fxml"));
            Scene scene = new Scene(loader.load());

            ExpenseFormController controller = loader.getController();
            controller.setDashboardController(this);
            controller.setUserId(requireUserId());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add Expense");
            stage.showAndWait();

            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Sidebar navigation */
    @FXML protected void onIncomeClick()  throws IOException { switchScene("income-view.fxml"); }
    @FXML protected void onExpensesClick() throws IOException { switchScene("expenses-view.fxml"); }
    @FXML
    protected void onAboutClick() throws IOException {
        switchScene("about-view.fxml");
    }

    @FXML
    protected void onLogoutClick() {
        try {
            // Clear in-memory session when logging out
            Session.clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Centralized scene switcher that passes userId forward */
    private void switchScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Scene scene = new Scene(loader.load());
        Object controller = loader.getController();

        int uid = requireUserId();

        if (controller instanceof IncomeController) {
            ((IncomeController) controller).setUserId(uid);
        } else if (controller instanceof ExpensesController) {
            ((ExpensesController) controller).setUserId(uid);
        }

        Stage stage = (Stage) expensesTable.getScene().getWindow();
        stage.setScene(scene);
    }
}
