package org.example.patientinformation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class StartMenuController {

    @FXML
    private void handleDoctorButton(ActionEvent event) throws IOException {
        switchScene(event, "DoctorScreen.fxml");
    }

    @FXML
    private void handlePatientButton(ActionEvent event) throws IOException {
        switchScene(event, "PatientScreen.fxml");
    }



    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 750, 750));
        stage.show();
    }
}
