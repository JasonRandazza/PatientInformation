package org.example.patientinformation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class PatientLoginScreenController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignIn(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Basic debug message
        System.out.println("Sign In clicked. Email: " + email + ", Password: " + password);

        // TODO: Add sign-in logic here (e.g., AuthService.login(email, password))
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Basic debug message
        System.out.println("Register clicked. Email: " + email + ", Password: " + password);

        // TODO: Add registration logic here (e.g., AuthService.register(email, password))
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/startmenu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Hospital App - Start Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
