package org.example.patientinformation;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.io.InputStream;

public class FirestoreContext {

    private static Firestore firestore;

    public static Firestore getFirestore() {
        if (firestore == null) {
            try {

                InputStream serviceAccount = FirestoreContext.class.getResourceAsStream("PatientInformationKey.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("Could not find Firebase key in resources!");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket("patientinformation-f40da.firebasestorage.app")
                        .build();

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
