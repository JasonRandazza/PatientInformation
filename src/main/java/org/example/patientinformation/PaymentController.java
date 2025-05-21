package org.example.patientinformation;

import com.google.api.core.ApiFuture;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.exception.StripeException;
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
        // TODO: Replace with a secure way to handle API keys
        Stripe.apiKey = "sk_test_YOUR_STRIPE_SECRET_KEY"; 
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
        String amountToPayInDollars;

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
                amountToPayInDollars = String.format("%.2f", parsed); // Format to 2 decimal places
            } catch (NumberFormatException e) {
                showWarning("Invalid amount. Please enter a valid number.");
                return;
            }
        } else {
            double totalBill = BillingManager.getBillingRecords().stream()
                    .mapToDouble(record -> Double.parseDouble(record.getCost().replace("$", "")))
                    .sum();
            if (totalBill <= 0) {
                showWarning("There is no amount to pay or the bill is zero.");
                return;
            }
            amountToPayInDollars = String.format("%.2f", totalBill);
        }

        long amountInCents = (long) (Double.parseDouble(amountToPayInDollars) * 100);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setPaymentMethod("pm_card_visa") // Test payment method
                .setConfirm(true)
                .setReturnUrl("http://localhost/checkout/success") // Placeholder
                .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                String paymentIntentId = paymentIntent.getId();
                String paymentStatus = paymentIntent.getStatus();

                Firestore db = FirestoreClient.getFirestore();
                String userEmail = LoggedInUser.getEmail();

                ApiFuture<QuerySnapshot> future = db.collection("users")
                        .document(userEmail)
                        .collection("billing")
                        .get();

                try {
                    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                    WriteBatch batch = db.batch();

                    for (QueryDocumentSnapshot doc : documents) {
                        DocumentReference paymentHistoryRef = db.collection("users")
                                .document(userEmail)
                                .collection("payment_history")
                                .document(); // Auto-generate ID

                        java.util.Map<String, Object> dataWithStripeInfo = new java.util.HashMap<>(doc.getData());
                        dataWithStripeInfo.put("stripePaymentIntentId", paymentIntentId);
                        dataWithStripeInfo.put("stripePaymentStatus", paymentStatus);

                        batch.set(paymentHistoryRef, dataWithStripeInfo);
                        batch.delete(doc.getReference());
                    }
                    batch.commit().get(); // Wait for batch commit

                    BillingManager.getBillingRecords().clear();
                    if (billingController != null) {
                        billingController.refreshBilling();
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Payment Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Payment successful. Stripe Payment ID: " + paymentIntentId);
                    alert.showAndWait();

                    Stage stage = (Stage) fullAmountOption.getScene().getWindow();
                    stage.close();

                } catch (Exception e) {
                    e.printStackTrace(); // Log Firestore specific exceptions
                    showError("An error occurred while updating records post-payment: " + e.getMessage());
                }
            } else {
                showError("Payment failed or requires further action. Status: " + paymentIntent.getStatus());
            }

        } catch (StripeException e) {
            e.printStackTrace(); // Log Stripe exception
            showError("Payment failed due to a Stripe error: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions during payment processing or amount parsing
            e.printStackTrace();
            showError("An unexpected error occurred during payment processing: " + e.getMessage());
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Payment Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
