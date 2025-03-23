package org.example.patientinformation.Interface;

public interface UserAccount {
    String getEmail();
    String getName();
    String getRole(); //"patient" or "doctor" or "admin"
}
