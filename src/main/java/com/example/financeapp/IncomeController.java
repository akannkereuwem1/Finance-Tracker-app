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

public class IncomeController {

    @FXML private TableView<Income> incomeTable;
    @FXML private TableColumn<Income, String> dateColumn;
    @FXML private TableColumn<Income, String> sourceColumn;
    @FXML private TableColumn<Income, Number> amountColumn;
    @FXML private TableColumn<Income, String> notesColumn;

    private final ObservableList<Income> incomes = FXCollections.observableArrayList();
    private int userId;



    public void setUserId(int userId) {   // 👈 add this setter
        this.userId = userId;
    }
    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        sourceColumn.setCellValueFactory(data -> data.getValue().sourceProperty());
        amountColumn.setCellValueFactory(data -> data.getValue().amountProperty());
        notesColumn.setCellValueFactory(data -> data.getValue().notesProperty());

        loadIncome();
    }

    private void loadIncome() {
        incomes.clear();
        String sql = "SELECT date, source, amount, notes FROM income WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Session.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                incomes.add(new Income(
                        rs.getString("date"),
                        rs.getString("source"),
                        rs.getDouble("amount"),
                        rs.getString("notes")
                ));
            }
            incomeTable.setItems(incomes);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddIncomeClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("income-form.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Add Income");
        stage.showAndWait();

        loadIncome(); // refresh after adding
    }

    // Navigation
    @FXML protected void onDashboardClick() throws IOException { switchScene("dashboard-view.fxml"); }
    @FXML protected void onExpensesClick() throws IOException { switchScene("expenses-view.fxml"); }
    @FXML protected void onIncomeClick() throws IOException { switchScene("income-view.fxml"); }
    @FXML protected void onLogoutClick() throws IOException {
        Session.clear();
        switchScene("login-view.fxml");
    }

    private void switchScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Stage stage = (Stage) incomeTable.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }
}
