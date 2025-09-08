package com.example.financeapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void onRegisterClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 200);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Register");
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (validateLogin(user, pass)) {
            System.out.println("Login successful!");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardController controller = loader.getController();
            controller.setUser(Session.getUserId(), Session.getUsername());



            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");

        } else {
            System.out.println("❌ Invalid credentials.");
        }
    }

    private boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // store logged-in user in Session
                Session.setUser(rs.getInt("id"), rs.getString("username"));
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Login check error: " + e.getMessage());
            return false;
        }
    }
}
