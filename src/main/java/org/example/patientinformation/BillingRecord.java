package org.example.patientinformation;

import javafx.beans.property.SimpleStringProperty;

public class BillingRecord {
    private final SimpleStringProperty service;
    private final SimpleStringProperty date;
    private final SimpleStringProperty cost;

    public BillingRecord(String service, String date, String cost) {
        this.service = new SimpleStringProperty(service);
        this.date = new SimpleStringProperty(date);
        this.cost = new SimpleStringProperty(cost);
    }

    public String getService() { return service.get(); }
    public String getDate() { return date.get(); }
    public String getCost() { return cost.get(); }

    public SimpleStringProperty serviceProperty() { return service; }
    public SimpleStringProperty dateProperty() { return date; }
    public SimpleStringProperty costProperty() { return cost; }
}
