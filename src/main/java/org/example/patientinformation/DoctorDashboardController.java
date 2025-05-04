package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class DoctorDashboardController {


    @FXML
    private Label welcomeLabel, nameLabel;

    @FXML
    private Label genderLabel, ageLabel, heightLabel, weightLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private StackPane imageContainer;

    @FXML
    private Label uploadLabel;

    @FXML
    private ListView<PatientRecord> patientListView;

    @FXML
    private Button logoutButton;

    @FXML
    private Button sendMessageButton;

    @FXML
    private Button editPatientButton;

    @FXML
    private Button addPatientButton;

    @FXML
    private Button deletePatientButton;

    @FXML
    private Button viewAppointmentsButton;

    @FXML
    private Button viewBillingButton;

    @FXML
    private TextField searchField;

    @FXML
    private VBox patientInfoBox;


    private PatientRecord selectedPatient;

    private ObservableList<PatientRecord> allPatients = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadLoggedInUserInfo();
    }
    @FXML
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

        if (imageContainer != null) {
            imageContainer.setOnMouseClicked(this::onProfileImageClick);
        }

        Map<String, Object> userData = new UserService().getUserByEmail(email);
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

   /* @FXML
    private void loadPatients() {
        allPatients.clear();
        UserService userService = new UserService();

        List<Map<String, Object>> firebasePatients = userService.getAllPatients();
        Map<String, Object> userData = new UserService().getUserByEmail(LoggedInUser.getEmail());

        String gender = userData != null && userData.get("gender") != null
                ? userData.get("gender").toString()
                : "Unknown";

        for (Map<String, Object> data : firebasePatients) {
            String name = (String) data.getOrDefault("name", "N/A");
            int age = Integer.parseInt(data.getOrDefault("age", "0").toString());
            String diagnosis = data.getOrDefault("diagnosis", "N/A").toString(); // optional field
            String medication = data.getOrDefault("medication", "N/A").toString(); // optional field

            PatientRecord record = new PatientRecord(
                    name,
                    age,
                    diagnosis,
                    medication,
                    0.0,
                    "N/A",
                    LoggedInUser.getEmail(),
                    gender
            );

            allPatients.add(record);
        }

        patientListView.setItems(allPatients);
    }
*/


    @FXML
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
                new UserService().updateProfileImageUrl(LoggedInUser.getEmail(), imageUrl);
                profileImageView.setImage(new Image(imageUrl));
                uploadLabel.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    @FXML
    private void onPatientSelected(MouseEvent event) {
        selectedPatient = patientListView.getSelectionModel().getSelectedItem();
    }

    private void showPatientDetails(PatientRecord patient) {
        patientInfoBox.getChildren().clear();

        Text title = createBoldText("Patient Details");
        Text nameCategory = createBoldText("Name");
        Text nameValue = new Text(patient.getName());

        Text ageCategory = createBoldText("Age");
        Text ageValue = new Text(String.valueOf(patient.getAge()));

        Text diagnosisCategory = createBoldText("Diagnosis");
        Text diagnosisValue = new Text(patient.getDiagnosis());

        Button goBackButton = new Button("Go Back");
        goBackButton.setOnAction(e -> showMainDashboard());

        patientInfoBox.getChildren().addAll(
                title,
                nameCategory, nameValue,
                ageCategory, ageValue,
                diagnosisCategory, diagnosisValue,
                goBackButton
        );
    }

    @FXML
    private void showMainDashboard() {
        patientInfoBox.getChildren().clear();

        HBox searchBox = new HBox(10, searchField, createButton("Search", this::onSearch));
        patientInfoBox.getChildren().add(searchBox);

        patientListView.setItems(FXCollections.observableArrayList()); // optional: clear list at first
        patientInfoBox.getChildren().add(patientListView);

        HBox actionButtons = new HBox(10,
                addPatientButton,
                editPatientButton,
                deletePatientButton,
                viewAppointmentsButton,
                viewBillingButton,
                logoutButton
        );
        patientInfoBox.getChildren().add(actionButtons);
    }


    private Text createBoldText(String content) {
        Text text = new Text(content);
        text.setFont(Font.font("Arial", 16));
        text.setStyle("-fx-font-weight: bold;");
        return text;
    }

    private Button createButton(String text, javafx.event.EventHandler<ActionEvent> event) {
        Button button = new Button(text);
        button.setOnAction(event);
        return button;
    }

    @FXML
    private void onLogout(ActionEvent event) {
        Alert logoutConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        logoutConfirmation.setTitle("Logout");
        logoutConfirmation.setHeaderText("Are you sure you want to log out?");
        logoutConfirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
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

    @FXML
    private void onAddPatient(ActionEvent event) {
        patientInfoBox.getChildren().clear();

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");

        TextField ageField = new TextField();
        ageField.setPromptText("Enter Age");

        TextField diagnosisField = new TextField();
        diagnosisField.setPromptText("Enter Diagnosis");

        Button saveButton = new Button("Save Patient");
        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String diagnosis = diagnosisField.getText();

                // Get user metadata
                Map<String, Object> userData = new UserService().getUserByEmail(LoggedInUser.getEmail());
                String gender = userData != null && userData.get("gender") != null
                        ? userData.get("gender").toString()
                        : "Unknown";

                PatientRecord newPatient = new PatientRecord(
                        name, age, diagnosis, "Medication", 0.0, "2025-05-01",
                        LoggedInUser.getEmail(), gender
                );

                allPatients.add(newPatient);
                showAlert("Success", "Patient added successfully.");
                showMainDashboard();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid input for age.");
            }
        });

        Button goBackButton = new Button("Go Back");
        goBackButton.setOnAction(e -> showMainDashboard());

        patientInfoBox.getChildren().addAll(
                createBoldText("Add New Patient"),
                nameField,
                ageField,
                diagnosisField,
                saveButton,
                goBackButton
        );
    }


    @FXML
    private void onEditPatient(ActionEvent event) {
        if (selectedPatient != null) {
            patientInfoBox.getChildren().clear();

            TextField nameField = new TextField(selectedPatient.getName());
            TextField ageField = new TextField(String.valueOf(selectedPatient.getAge()));
            TextField diagnosisField = new TextField(selectedPatient.getDiagnosis());

            Button updateButton = new Button("Update Patient");
            updateButton.setOnAction(e -> {
                try {
                    String name = nameField.getText();
                    int age = Integer.parseInt(ageField.getText());
                    String diagnosis = diagnosisField.getText();

                    // Get user metadata
                    Map<String, Object> userData = new UserService().getUserByEmail(LoggedInUser.getEmail());
                    String gender = userData != null && userData.get("gender") != null
                            ? userData.get("gender").toString()
                            : "Unknown";

                    selectedPatient.setName(name);
                    selectedPatient.setAge(age);
                    selectedPatient.setDiagnosis(diagnosis);
                    selectedPatient.setEmail(LoggedInUser.getEmail());
                    selectedPatient.setGender(gender);

                    patientListView.refresh();
                    showAlert("Success", "Patient updated successfully.");
                    showMainDashboard();
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Invalid input for age.");
                }
            });

            Button goBackButton = new Button("Go Back");
            goBackButton.setOnAction(e -> showMainDashboard());

            patientInfoBox.getChildren().addAll(
                    createBoldText("Edit Patient"),
                    nameField,
                    ageField,
                    diagnosisField,
                    updateButton,
                    goBackButton
            );
        } else {
            showAlert("Error", "Please select a patient to edit.");
        }
    }


    @FXML
    private void onDeletePatient(ActionEvent event) {
        if (selectedPatient != null) {
            Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
            deleteConfirmation.setTitle("Delete Patient");
            deleteConfirmation.setHeaderText("Are you sure you want to delete this patient?");
            deleteConfirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    allPatients.remove(selectedPatient);
                    patientListView.setItems(allPatients);
                    selectedPatient = null;
                    showAlert("Success", "Patient deleted.");
                    showMainDashboard();
                }
            });
        } else {
            showAlert("Error", "Please select a patient to delete.");
        }
    }

    @FXML
    private void onSearch(ActionEvent event) {
        String searchQuery = searchField.getText().toLowerCase();
        ObservableList<PatientRecord> searchResults = FXCollections.observableArrayList();

        UserService userService = new UserService();
        List<Map<String, Object>> firebasePatients = userService.getAllPatients();

        for (Map<String, Object> data : firebasePatients) {
            String name = data.getOrDefault("name", "").toString().toLowerCase();
            String email = data.getOrDefault("email", "").toString().toLowerCase();

            if (name.contains(searchQuery) || email.contains(searchQuery)) {
                int age = Integer.parseInt(data.getOrDefault("age", "0").toString());
                String diagnosis = data.getOrDefault("diagnosis", "N/A").toString();
                String medication = data.getOrDefault("medication", "N/A").toString();
                String gender = data.getOrDefault("gender", "Unknown").toString();

                searchResults.add(new PatientRecord(
                        data.getOrDefault("name", "N/A").toString(),
                        age,
                        diagnosis,
                        medication,
                        0.0,
                        "N/A",
                        email,
                        gender
                ));
            }
        }

        patientListView.setItems(searchResults);
    }

    @FXML
    private void onViewAppointments(ActionEvent event) {
        if (selectedPatient != null) {
            patientInfoBox.getChildren().clear();
            patientInfoBox.getChildren().addAll(
                    createBoldText("Appointments for " + selectedPatient.getName()),
                    new Text("Upcoming Appointment: [To be scheduled]"),
                    new Text("Diagnosis: " + selectedPatient.getDiagnosis()),
                    createGoBackButton()
            );
        } else {
            showAlert("Error", "Please select a patient to view appointments.");
        }
    }

    @FXML
    private void onViewBilling(ActionEvent event) {
        if (selectedPatient != null) {
            patientInfoBox.getChildren().clear();
            patientInfoBox.getChildren().addAll(
                    createBoldText("Billing for " + selectedPatient.getName()),
                    new Text("Treatment Cost: $" + selectedPatient.getBillingAmount()),
                    new Text("Diagnosis: " + selectedPatient.getDiagnosis()),
                    createGoBackButton()
            );
        } else {
            showAlert("Error", "Please select a patient to view billing.");
        }
    }

    private Button createGoBackButton() {
        Button goBackButton = new Button("Go Back");
        goBackButton.setOnAction(e -> showMainDashboard());
        return goBackButton;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
