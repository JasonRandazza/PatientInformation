package org.example.patientinformation;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.example.patientinformation.util.PasswordUtils;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    public static void createPatientAccount(String email, String password, String name, String role) {
        Firestore db = FirestoreContext.getDB();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);

        //Store the hashed password - NEVER store plain text passwords!
        userData.put("password", PasswordUtils.hashPassword(password));
        userData.put("name", name);
        userData.put("role", role.toLowerCase());

        //Using a generic "users" collection
        DocumentReference docRef = db.collection("users").document(email);
        ApiFuture<WriteResult> result = docRef.set(userData);
        try {
            System.out.println("Account created at: " + result.get().getUpdateTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
