package org.example.patientinformation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class Appointment {
    private final StringProperty date;
    private final StringProperty time;
    private final StringProperty category;
    private String firestoreId;


    public Appointment(String date, String time, String category) {
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
        this.category = new SimpleStringProperty(category);

    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty timeProperty() {
        return time;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "date", date.get(),
                "time", time.get(),
                "category", category.get()
        );
    }
    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }
}
