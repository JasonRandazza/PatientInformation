package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class DoctorMedicalRecordController {

    @FXML private TableView<MedicalRecord> recordTable;
    @FXML private TableColumn<MedicalRecord, String> dateColumn;
    @FXML private TableColumn<MedicalRecord, String> doctorColumn;
    @FXML private TableColumn<MedicalRecord, String> notesColumn;
    @FXML private TableColumn<MedicalRecord, String> prescriptionColumn;

    @FXML private Button addRecordBtn;     // Will be hidden
    @FXML private Button downloadBtn;      // Optional: leave or hide

    private final ObservableList<MedicalRecord> records = FXCollections.observableArrayList();
    private String patientEmail;

    public void setPatientEmail(String email) {
        this.patientEmail = email;
        loadMedicalRecords();
    }

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(cell -> cell.getValue().dateProperty());
        doctorColumn.setCellValueFactory(cell -> cell.getValue().doctorProperty());
        notesColumn.setCellValueFactory(cell -> cell.getValue().notesProperty());
        prescriptionColumn.setCellValueFactory(cell -> cell.getValue().prescriptionProperty());

        recordTable.setItems(records);

        // Hide Add/Download buttons for doctor
        if (addRecordBtn != null) addRecordBtn.setVisible(false);
        if (downloadBtn != null) downloadBtn.setVisible(false);
    }

    private void loadMedicalRecords() {
        if (patientEmail == null || patientEmail.isEmpty()) return;

        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("users")
                .document(patientEmail)
                .collection("medical_records")
                .get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            records.clear();
            for (QueryDocumentSnapshot doc : documents) {
                String date = doc.getString("date");
                String doctor = doc.getString("doctor");
                String notes = doc.getString("notes");
                String prescription = doc.getString("prescription");
                records.add(new MedicalRecord(date, doctor, notes, prescription));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
