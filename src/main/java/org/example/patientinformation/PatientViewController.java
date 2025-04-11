package org.example.patientinformation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;


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
    private void handleLogout() {
        System.out.println("Logging out...");
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