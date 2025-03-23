package org.example.patientinformation;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class FirestoreContext {

    private static Firestore db;

    public static void initialize() {
        if (db != null) return;

        try {

            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/org/example/patientinformation/PatientInformationKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore();

            System.out.println("Firebase initialized successfully.");

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    public static Firestore getDB() {
        return db;
    }
}
