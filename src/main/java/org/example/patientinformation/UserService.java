package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

    public List<Map<String, Object>> getAllPatients() {
        return null;
    }
}