package org.example.patientinformation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DoctorDashboardController {

    // ------------------------------------------------------------
    //  FXML BINDINGS
    // ------------------------------------------------------------
    @FXML private Label welcomeLabel, nameLabel;
    @FXML private Label genderLabel, ageLabel, heightLabel, weightLabel;
    @FXML private ImageView profileImageView;
    @FXML private StackPane imageContainer;
    @FXML private Label uploadLabel;
    @FXML private ListView<PatientRecord> patientListView;
    @FXML private Button logoutButton, sendMessageButton, editPatientButton,
            addPatientButton, deletePatientButton,
            viewAppointmentsButton, viewBillingButton;
    @FXML private TextField searchField;
    @FXML private VBox patientInfoBox;

    // ------------------------------------------------------------
    //  STATE
    // ------------------------------------------------------------
    private PatientRecord selectedPatient = null;
    private final ObservableList<PatientRecord> allPatients = FXCollections.observableArrayList();
    private final UserService userService = new UserService();

    // ------------------------------------------------------------
    //  INITIALIZATION
    // ------------------------------------------------------------
    @FXML
    private void initialize() {
        patientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPatient = newVal;
        });
        refreshPatients();
        showMainDashboard();
    }
    // ------------------------------------------------------------
    //  PATIENT LIST LOADING / REFRESHING
    // ------------------------------------------------------------
    private void loadPatients() {
        allPatients.clear();
        List<Map<String, Object>> firebasePatients = userService.getAllPatients();
        for (Map<String, Object> data : firebasePatients) {
            String name = (String) data.getOrDefault("name", "N/A");
            int age = Integer.parseInt(data.getOrDefault("age", "0").toString());
            String diagnosis = data.getOrDefault("diagnosis", "N/A").toString();
            String medication = data.getOrDefault("medication", "N/A").toString();
            double billingAmt = Double.parseDouble(data.getOrDefault("billingAmount", "0").toString());
            String apptDate = data.getOrDefault("appointmentDate", "N/A").toString();
            String email = data.getOrDefault("email", "").toString();
            String gender = data.getOrDefault("gender", "Unknown").toString();

            PatientRecord record = new PatientRecord(name, age, diagnosis, medication, billingAmt,
                    apptDate, email, gender);
            allPatients.add(record);
        }
        patientListView.setItems(allPatients);
    }

    private void refreshPatients() {
        loadPatients();
        patientListView.refresh();
    }

    // ------------------------------------------------------------
    //  CRUD BUTTON HANDLERS
    // ------------------------------------------------------------

    // ADD PATIENT --------------------------------------------------
    @FXML
    private void onAddPatient(ActionEvent event) {
        patientInfoBox.getChildren().clear();

        TextField emailField = new TextField();
        emailField.setPromptText("Enter unique email address");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter full name");

        TextField ageField = new TextField();
        ageField.setPromptText("Enter patient age");

        TextField diagnosisField = new TextField();
        diagnosisField.setPromptText("Enter diagnosis details");

        TextField medicationField = new TextField();
        medicationField.setPromptText("Enter medications (comma-separated)");

        TextField billingField = new TextField();
        billingField.setPromptText("Enter billing amount (e.g., 200.00)");

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setPromptText("Select gender");

        DatePicker appointmentPicker = new DatePicker();
        appointmentPicker.setPromptText("Select appointment date");

        Button saveButton = new Button("Save Patient");
        saveButton.setOnAction(e -> {
            try {
                String email = emailField.getText().trim().toLowerCase();
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String diagnosis = diagnosisField.getText().trim();
                String medication = medicationField.getText().trim();
                double billingAmount = Double.parseDouble(billingField.getText().trim());
                String gender = genderBox.getValue() != null ? genderBox.getValue() : "Unknown";
                String appointmentDate = appointmentPicker.getValue() != null ? appointmentPicker.getValue().toString() : "N/A";

                if (email.isEmpty() || name.isEmpty()) {
                    showAlert("Error", "Email and Name are required.");
                    return;
                }

                PatientRecord newPatient = new PatientRecord(
                        name, age, diagnosis, medication, billingAmount, appointmentDate, email, gender);

                boolean ok = userService.addPatient(newPatient);
                if (ok) {
                    showAlert("Success", "Patient added successfully.");
                    refreshPatients();
                    showMainDashboard();
                } else {
                    showAlert("Error", "Failed to add patient. Check logs.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid input for age or billing amount.");
            }
        });

        Button goBackButton = createGoBackButton();

        patientInfoBox.getChildren().addAll(
                createBoldText("Add New Patient"),
                emailField, nameField, ageField, diagnosisField,
                medicationField, billingField,
                genderBox, appointmentPicker,
                saveButton, goBackButton
        );
    }



    // EDIT PATIENT -------------------------------------------------
    @FXML
    private void onEditPatient(ActionEvent event) {
        selectedPatient = patientListView.getSelectionModel().getSelectedItem();

        if (selectedPatient == null) {
            showAlert("Error", "Please select a patient to edit.");
            return;
        }

        patientInfoBox.getChildren().clear();

        TextField nameField = new TextField(selectedPatient.getName());
        nameField.setPromptText("Enter full name");

        TextField ageField = new TextField(String.valueOf(selectedPatient.getAge()));
        ageField.setPromptText("Enter patient age");

        TextField diagnosisField = new TextField(selectedPatient.getDiagnosis());
        diagnosisField.setPromptText("Enter diagnosis details");

        TextField medicationField = new TextField(selectedPatient.getMedications());
        medicationField.setPromptText("Enter medications (comma-separated)");

        TextField billingField = new TextField(String.valueOf(selectedPatient.getBillingAmount()));
        billingField.setPromptText("Enter billing amount (e.g., 200.00)");

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setValue(selectedPatient.getGender());
        genderBox.setPromptText("Select gender");

        DatePicker appointmentPicker = new DatePicker();
        appointmentPicker.setPromptText("Select appointment date");
        try {
            if (!selectedPatient.getAppointmentDate().equals("N/A")) {
                appointmentPicker.setValue(java.time.LocalDate.parse(selectedPatient.getAppointmentDate()));
            }
        } catch (Exception e) {
            appointmentPicker.setPromptText("Invalid format");
        }

        Button updateButton = new Button("Update Patient");
        updateButton.setOnAction(e -> {
            try {
                selectedPatient.setName(nameField.getText().trim());
                selectedPatient.setAge(Integer.parseInt(ageField.getText().trim()));
                selectedPatient.setDiagnosis(diagnosisField.getText().trim());
                selectedPatient.setMedications(medicationField.getText().trim());
                selectedPatient.setBillingAmount(Double.parseDouble(billingField.getText().trim()));
                selectedPatient.setGender(genderBox.getValue());
                selectedPatient.setAppointmentDate(
                        appointmentPicker.getValue() != null ? appointmentPicker.getValue().toString() : "N/A"
                );

                boolean ok = userService.updatePatient(selectedPatient);
                if (ok) {
                    showAlert("Success", "Patient updated successfully.");
                    refreshPatients();
                    showMainDashboard();
                } else {
                    showAlert("Error", "Failed to update patient. Check logs.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid input for age or billing amount.");
            }
        });

        Button goBackButton = createGoBackButton();

        patientInfoBox.getChildren().addAll(
                createBoldText("Edit Patient"),
                nameField, ageField, diagnosisField,
                medicationField, billingField,
                genderBox, appointmentPicker,
                updateButton, goBackButton
        );
    }


    // DELETE PATIENT ----------------------------------------------
    @FXML
    private void onDeletePatient(ActionEvent event) {
        if (selectedPatient == null) {
            showAlert("Error", "Please select a patient to delete.");
            return;
        }

        Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        deleteConfirmation.setTitle("Delete Patient");
        deleteConfirmation.setHeaderText("Are you sure you want to delete this patient?");
        deleteConfirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean ok = userService.deleteUser(selectedPatient.getEmail());
                if (ok) {
                    showAlert("Success", "Patient deleted.");
                    selectedPatient = null;
                    refreshPatients();
                    showMainDashboard();
                } else {
                    showAlert("Error", "Failed to delete patient. Check logs.");
                }
            }
        });
    }

    // ------------------------------------------------------------
    //  SEARCH, APPOINTMENTS, BILLING (unchanged logic)
    // ------------------------------------------------------------
    @FXML
    private void onSearch(ActionEvent event) {
        String searchQuery = searchField.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            refreshPatients();
            return;
        }
        ObservableList<PatientRecord> results = FXCollections.observableArrayList();
        for (PatientRecord p : allPatients) {
            if (p.getName().toLowerCase().contains(searchQuery) ||
                    p.getEmail().toLowerCase().contains(searchQuery)) {
                results.add(p);
            }
        }
        patientListView.setItems(results);
    }

    @FXML
    private void onViewAppointments(ActionEvent event) {
        if (selectedPatient == null) {
            showAlert("Error", "Please select a patient to view appointments.");
            return;
        }
        patientInfoBox.getChildren().clear();
        patientInfoBox.getChildren().addAll(
                createBoldText("Appointments for " + selectedPatient.getName()),
                new Text("Upcoming Appointment: " + selectedPatient.getAppointmentDate()),
                new Text("Diagnosis: " + selectedPatient.getDiagnosis()),
                createGoBackButton()
        );
    }

    @FXML
    private void onViewBilling(ActionEvent event) {
        if (selectedPatient == null) {
            showAlert("Error", "Please select a patient to view billing.");
            return;
        }
        patientInfoBox.getChildren().clear();
        patientInfoBox.getChildren().addAll(
                createBoldText("Billing for " + selectedPatient.getName()),
                new Text("Treatment Cost: $" + selectedPatient.getBillingAmount()),
                new Text("Diagnosis: " + selectedPatient.getDiagnosis()),
                createGoBackButton()
        );
    }

    // ------------------------------------------------------------
    //  UTILITIES
    // ------------------------------------------------------------
    private void showMainDashboard() {
        patientInfoBox.getChildren().clear();

        String email = LoggedInUser.getEmail();
        Map<String, Object> userData = userService.getUserByEmail(email);
        String fullName = userData != null ? (String) userData.getOrDefault("name", "") : "";
        String lastName = extractLastName(fullName);
        welcomeLabel.setText("Welcome, Dr. " + lastName);

        patientInfoBox.getChildren().addAll(
                welcomeLabel,
                createSearchBox(),
                patientListView
        );
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : parts[0];
    }

    private HBox createSearchBox() {
        Button searchButton = new Button("Search");
        searchButton.setOnAction(this::onSearch);
        return new HBox(10, searchField, searchButton);
    }

    private Text createBoldText(String content) {
        Text text = new Text(content);
        text.setFont(Font.font("Arial", 16));
        text.setStyle("-fx-font-weight: bold;");
        return text;
    }

    private Button createGoBackButton() {
        Button back = new Button("Go Back");
        back.setOnAction(e -> showMainDashboard());
        return back;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void onLogout(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Are you sure you want to log out?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/patientinformation/StartMenu.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = (Stage) logoutButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setTitle("Hospital Lab Home");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Unable to load home screen.");
                }
            }
        });
    }
}
