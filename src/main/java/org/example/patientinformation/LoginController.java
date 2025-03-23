package org.example.patientinformation;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void onLoginClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if(email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill out all fields.");
            return;
        }

        boolean success = AuthService.authenticate(email, password);
        if(success) {
            messageLabel.setText("login successful");
            //TODO: Load the patient dashboard scene
        }
        else {
            messageLabel.setText("Invalid email or password.");
        }
    }
}
