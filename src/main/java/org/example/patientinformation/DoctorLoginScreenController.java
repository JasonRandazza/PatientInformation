package org.example.patientinformation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class DoctorLoginScreenController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    // This method is triggered when the login button is clicked
    public void onLogin(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Check if any of the fields are empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required!");
        } else {
            showAlert("Login Successful", "Welcome, Dr. " + username + "!");
            // Redirect to the Doctor Dashboard after successful login
            try {
                // Load the Doctor Dashboard FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/patientinformation/DoctorDashboard.fxml"));
                Parent root = loader.load();

                // Get the current stage (window) and set the new scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 750, 750));
                stage.show();
            } catch (IOException e) {
                showAlert("Error", "Unable to load Doctor Dashboard.");
                e.printStackTrace();
            }
        }
    }

    // This method is triggered when the "Go Back" button is clicked
    public void onGoBack(ActionEvent event) {
        try {
            // Load the Start Menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/patientinformation/StartMenu.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 750, 750));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load Start Menu.");
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
