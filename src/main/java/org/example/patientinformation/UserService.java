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

    // Get user details by email
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

    // Get all users
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("users").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                users.add(doc.getData());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Get all patients
    public List<Map<String, Object>> getAllPatients() {
        List<Map<String, Object>> patients = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("users").whereEqualTo("userType", "Patient").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                Map<String, Object> data = doc.getData();
                data.put("email", doc.getId()); // include email for future operations
                patients.add(data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return patients;
    }

    // Add a new patient record
    public boolean addPatient(PatientRecord patient) {
        try {
            DocumentReference docRef = db.collection("users").document(patient.getEmail());
            Map<String, Object> data = Map.of(
                    "name", patient.getName(),
                    "age", patient.getAge(),
                    "diagnosis", patient.getDiagnosis(),
                    "medication", patient.getMedications(),
                    "billingAmount", patient.getBillingAmount(),
                    "appointmentDate", patient.getAppointmentDate(),
                    "gender", patient.getGender(),
                    "userType", "Patient"
            );
            docRef.set(data).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing patient record
    public boolean updatePatient(PatientRecord patient) {
        try {
            DocumentReference docRef = db.collection("users").document(patient.getEmail());
            Map<String, Object> updates = Map.of(
                    "name", patient.getName(),
                    "age", patient.getAge(),
                    "diagnosis", patient.getDiagnosis(),
                    "medication", patient.getMedications(),
                    "billingAmount", patient.getBillingAmount(),
                    "appointmentDate", patient.getAppointmentDate(),
                    "gender", patient.getGender()
            );
            docRef.update(updates).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
