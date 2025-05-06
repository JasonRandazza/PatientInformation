package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserService {

    private final Firestore db;

    public UserService() {
        this.db = FirestoreContext.getFirestore();
    }

    // Get user details by email (document id)
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

    // Get all users (any userType)
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("users").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                Map<String, Object> data = doc.getData();
                data.put("email", doc.getId()); // expose doc id as email
                users.add(data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Get only patients (userType == "Patient")
    public List<Map<String, Object>> getAllPatients() {
        List<Map<String, Object>> patients = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("users")
                    .whereEqualTo("userType", "Patient")
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                Map<String, Object> data = doc.getData();
                data.put("email", doc.getId()); // include email for future ops
                patients.add(data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return patients;
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

    // Update a user field
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

    // Add a new patient (creates a user document with userType = "Patient")
    public boolean addPatient(PatientRecord patient) {
        try {
            String email = patient.getEmail(); // must be unique (used as doc id)
            if (email == null || email.isEmpty()) {
                System.err.println("[addPatient] Email is required for a patient record.");
                return false;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("name", patient.getName());
            data.put("age", patient.getAge());
            data.put("diagnosis", patient.getDiagnosis());
            data.put("medication", patient.getMedications());
            data.put("billingAmount", patient.getBillingAmount());
            data.put("appointmentDate", patient.getAppointmentDate());
            data.put("gender", patient.getGender());
            data.put("userType", "Patient");

            // "set" will create / overwrite
            db.collection("users").document(email).set(data).get();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to add patient.");
            e.printStackTrace();
            return false;
        }
    }

    // Update existing patient (partial overwrite)
    public boolean updatePatient(PatientRecord patient) {
        try {
            String email = patient.getEmail();
            if (email == null || email.isEmpty()) {
                System.err.println("[updatePatient] Email is required for a patient record.");
                return false;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", patient.getName());
            updates.put("age", patient.getAge());
            updates.put("diagnosis", patient.getDiagnosis());
            updates.put("medication", patient.getMedications());
            updates.put("billingAmount", patient.getBillingAmount());
            updates.put("appointmentDate", patient.getAppointmentDate());
            updates.put("gender", patient.getGender());

            db.collection("users").document(email).update(updates).get();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update patient.");
            e.printStackTrace();
            return false;
        }
    }

    // Update profile image
    public boolean updateProfileImageUrl(String email, String imageUrl) {
        try {
            db.collection("users").document(email).update("profileImage", imageUrl).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
