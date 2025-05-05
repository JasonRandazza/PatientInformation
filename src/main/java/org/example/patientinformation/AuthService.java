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
            DocumentReference userRef = db.collection("users").document(email);
            DocumentSnapshot snapshot = userRef.get().get();

            if (snapshot.exists()) {
                System.out.println("User already exists");
                return false;
            }

            String hashedPassword = PasswordUtils.hashPassword(password);

            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("password", hashedPassword);
            userData.put("userType", userType);
            userData.put("name", name);

            ApiFuture<WriteResult> result = userRef.set(userData);
            System.out.println("User created at: " + result.get().getUpdateTime());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String email, String password, String expectedUserType) {
        try {
            DocumentReference userRef = db.collection("users").document(email);
            DocumentSnapshot snapshot = userRef.get().get();

            if (!snapshot.exists()) {
                System.out.println("User does not exist");
                return false;
            }

            String storedHash = snapshot.getString("password");
            String actualUserType = snapshot.getString("userType");

            if (storedHash == null || actualUserType == null) {
                System.out.println("Invalid account data.");
                return false;
            }

            if (!expectedUserType.equalsIgnoreCase(actualUserType)) {
                System.out.println("Unauthorized login: expected " + expectedUserType + ", but found " + actualUserType);
                return false;
            }

            return PasswordUtils.verifyPassword(password, storedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean loginWithoutRoleCheck(String email, String password) {
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
    public boolean updateProfileImageUrl(String email, String imageUrl) {
        try {
            DocumentReference userRef = FirestoreContext.getFirestore().collection("users").document(email);
            ApiFuture<WriteResult> future = userRef.update("profileImage", imageUrl);
            System.out.println("Profile image URL updated at: " + future.get().getUpdateTime());
            return true;
        } catch (Exception e) {
            System.err.println("Error updating profile image URL:");
            e.printStackTrace();
            return false;
        }
    }

}
