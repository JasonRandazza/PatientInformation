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
        FirestoreContext.initialize();
        Firestore db = FirestoreContext.getDB();
        FXMLLoader fxmlLoader = new FXMLLoader(PatientInformationApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}