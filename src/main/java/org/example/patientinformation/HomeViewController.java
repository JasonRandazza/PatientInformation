package org.example.patientinformation;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeViewController {

    @FXML private Label welcomeLabel;
    @FXML private Label nameLabel;

    @FXML
    public void initialize() {

        String userName = LoggedInUser.getName();
        System.out.println("HomeView initialized. User: " + userName); // <-- ADD THIS

        welcomeLabel.setText("Welcome back, " + userName);
        nameLabel.setText(userName);
    }
}
