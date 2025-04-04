package org.example.patientinformation;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;

public class PatientLoginScreenController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleSignIn(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        System.out.println("Sign In clicked. Email: " + email + ", Password: " + password);
        // TODO: Call AuthService to verify credentials
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        System.out.println("Register clicked. Email: " + email + ", Password: " + password);
        // TODO: Call AuthService to register new user
    }
}
