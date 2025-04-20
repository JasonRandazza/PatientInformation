package org.example.patientinformation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void onLoginClick() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if(email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill out all fields.");
            return;
        }

        boolean loginSuccess = authService.login(email, password);
        if(loginSuccess) {
            String userType = authService.getUserType(email);
            String name = new UserService().getUserByEmail(email).get("name").toString();
            LoggedInUser.setName(name);
            try {
                Stage stage = (Stage) emailField.getScene().getWindow();
                FXMLLoader loader;

                if ("doctor".equalsIgnoreCase(userType)) {
                    loader = new FXMLLoader(getClass().getResource("DoctorScreen.fxml"));
                } else if ("patient".equalsIgnoreCase(userType)) {
                    loader = new FXMLLoader(getClass().getResource("PatientScreen.fxml"));
                } else {
                    showAlert("Error", "Unknown user type.");
                    return;
                }

                Scene newScene = new Scene(loader.load(), 750, 750);
                stage.setScene(newScene);
                stage.setTitle(userType + " Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Could not load the user screen.");
            }
        } else {
            showAlert("Error", "Invalid email or password.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
