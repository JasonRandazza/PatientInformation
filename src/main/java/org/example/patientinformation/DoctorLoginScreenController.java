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
import java.util.Map;

public class DoctorLoginScreenController {


    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;


    @FXML
    public void onLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both email and password.");
            return;
        }

        AuthService authService = new AuthService();
        boolean isAuthenticated = authService.login(email, password, "Doctor");

        if (!isAuthenticated) {
            showAlert("Login Failed", "Incorrect credentials or unauthorized role.");
            return;
        }

        Map<String, Object> userData = new UserService().getUserByEmail(email);
        LoggedInUser.setName(userData != null && userData.get("name") != null ? userData.get("name").toString() : "Doctor");
        LoggedInUser.setEmail(email);


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/patientinformation/doctor-view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600); // match dimensions in FXML
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Doctor Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load Doctor Dashboard.");
        }
    }


    @FXML
    public void onGoBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/patientinformation/StartMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 750, 750));
            stage.setTitle("Start Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load Start Menu.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 750, 750));
            stage.setTitle("Doctor Registration");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load Sign Up screen.");
        }
    }
}
