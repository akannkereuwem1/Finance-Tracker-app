package com.example.financeapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class IncomeFormController {

    @FXML private DatePicker dateField;
    @FXML private TextField sourceField;
    @FXML private TextField amountField;
    @FXML private TextArea notesField;
    @FXML private Label previewBalance;

    private DashboardController dashboardController; // ✅ should be DashboardController
    private int userId;

    // Correct setter
    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void initialize() {
        amountField.textProperty().addListener((obs, oldVal, newVal) ->
                previewBalance.setText(getPreviewBalance(newVal))
        );
    }

    private String getPreviewBalance(String newVal) {
        try {
            double currentBalance = Database.getBalance(userId);
            double entered = Double.parseDouble(newVal);
            return "Balance after adding: $" + String.format("%.2f", (currentBalance + entered));
        } catch (NumberFormatException e) {
            return "";
        }
    }

    @FXML
    protected void onSaveClick() {
        String sql = "INSERT INTO income(user_id, date, source, amount, notes) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, dateField.getValue().toString());
            pstmt.setString(3, sourceField.getText());
            pstmt.setDouble(4, Double.parseDouble(amountField.getText()));
            pstmt.setString(5, notesField.getText());
            pstmt.executeUpdate();

            if (dashboardController != null) dashboardController.refresh(); // ✅ now works

            onCancelClick();
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
