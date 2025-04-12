package org.example.patientinformation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SignUpController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> userTypeChoice;
    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        // Populate the user type dropdown
        userTypeChoice.setValue("Patient"); // Default selection
    }

    @FXML
    private void onSignUpClick() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();
        String userType = userTypeChoice.getValue();

        if(email.isEmpty() || password.isEmpty() || name.isEmpty() || userType == null) {
            showError("All fields are required.");
            return;
        }

        boolean success = authService.signUp(email, password, name, userType);

        if (success) {
            try {
                Stage stage = (Stage) emailField.getScene().getWindow();
                FXMLLoader loader;

                if ("doctor".equalsIgnoreCase(userType)) {
                    loader = new FXMLLoader(getClass().getResource("DoctorScreen.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("PatientScreen.fxml"));
                }

                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.setTitle(userType + " Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to load user screen.");
            }
        } else {
            showError("Sign-up failed. User may already exist.");
        }
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
    }
}
