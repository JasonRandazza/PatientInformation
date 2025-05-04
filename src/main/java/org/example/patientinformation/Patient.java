package org.example.patientinformation;

public class Patient {
    private String name;
    private int age;
    private String diagnosis;
    private String medication;

    public Patient(String name, int age, String diagnosis, String medication) {
        this.name = name;
        this.age = age;
        this.diagnosis = diagnosis;
        this.medication = medication;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDiagnosis() { return diagnosis; }
    public String getMedication() { return medication; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setMedication(String medication) { this.medication = medication; }
}