package org.example.patientinformation;

public class PatientRecord {
    private String name;
    private int age;
    private String diagnosis;
    private String medications;
    private double billingAmount;
    private String appointmentDate;
    private String email;
    private String gender;

    // Constructor
    public PatientRecord(String name, int age, String diagnosis, String medications, double billingAmount,
                         String appointmentDate, String email, String gender) {
        this.name = name;
        this.age = age;
        this.diagnosis = diagnosis;
        this.medications = medications;
        this.billingAmount = billingAmount;
        this.appointmentDate = appointmentDate;
        this.email = email;
        this.gender = gender;
    }

    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDiagnosis() { return diagnosis; }
    public String getMedications() { return medications; }
    public double getBillingAmount() { return billingAmount; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setMedications(String medications) { this.medications = medications; }
    public void setBillingAmount(double billingAmount) { this.billingAmount = billingAmount; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }

    @Override
    public String toString() {
        return name + " (" + age + " years, " + gender + ") - " + diagnosis;
    }
}
