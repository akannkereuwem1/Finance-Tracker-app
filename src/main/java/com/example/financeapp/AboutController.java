package com.example.financeapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AboutController {

    @FXML private ImageView profileImage;

    private int userId;
    private String username;

    // Setter to receive user data
    public void setUser(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @FXML
    private void onBackToDashboardClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();

            // Get the dashboard controller and pass user info back
            DashboardController controller = loader.getController();
            controller.setUser(userId, username);

            Stage stage = (Stage) profileImage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/profile.jpg"));
            profileImage.setImage(img);
        } catch (Exception e) {
            System.out.println("⚠️ Could not load image. Place profile.jpg in resources/images/");
        }
    }
}
