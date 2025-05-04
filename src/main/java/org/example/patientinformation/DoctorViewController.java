package org.example.patientinformation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DoctorViewController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<PatientRecord> patientTable;

    @FXML
    private TableColumn<PatientRecord, String> nameColumn;

    @FXML
    private TableColumn<PatientRecord, String> emailColumn;

    @FXML
    private TableColumn<PatientRecord, Integer> ageColumn;

    @FXML
    private TableColumn<PatientRecord, String> genderColumn;

    private final ObservableList<PatientRecord> masterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        loadPatients();
    }

    private void loadPatients() {
        UserService service = new UserService();
        List<Map<String, Object>> data = service.getAllPatients();
        masterList.clear();

        for (Map<String, Object> entry : data) {
            String name = (String) entry.getOrDefault("name", "Unknown");
            String email = (String) entry.getOrDefault("email", "N/A");
            int age = Integer.parseInt(entry.getOrDefault("age", "0").toString());
            String gender = (String) entry.getOrDefault("gender", "");
            masterList.add(new PatientRecord(name, age, "N/A", "N/A", 0.0, "", email, gender));
        }

        patientTable.setItems(masterList);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            patientTable.setItems(masterList);
            return;
        }

        ObservableList<PatientRecord> filtered = FXCollections.observableArrayList();
        for (PatientRecord record : masterList) {
            if (record.getName().toLowerCase().contains(query) ||
                    record.getEmail().toLowerCase().contains(query)) {
                filtered.add(record);
            }
        }

        patientTable.setItems(filtered);
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadPatients();
    }

    @FXML
    private void handleViewDetails() {
        PatientRecord selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Patient Details");
            alert.setHeaderText(selected.getName());
            alert.setContentText("Email: " + selected.getEmail() +
                    "\nAge: " + selected.getAge() +
                    "\nGender: " + selected.getGender());
            alert.show();
        }
    }

    @FXML
    private void handleDeletePatient() {
        PatientRecord selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            UserService service = new UserService();
            boolean deleted = service.deleteUser(selected.getEmail());
            if (deleted) {
                masterList.remove(selected);
                patientTable.refresh();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Patient deleted successfully.", ButtonType.OK);
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete patient.", ButtonType.OK);
                alert.show();
            }
        }
    }
}
