package org.example.patientinformation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class PatientViewController {
    @FXML
    private StackPane mainContent;

    @FXML
    public void initialize() {
        loadView("home-view.fxml");
    }

    @FXML
    private void handleHome() {
        loadView("home-view.fxml");
    }

    @FXML
    private void handleBillings() {
        loadView("billing-view.fxml");
    }

    @FXML
    private void handleAppointments() {
        loadView("appointments-view.fxml");
    }

    @FXML
    private void handleRecords() {
        loadView("medical-records-view.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirmLogout = new Alert(Alert.AlertType.CONFIRMATION);
        confirmLogout.setTitle("Confirm Logout");
        confirmLogout.setHeaderText("Are you sure you want to log out?");
        confirmLogout.setContentText("Press OK to log out or Cancel to stay.");

        Optional<ButtonType> result = confirmLogout.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/org/example/patientinformation/StartMenu.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 755, 750));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String VIEW_BASE_PATH = "/org/example/patientinformation/";

    private void loadView(String viewFile) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(VIEW_BASE_PATH + viewFile));
            mainContent.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
