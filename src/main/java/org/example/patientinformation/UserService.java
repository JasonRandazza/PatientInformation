package org.example.patientinformation;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;

import java.util.Map;

public class UserService {

    private final Firestore db;

    public UserService() {
        this.db = FirestoreContext.getFirestore();
    }

    // Get user details by name
    public Map<String, Object> getUserByName(String email) {
        try {
            DocumentReference userRef = db.collection("users").document(email);
            DocumentSnapshot snapshot = userRef.get().get();

            if (snapshot.exists()) {
                return snapshot.getData();
            } else {
                System.out.println("User not found");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Update a user field
    public boolean updateUserField(String email, String field, Object value) {
        try {
            DocumentReference userRef = db.collection("users").document(email);
            ApiFuture<WriteResult> future = userRef.update(field, value);
            System.out.println("Updated '" + field + "' at " + future.get().getUpdateTime());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update user field.");
            e.printStackTrace();
            return false;
        }
    }

    // Delete a user by name
    public boolean deleteUser(String email) {
        try {
            db.collection("users").document(email).delete().get();
            System.out.println("User " + email + " deleted.");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete user.");
            e.printStackTrace();
            return false;
        }
    }
    public Map<String, Object> getUserByEmail(String email) {
        try {
            DocumentReference userRef = db.collection("users").document(email);
            DocumentSnapshot snapshot = userRef.get().get();
            return snapshot.exists() ? snapshot.getData() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
