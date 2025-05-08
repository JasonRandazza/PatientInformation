package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class PaymentController {

    @FXML private RadioButton fullAmountOption;
    @FXML private RadioButton otherAmountOption;
    @FXML private TextField customAmountField;
    private BillingController billingController;

    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        fullAmountOption.setToggleGroup(group);
        otherAmountOption.setToggleGroup(group);

        otherAmountOption.setOnAction(e -> customAmountField.setDisable(false));
        fullAmountOption.setOnAction(e -> customAmountField.setDisable(true));
    }

    public void setBillingController(BillingController controller) {
        this.billingController = controller;
    }

    @FXML
    private void handleConfirmPayment() {
        String amount;

        if (otherAmountOption.isSelected()) {
            String input = customAmountField.getText();
            if (input == null || input.trim().isEmpty()) {
                showWarning("Please enter a payment amount.");
                return;
            }
            try {
                double parsed = Double.parseDouble(input);
                if (parsed <= 0) {
                    showWarning("Amount must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showWarning("Invalid amount. Please enter a valid number.");
                return;
            }
            amount = input;
        } else {
            amount = BillingManager.getBillingRecords().stream()
                    .mapToDouble(record -> Double.parseDouble(record.getCost().replace("$", "")))
                    .sum() + "";
        }

        Firestore db = FirestoreClient.getFirestore();
        String userEmail = LoggedInUser.getEmail();

        ApiFuture<QuerySnapshot> future = db.collection("users")
                .document(userEmail)
                .collection("billing")
                .get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                db.collection("users")
                        .document(userEmail)
                        .collection("payment_history")
                        .add(doc.getData());

                doc.getReference().delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        BillingManager.getBillingRecords().clear();

        if (billingController != null) {
            billingController.refreshBilling();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText(null);
        alert.setContentText("You paid: $" + amount);
        alert.showAndWait();

        Stage stage = (Stage) fullAmountOption.getScene().getWindow();
        stage.close();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
