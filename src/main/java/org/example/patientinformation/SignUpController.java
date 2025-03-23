package org.example.patientinformation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SignUpController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private Label messageLabel;

    @FXML
    private void onSignUpClick() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();

        if(email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            messageLabel.setText("Please fill out all fields.");
            return;
        }

        PatientService.createPatientAccount(email, password, name);
        messageLabel.setText("Account created! Please log in.");
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
}
