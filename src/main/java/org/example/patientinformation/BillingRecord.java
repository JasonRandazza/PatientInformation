package org.example.patientinformation;

import javafx.beans.property.SimpleStringProperty;

public class BillingRecord {
    private final SimpleStringProperty service;
    private final SimpleStringProperty date;
    private final SimpleStringProperty cost;
    private SimpleStringProperty stripePaymentIntentId;
    private SimpleStringProperty stripePaymentStatus;

    public BillingRecord(String service, String date, String cost) {
        this.service = new SimpleStringProperty(service);
        this.date = new SimpleStringProperty(date);
        this.cost = new SimpleStringProperty(cost);
        this.stripePaymentIntentId = new SimpleStringProperty(null);
        this.stripePaymentStatus = new SimpleStringProperty(null);
    }

    public BillingRecord(String service, String date, String cost, String stripeId, String stripeStatus) {
        this.service = new SimpleStringProperty(service);
        this.date = new SimpleStringProperty(date);
        this.cost = new SimpleStringProperty(cost);
        this.stripePaymentIntentId = new SimpleStringProperty(stripeId);
        this.stripePaymentStatus = new SimpleStringProperty(stripeStatus);
    }

    public String getService() { return service.get(); }
    public String getDate() { return date.get(); }
    public String getCost() { return cost.get(); }
    public String getStripePaymentIntentId() { return stripePaymentIntentId.get(); }
    public String getStripePaymentStatus() { return stripePaymentStatus.get(); }

    public SimpleStringProperty serviceProperty() { return service; }
    public SimpleStringProperty dateProperty() { return date; }
    public SimpleStringProperty costProperty() { return cost; }
    public SimpleStringProperty stripePaymentIntentIdProperty() { return stripePaymentIntentId; }
    public SimpleStringProperty stripePaymentStatusProperty() { return stripePaymentStatus; }
}
