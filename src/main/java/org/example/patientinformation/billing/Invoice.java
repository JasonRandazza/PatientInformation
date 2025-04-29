package org.example.patientinformation.billing;

import com.google.cloud.Timestamp;

public class Invoice {

    private String id;  //document ID
    private long amount;
    private String currency;
    private String description;
    private String status;  //Current/ Past due
    private Timestamp createdAt;;
    private Timestamp updatedAt;

    public Invoice() {}  //Firestore needs no-arg constructor

    public Invoice(String id, long amount, String description){
        this.setId(id);
        this.setAmount(amount);
        this.setCurrency("USD");
        this.setDescription(description);
        this.setStatus("Current");
        this.setCreatedAt(Timestamp.now());
        this.setUpdatedAt(Timestamp.now());
    }
    //Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
