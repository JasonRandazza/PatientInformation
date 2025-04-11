package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.example.patientinformation.util.PasswordUtils;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private final Firestore db;

    public AuthService() {
        this.db = FirestoreContext.getFirestore();
    }

    // Sign-up a new user
    public boolean signUp(String email, String password, String name, String userType) {
        try {

            // Check if user exists
            DocumentReference userRef = db.collection("users").document(email);
            DocumentSnapshot snapshot = userRef.get().get();

            if (snapshot.exists()) {
                System.out.println("User already exists");
                return false;
            }

            // Hash the password
            String hashedPassword = PasswordUtils.hashPassword(password);

            // Create user document
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("password", hashedPassword);
            userData.put("userType", userType); // e.g., "patient", "doctor"

            ApiFuture<WriteResult> result = userRef.set(userData);
            System.out.println("User created at: " + result.get().getUpdateTime());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Authenticate login attempt
    public boolean login(String email, String password) {
        try {
            DocumentReference userRef = db.collection("users").document(email);
            DocumentSnapshot snapshot = userRef.get().get();

            if (!snapshot.exists()) {
                System.out.println("User does not exist");
                return false;
            }

            String storedHash = snapshot.getString("password");
            return PasswordUtils.verifyPassword(password, storedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get user type
    public String getUserType(String email) {
        try {
            DocumentReference userRef = db.collection("users").document(email);
            DocumentSnapshot snapshot = userRef.get().get();

            if (snapshot.exists()) {
                return snapshot.getString("userType");
            } else {
                System.out.println("User does not exist");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
