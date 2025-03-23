package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.example.patientinformation.util.PasswordUtils;

public class AuthService {

    public static boolean authenticate(String email, String password) {
        Firestore db = FirestoreContext.getDB();
        ApiFuture<DocumentSnapshot> future = db.collection("patients").document(email).get();
        try {
            DocumentSnapshot doc = future.get();
            if (!doc.exists()) {
                return false;
            }
            String storedHashed = doc.getString("password");
            String providedHashed = PasswordUtils.hashPassword(password);
            return storedHashed != null && storedHashed.equals(providedHashed);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
