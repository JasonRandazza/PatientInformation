package org.example.patientinformation.billing;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.patientinformation.billing.*;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class BillingController {

    @FXML
    private Button currentBtn;

    @FXML
    private Button pastBtn;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private VBox billingList;

    @FXML
    private Button payNowBtn;   //TODO: add fx:id in FXML for the pay button

    private final BillingService svc = new BillingService();

    private String patientEmail; //set by Doctor- or Patient-*ScreenController
    private String role;         // "doctor" or "patient"

    @FXML
    public void initialize(String email, String role) {
        this.patientEmail = email;
        this.role = role;
        loadInvoices();
    }

    //Doctor clicks "+ New" //TODO: hook it up in FXML or DoctorDashboardController
    public void onAddInvoice(String description, long dollars) {
        if (!"doctor".equals(role)) return;
        Invoice inv = new Invoice(UUID.randomUUID().toString(), dollars*100, description);
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

    @FXML private void onPayNow() {
        if (!"patient".equals(role)) return;
        Invoice sel = (Invoice) billingList.getUserData();  //stored in loadInvoices()
        if (sel==null || !"OPEN".equals(sel.getStatus())) return;

        try {
            String url = StripeCheckout.fetchUrl(patientEmail, sel.getId()); //HTTPS fn call
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception ex) {
            ex.printStackTrace();
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

            //Save last selected invoice as userData for PayNow button
            billingList.setUserData(list.stream()
                        .filter(i->"OPEN".equals(i.getStatus())).findFirst().orElse(null));

            payNowBtn.setDisable(!"patient".equals(role) || total==0);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void addRow(Invoice i) {
        Label item = new Label(i.getDescription());
        Label amt = new Label("$"+i.getAmount()/100.0);
        HBox.setHgrow(new Region(), Priority.ALWAYS);
        HBox row = new HBox(10, item, new Region(), amt);
        billingList.getChildren().add(row);

    }

    private void highlightButton(Button selected, Button other) {
        selected.setStyle("-fx-background-color: #7b4fff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20;");
        other.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 20;");
    }
}

