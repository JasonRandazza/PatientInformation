package org.example.patientinformation;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML private VBox nameContainer;
    @FXML private VBox emailContainer;
    @FXML private VBox passwordContainer;
    @FXML private VBox roleContainer;

    @FXML
    private void initialize() {
        animateNode(nameContainer, 0);
        animateNode(emailContainer, 100);
        animateNode(passwordContainer, 200);
        animateNode(roleContainer, 300);


    }
    private void animateNode(Node node, int delay) {
        node.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
        tt.setFromY(30);
        tt.setToY(0);
        tt.setDelay(Duration.millis(delay));
        tt.setInterpolator(Interpolator.EASE_OUT);
        tt.play();

        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(delay));
        ft.play();
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
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/patientinformation/PatientScreen.fxml"));
            Scene scene = new Scene(root, 900, 600);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load login screen.");
        }
    }


    private void showError(String msg) {
        messageLabel.setText(msg);
    }
}
