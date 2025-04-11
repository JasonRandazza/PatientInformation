package org.example.patientinformation;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class PatientLoginScreenController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private VBox emailContainer;

    @FXML
    private VBox passwordContainer;

    @FXML
    private HBox buttonContainer;

    @FXML
    public void initialize() {
        if (emailContainer != null) animateNode(emailContainer, 0);
        if (passwordContainer != null) animateNode(passwordContainer, 200);
        if (buttonContainer != null) animateNode(buttonContainer, 400);
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
    private void handleSignIn() {
        loadScene("PatientView.fxml");
    }

    @FXML
    private void handleRegister() {
            loadScene("SignUpScreen.fxml");
    }

    @FXML
    private void handleGoBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StartMenu.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to go back to Start Menu.");
        }
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load screen: " + fxmlFile);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
