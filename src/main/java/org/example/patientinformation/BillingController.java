package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class BillingController {

    @FXML
    private Button currentBtn;
    @FXML
    private Button pastBtn;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private TableView<BillingRecord> billingTable;
    @FXML
    private TableColumn<BillingRecord, String> serviceColumn;
    @FXML
    private TableColumn<BillingRecord, String> dateColumn;
    @FXML
    private TableColumn<BillingRecord, String> costColumn;
    @FXML
    private VBox billingContainer;

    private final ObservableList<BillingRecord> currentRecords = FXCollections.observableArrayList();
    private final ObservableList<BillingRecord> pastRecords = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        serviceColumn.setCellValueFactory(cellData -> cellData.getValue().serviceProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        costColumn.setCellValueFactory(cellData -> cellData.getValue().costProperty());

        loadBillingFromFirestore();

        showCurrent();
    }

    private void loadBillingFromFirestore() {
        Firestore db = FirestoreClient.getFirestore();
        String userEmail = LoggedInUser.getEmail();

        ApiFuture<QuerySnapshot> future = db.collection("users")
                .document(userEmail)
                .collection("billing")
                .get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            currentRecords.clear();
            for (QueryDocumentSnapshot doc : documents) {
                String service = doc.getString("service");
                String date = doc.getString("date");
                String cost = doc.getString("cost");
                currentRecords.add(new BillingRecord(service, date, cost));
            }
            billingTable.setItems(currentRecords);
            updateTotalAmount(currentRecords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showCurrent() {
        highlightButton(currentBtn, pastBtn);
        billingTable.setItems(currentRecords);
        updateTotalAmount(currentRecords);
    }

    @FXML
    private void showPast() {
        highlightButton(pastBtn, currentBtn);
        loadPastPayments();
    }


    private void updateTotalAmount(ObservableList<BillingRecord> records) {
        double total = records.stream()
                .mapToDouble(record -> Double.parseDouble(record.getCost().replace("$", "")))
                .sum();
        totalAmountLabel.setText("$" + total);
    }

    private void highlightButton(Button selected, Button other) {
        selected.setStyle("-fx-background-color: #7b4fff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20;");
        other.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 20;");
    }

    @FXML
    private void handleDownloadPdf() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(billingContainer.getScene().getWindow())) {
            boolean success = job.printPage(billingContainer);
            if (success) {
                job.endJob();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF Export");
                alert.setHeaderText(null);
                alert.setContentText("Billing details exported successfully.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("PDF Export Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while exporting the PDF.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handlePayNow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PaymentScreen.fxml"));
            Parent root = loader.load();

            PaymentController controller = loader.getController();
            controller.setBillingController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Make a Payment");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshBilling() {
        loadBillingFromFirestore();
        billingTable.setItems(currentRecords);
        updateTotalAmount(currentRecords);
    }



    private void loadPastPayments() {
        Firestore db = FirestoreClient.getFirestore();
        String userEmail = LoggedInUser.getEmail();

        ApiFuture<QuerySnapshot> future = db.collection("users")
                .document(userEmail)
                .collection("payment_history")
                .get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            pastRecords.clear();
            for (QueryDocumentSnapshot doc : documents) {
                String service = doc.getString("service");
                String date = doc.getString("date");
                String cost = doc.getString("cost");
                pastRecords.add(new BillingRecord(service, date, cost));
            }
            billingTable.setItems(pastRecords);
            updateTotalAmount(pastRecords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
