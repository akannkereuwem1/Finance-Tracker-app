package com.example.financeapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static com.example.financeapp.Session.getUserId;

public class ExpenseFormController {

    @FXML private DatePicker dateField;
    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private TextArea notesField;
    @FXML private Label previewBalance;


    private DashboardController dashboardController;
    private int userId;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setUserId(int userId) {   // 👈 add this setter
        this.userId = userId;
    }

    @FXML
    private void initialize() {
        // Live preview balance
        amountField.textProperty().addListener((obs, oldVal, newVal) ->
                previewBalance.setText(getPreviewBalance(newVal))
        );
    }

    private String getPreviewBalance(String newVal) {
        try {
            double currentBalance = Database.getBalance(getUserId());
            double entered = Double.parseDouble(newVal);
            return "Balance after spending: $" + String.format("%.2f", (currentBalance - entered));
        } catch (NumberFormatException e) {
            return "";
        }
    }

    @FXML
    protected void onSaveClick() {
        String sql = "INSERT INTO expenses(user_id, date, category, amount, notes) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, dateField.getValue().toString());
            pstmt.setString(3, categoryField.getText());
            pstmt.setDouble(4, Double.parseDouble(amountField.getText()));
            pstmt.setString(5, notesField.getText());
            pstmt.executeUpdate();

            if (dashboardController != null) dashboardController.updateSummary();

            onCancelClick(); // close window
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCancelClick() {
        Stage stage = (Stage) dateField.getScene().getWindow();
        stage.close();
    }
}