package org.example.patientinformation.billing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.awt.Desktop;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class BillingController {

    private final BillingService svc = new BillingService();
    private Invoice selectedInvoice;
    private String patientEmail;    //set by Doctor- or Patient-*ScreenController
    private String role;           // "doctor" or "patient"

    private static final String STRIPE_FN_URL = "https://createcheckoutsession-ibnkkkswsa-uc.a.run.app";

    @FXML
    private Button currentBtn;

    @FXML
    private Button pastBtn;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private VBox billingList;

    @FXML
    private Button payNowBtn;     // "doctor" or "patient"

    //Initialize from DoctorDashboard or PatientDashboard
    @FXML
    public void initialize(String email, String role) {
        this.patientEmail = email;
        this.role = role;
        loadInvoices();
    }

    //Doctor adds a new invoice
    public void onAddInvoice(String description, long dollars) {
        if (!"doctor".equals(role)) return;
        Invoice inv = new Invoice(UUID.randomUUID().toString(), dollars * 100, description);
        svc.save(patientEmail, inv);
        loadInvoices();
    }

    @FXML
    private void showCurrent() {
        highlightButton(currentBtn, pastBtn);
    }

    @FXML
    private void showPast() {
        highlightButton(pastBtn, currentBtn);
    }

    @FXML
    private void onPayNow() {
        if (selectedInvoice == null || !"OPEN".equals(selectedInvoice.getStatus())) return;

        try {
            String requestBody = String.format ("{\"email\":\"%s\", \"invoiceId\":\"%s\"}",
                    patientEmail, selectedInvoice.getId());

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(STRIPE_FN_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            JsonObject obj = JsonParser.parseString(resp.body()).getAsJsonObject();
            String checkoutUrl = obj.get("url").getAsString();

            Desktop.getDesktop().browse(new URI(checkoutUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInvoices() {
        billingList.getChildren().clear();
        try {
            List<Invoice> list = svc.findAll(patientEmail);
            long total = list.stream().filter(i->"OPEN".equals(i.getStatus()))
                    .mapToLong(Invoice::getAmount).sum();

            totalAmountLabel.setText("$"+total/100.0);
            list.forEach(this::addRow);

            selectedInvoice = null;
            payNowBtn.setDisable(!"patient".equals(role) || total==0);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void addRow(Invoice i) {
        Label item = new Label(i.getDescription());
        Label amt = new Label("$"+i.getAmount()/100.0);
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, item, spacer, amt);
        row.setOnMouseClicked(event -> {
            if ("OPEN".equals(i.getStatus())) {
                selectedInvoice = i;
                payNowBtn.setDisable(false);
            } else {
                selectedInvoice = null;
                payNowBtn.setDisable(true);
            }
        });
        billingList.getChildren().add(row);

    }

    private void highlightButton(Button selected, Button other) {
        selected.setStyle("-fx-background-color: #7b4fff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20;");
        other.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 20;");
    }
}

