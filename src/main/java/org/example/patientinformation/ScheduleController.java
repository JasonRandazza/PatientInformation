package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleController {

    @FXML private ToggleButton physicalBtn;
    @FXML private ToggleButton heartBtn;
    @FXML private ToggleButton brainBtn;
    @FXML private ImageView categoryImageView;
    @FXML private TableView<Appointment> bookingsTable;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> timeColumn;
    @FXML private TableColumn<Appointment, String> categoryColumn;


    private final ToggleGroup categoryGroup = new ToggleGroup();
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Group category buttons
        physicalBtn.setToggleGroup(categoryGroup);
        heartBtn.setToggleGroup(categoryGroup);
        brainBtn.setToggleGroup(categoryGroup);

        // Setup image switching
        physicalBtn.setOnAction(e -> updateImage("physical"));
        heartBtn.setOnAction(e -> updateImage("heart"));
        brainBtn.setOnAction(e -> updateImage("brain"));

        // Setup bookings table
        dateColumn.setCellValueFactory(cell -> cell.getValue().dateProperty());
        timeColumn.setCellValueFactory(cell -> cell.getValue().timeProperty());
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());

        bookingsTable.setItems(appointmentList);
        bookingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        loadAppointmentsFromFirestore();

    }

    private void updateImage(String category) {
        String pngPath = "/images/" + category + ".png";
        String jpgPath = "/images/" + category + ".jpeg";

        try (InputStream stream = getClass().getResourceAsStream(pngPath)) {
            if (stream != null) {
                categoryImageView.setImage(new Image(stream));
                return;
            }
        } catch (Exception ignored) {}

        try (InputStream stream = getClass().getResourceAsStream(jpgPath)) {
            if (stream != null) {
                categoryImageView.setImage(new Image(stream));
            }
        } catch (Exception e) {
            System.out.println("Image not found for category: " + category);
        }
    }

    @FXML
    private void openDateTimeDialog() {
        Dialog<Pair<LocalDate, String>> dialog = new Dialog<>();
        dialog.setTitle("Select Date & Time");

        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<String> timeComboBox = new ComboBox<>();

        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(15, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        while (!start.isAfter(end)) {
            timeComboBox.getItems().add(start.format(formatter));
            start = start.plusMinutes(30);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeComboBox, 1, 1);

        GridPane.setHgrow(datePicker, Priority.ALWAYS);
        GridPane.setHgrow(timeComboBox, Priority.ALWAYS);
        dialog.getDialogPane().setContent(grid);

        ButtonType confirmBtn = new ButtonType("Confirm Appointment", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBtn, ButtonType.CANCEL);

        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == confirmBtn && datePicker.getValue() != null && timeComboBox.getValue() != null) {
                return new Pair<>(datePicker.getValue(), timeComboBox.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            String category = getSelectedCategory();
            Appointment appointment = new Appointment(result.getKey().toString(), result.getValue(), category);
            appointmentList.add(appointment);
            saveAppointmentToFirestore(appointment);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Scheduled");
            alert.setHeaderText(null);
            alert.setContentText("Your appointment is booked for " +
                    result.getKey() + " at " + result.getValue() + " under " + category + ".");
            alert.showAndWait();
        });
    }
    private String getSelectedCategory() {
        if (physicalBtn.isSelected()) return "Physical";
        if (heartBtn.isSelected()) return "Heart";
        if (brainBtn.isSelected()) return "Brain";
        return "Unspecified";
    }

    private void saveAppointmentToFirestore(Appointment appointment) {
        Firestore db = FirestoreClient.getFirestore();
        String userEmail = LoggedInUser.getEmail();
        DocumentReference userDocRef = db.collection("users").document(userEmail);
        CollectionReference appointmentsRef = userDocRef.collection("appointments");

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("date", appointment.dateProperty().get());
        appointmentData.put("time", appointment.timeProperty().get());
        appointmentData.put("category", appointment.categoryProperty().get());

        appointmentsRef.add(appointmentData);
    }

    private void loadAppointmentsFromFirestore() {
        Firestore db = FirestoreClient.getFirestore();
        String userEmail = LoggedInUser.getEmail();
        CollectionReference appointmentsRef = db.collection("users").document(userEmail).collection("appointments");

        ApiFuture<QuerySnapshot> future = appointmentsRef.get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                String date = doc.getString("date");
                String time = doc.getString("time");
                String category = doc.getString("category");
                appointmentList.add(new Appointment(date, time, category));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}