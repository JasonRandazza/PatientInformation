package org.example.patientinformation;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class FirestoreContext {

    private static Firestore firestore;

    public static Firestore getFirestore() {
        if (firestore == null) {
            try {

                FileInputStream serviceAccount =
                        new FileInputStream("src/main/resources/aydin/firebasedemo/key.json");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                //Initialize FirebaseApp if not already initialized
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }

                firestore = FirestoreClient.getFirestore();

            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Failed to initialize Firestore", ex);
            }
        }
        return firestore;
    }
}
