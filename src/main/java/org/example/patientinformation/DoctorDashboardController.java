package org.example.patientinformation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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
    private PatientRecord selectedPatient;
    private final ObservableList<PatientRecord> allPatients = FXCollections.observableArrayList();
    private final UserService userService = new UserService();

    // ------------------------------------------------------------
    //  INITIALIZATION
    // ------------------------------------------------------------
    @FXML
    public void initialize() {
        loadLoggedInUserInfo();
        loadPatients();
        patientListView.setItems(allPatients);
        patientListView.setOnMouseClicked(this::onPatientSelected);
    }

    // ------------------------------------------------------------
    //  USER INFO & PROFILE IMAGE
    // ------------------------------------------------------------
    private void loadLoggedInUserInfo() {
        String name = LoggedInUser.getName();
        String email = LoggedInUser.getEmail();

        if (welcomeLabel != null) welcomeLabel.setText("Welcome back, Dr. " + name);
        if (nameLabel != null) nameLabel.setText(name);

        if (profileImageView != null) {
            Circle clip = new Circle(50, 50, 50);
            profileImageView.setClip(clip);
        }

        if (uploadLabel != null) uploadLabel.setVisible(true);
        if (imageContainer != null) imageContainer.setOnMouseClicked(this::onProfileImageClick);

        Map<String, Object> userData = userService.getUserByEmail(email);
        if (userData != null) {
            if (userData.get("profileImage") != null && profileImageView != null) {
                profileImageView.setImage(new Image(userData.get("profileImage").toString()));
                uploadLabel.setVisible(false);
            }
            if (userData.get("gender") != null && genderLabel != null)
                genderLabel.setText("Gender: " + userData.get("gender"));
            if (userData.get("age") != null && ageLabel != null)
                ageLabel.setText("Age: " + userData.get("age"));
            if (userData.get("height") != null && heightLabel != null)
                heightLabel.setText("Height: " + userData.get("height"));
            if (userData.get("weight") != null && weightLabel != null)
                weightLabel.setText("Weight: " + userData.get("weight"));
        }
    }

    private void onProfileImageClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (file != null) {
            try {
                String imageUrl = StorageService.uploadProfileImage(LoggedInUser.getEmail(), file);
                userService.updateUserField(LoggedInUser.getEmail(), "profileImage", imageUrl);
                profileImageView.setImage(new Image(imageUrl));
                uploadLabel.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    // Track which patient row was clicked
    private void onPatientSelected(MouseEvent event) {
        selectedPatient = patientListView.getSelectionModel().getSelectedItem();
    }

    // ADD PATIENT --------------------------------------------------
    @FXML
    private void onAddPatient(ActionEvent event) {
        patientInfoBox.getChildren().clear();

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email (unique)");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");

        TextField ageField = new TextField();
        ageField.setPromptText("Enter Age");

        TextField diagnosisField = new TextField();
        diagnosisField.setPromptText("Enter Diagnosis");

        Button saveButton = new Button("Save Patient");
        saveButton.setOnAction(e -> {
            try {
                String email = emailField.getText().trim().toLowerCase();
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String diagnosis = diagnosisField.getText().trim();

                if (email.isEmpty() || name.isEmpty()) {
                    showAlert("Error", "Email and Name are required.");
                    return;
                }

                // For simplicity, default medication, billing, etc.
                PatientRecord newPatient = new PatientRecord(
                        name, age, diagnosis, "N/A", 0.0, "N/A", email, "Unknown");

                boolean ok = userService.addPatient(newPatient);
                if (ok) {
                    showAlert("Success", "Patient added successfully.");
                    refreshPatients();
                    showMainDashboard();
                } else {
                    showAlert("Error", "Failed to add patient. Check logs.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid input for age.");
            }
        });

        Button goBackButton = createGoBackButton();

        patientInfoBox.getChildren().addAll(
                createBoldText("Add New Patient"),
                emailField, nameField, ageField, diagnosisField,
                saveButton, goBackButton
        );
    }

    // EDIT PATIENT -------------------------------------------------
    @FXML
    private void onEditPatient(ActionEvent event) {
        if (selectedPatient == null) {
            showAlert("Error", "Please select a patient to edit.");
            return;
        }

        patientInfoBox.getChildren().clear();

        TextField nameField = new TextField(selectedPatient.getName());
        TextField ageField = new TextField(String.valueOf(selectedPatient.getAge()));
        TextField diagnosisField = new TextField(selectedPatient.getDiagnosis());

        Button updateButton = new Button("Update Patient");
        updateButton.setOnAction(e -> {
            try {
                selectedPatient.setName(nameField.getText().trim());
                selectedPatient.setAge(Integer.parseInt(ageField.getText().trim()));
                selectedPatient.setDiagnosis(diagnosisField.getText().trim());

                boolean ok = userService.updatePatient(selectedPatient);
                if (ok) {
                    showAlert("Success", "Patient updated successfully.");
                    refreshPatients();
                    showMainDashboard();
                } else {
                    showAlert("Error", "Failed to update patient. Check logs.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid input for age.");
            }
        });

        Button goBackButton = createGoBackButton();

        patientInfoBox.getChildren().addAll(
                createBoldText("Edit Patient"),
                nameField, ageField, diagnosisField,
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

        Label welcomeText = new Label("Welcome back");
        welcomeText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox searchBox = new HBox(10, searchField, createButton("Search", this::onSearch));
        searchBox.setAlignment(Pos.CENTER_LEFT);

        refreshPatients();

        patientInfoBox.getChildren().addAll(
                welcomeText,
                createSearchBox(),
                patientListView
        );
    }
    private HBox createSearchBox() {
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(this::onSearch);
        HBox searchBox = new HBox(10, searchField, searchBtn);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        return searchBox;
    }



    private Text createBoldText(String content) {
        Text text = new Text(content);
        text.setFont(Font.font("Arial", 16));
        text.setStyle("-fx-font-weight: bold;");
        return text;
    }

    private Button createButton(String text, javafx.event.EventHandler<ActionEvent> evt) {
        Button btn = new Button(text);
        btn.setOnAction(evt);
        return btn;
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

    // LOGOUT (logic unchanged)
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
