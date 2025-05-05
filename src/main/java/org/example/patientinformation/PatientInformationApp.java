package org.example.patientinformation;

import com.google.cloud.firestore.Firestore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PatientInformationApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PatientInformationApp.class.getResource("StartMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 750);
        stage.setTitle("Hospital App");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        try {
            Firestore db = FirestoreContext.getFirestore();
            System.out.println("Firestore connected. Project ID: " + db.getOptions().getProjectId());
        } catch (Exception e) {
            System.err.println("Failed to connect to Firestore: ");
            e.printStackTrace();
        }
        launch(args);
    }
}