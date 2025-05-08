package org.example.patientinformation;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;

public class BillingManager {
    private static final ObservableList<BillingRecord> billingRecords = FXCollections.observableArrayList();

    public static void addBillingRecord(Appointment appointment) {
        String category = appointment.categoryProperty().get();
        String cost = switch (category.toLowerCase()) {
            case "heart" -> "$75";
            case "brain" -> "$70";
            case "physical" -> "$50";
            default -> "$0";
        };

        BillingRecord record = new BillingRecord(category, appointment.dateProperty().get(), cost);
        billingRecords.add(record);

        Firestore db = FirestoreClient.getFirestore();
        String userEmail = LoggedInUser.getEmail();

        Map<String, Object> billingData = Map.of(
                "service", category,
                "date", appointment.dateProperty().get(),
                "cost", cost
        );

        db.collection("users")
                .document(userEmail)
                .collection("billing")
                .add(billingData);
    }

    public static ObservableList<BillingRecord> getBillingRecords() {
        return billingRecords;
    }
}
