package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Map;

public class MedicalRecordController {

    @FXML private TableView<MedicalRecord> recordTable;
    @FXML private TableColumn<MedicalRecord, String> dateColumn;
    @FXML private TableColumn<MedicalRecord, String> doctorColumn;
    @FXML private TableColumn<MedicalRecord, String> notesColumn;
    @FXML private TableColumn<MedicalRecord, String> prescriptionColumn;
    @FXML private Button addRecordBtn;
    @FXML private Button downloadBtn;

    private final ObservableList<MedicalRecord> records = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(cell -> cell.getValue().dateProperty());
        doctorColumn.setCellValueFactory(cell -> cell.getValue().doctorProperty());
        notesColumn.setCellValueFactory(cell -> cell.getValue().notesProperty());
        prescriptionColumn.setCellValueFactory(cell -> cell.getValue().prescriptionProperty());

        recordTable.setItems(records);
        loadMedicalRecords();

        addRecordBtn.setOnAction(e -> addTestRecord());
        downloadBtn.setOnAction(e -> handleDownload());
    }

    private void loadMedicalRecords() {
        Firestore db = FirestoreClient.getFirestore();
        String userEmail = LoggedInUser.getEmail();

        ApiFuture<QuerySnapshot> future = db.collection("users")
                .document(userEmail)
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

    private void addTestRecord() {
        Firestore db = FirestoreClient.getFirestore();
        String userEmail = LoggedInUser.getEmail();

        Map<String, Object> testData = Map.of(
                "date", "2025-05-07",
                "doctor", "Dr. Smith",
                "notes", "Annual checkup - all good.",
                "prescription", "Vitamin D"
        );

        db.collection("users")
                .document(userEmail)
                .collection("medical_records")
                .add(testData);

        loadMedicalRecords();
    }

    private void handleDownload() {
        MedicalRecord selected = recordTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a medical record to download.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Download");
        alert.setHeaderText(null);
        alert.setContentText("Downloading: " + selected.getDate() + " - " + selected.getDoctor());
        alert.showAndWait();
    }
}
