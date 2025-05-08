package org.example.patientinformation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MedicalRecord {
    private final StringProperty date;
    private final StringProperty doctor;
    private final StringProperty notes;
    private final StringProperty prescription;

    public MedicalRecord(String date, String doctor, String notes, String prescription) {
        this.date = new SimpleStringProperty(date);
        this.doctor = new SimpleStringProperty(doctor);
        this.notes = new SimpleStringProperty(notes);
        this.prescription = new SimpleStringProperty(prescription);
    }

    public StringProperty dateProperty() { return date; }
    public StringProperty doctorProperty() { return doctor; }
    public StringProperty notesProperty() { return notes; }
    public StringProperty prescriptionProperty() { return prescription; }

    public String getDate() { return date.get(); }
    public String getDoctor() { return doctor.get(); }
    public String getNotes() { return notes.get(); }
    public String getPrescription() { return prescription.get(); }
}
