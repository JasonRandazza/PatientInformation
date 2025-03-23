package org.example.patientinformation;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.example.patientinformation.util.PasswordUtils;
import java.util.HashMap;
import java.util.Map;

public class PatientService {

    public static void createPatientAccount(String email, String password, String name) {
        Firestore db = FirestoreContext.getDB();
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("email", email);

        //Store the hashed password - NEVER store plain text passwords!
        patientData.put("password", PasswordUtils.hashPassword(password));
        patientData.put("name", name);

        DocumentReference docRef = db.collection("patients").document(email);
        ApiFuture<WriteResult> result = docRef.set(patientData);
        try {
            System.out.println("Account created at: " + result.get().getUpdateTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
