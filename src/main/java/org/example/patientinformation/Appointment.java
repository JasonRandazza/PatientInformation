package org.example.patientinformation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Appointment {
    private final StringProperty date;
    private final StringProperty time;

    public Appointment(String date, String time) {
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty timeProperty() {
        return time;
    }
}
