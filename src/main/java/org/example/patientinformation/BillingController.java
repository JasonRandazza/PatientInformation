package org.example.patientinformation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    public void initialize() {
        showCurrent();  // Default view on load
    }

    @FXML
    private void showCurrent() {
        highlightButton(currentBtn, pastBtn);
        totalAmountLabel.setText("$0");
        billingList.getChildren().clear();

        // Simulate adding one placeholder billing item
        HBox row = createBillingRow("Unknown", "$0");
        billingList.getChildren().add(row);
    }

    @FXML
    private void showPast() {
        highlightButton(pastBtn, currentBtn);
        totalAmountLabel.setText("$0");
        billingList.getChildren().clear();

        // Simulate past billing item
        HBox row = createBillingRow("Unknown (Past)", "$0");
        billingList.getChildren().add(row);
    }

    private HBox createBillingRow(String item, String amount) {
        Label itemLabel = new Label(item);
        itemLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        Label amountLabel = new Label(amount);
        amountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox row = new HBox(10, itemLabel, new javafx.scene.layout.Region(), amountLabel);
        HBox.setHgrow(row.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);
        row.setStyle("-fx-padding: 4 0;");
        return row;
    }

    private void highlightButton(Button selected, Button other) {
        selected.setStyle("-fx-background-color: #7b4fff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20;");
        other.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 20;");
    }
}

