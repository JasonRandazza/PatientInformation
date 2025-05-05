package org.example.patientinformation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class HomeViewController {

    @FXML private Label welcomeLabel;
    @FXML private Label nameLabel;
    @FXML private ImageView profileImageView;
    @FXML private StackPane imageContainer;
    @FXML private Label uploadLabel;

    @FXML private TextField genderField;
    @FXML private TextField ageField;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private Button editButton;

    private boolean isEditing = false;

    @FXML
    public void initialize() {
        String userName = LoggedInUser.getName();
        welcomeLabel.setText("Welcome back, " + userName);
        nameLabel.setText(userName);

        // Apply circular clip to make image round
        Circle clip = new Circle(50, 50, 50);
        profileImageView.setClip(clip);

        uploadLabel.setVisible(true);
        imageContainer.setOnMouseClicked(this::onProfileImageClick);

        loadProfileImage();
        loadProfileFields();

        // Disable editing by default
        setFieldsEditable(false);
    }

    private void loadProfileImage() {
        String email = LoggedInUser.getEmail();
        if (email == null || email.isEmpty()) return;

        Map<String, Object> userData = new UserService().getUserByEmail(email);
        if (userData != null && userData.get("profileImage") != null) {
            String imageUrl = userData.get("profileImage").toString();
            profileImageView.setImage(new Image(imageUrl));
            uploadLabel.setVisible(false);
        }
    }

    private void loadProfileFields() {
        Map<String, Object> data = new UserService().getUserByEmail(LoggedInUser.getEmail());
        if (data != null) {
            genderField.setText(data.getOrDefault("gender", "").toString());
            ageField.setText(data.getOrDefault("age", "").toString());
            heightField.setText(data.getOrDefault("height", "").toString());
            weightField.setText(data.getOrDefault("weight", "").toString());
        }
    }

    private void setFieldsEditable(boolean editable) {
        genderField.setEditable(editable);
        ageField.setEditable(editable);
        heightField.setEditable(editable);
        weightField.setEditable(editable);
    }

    @FXML
    private void onEditOrSaveProfile() {
        if (!isEditing) {
            setFieldsEditable(true);
            editButton.setText("Save");
            isEditing = true;
        } else {
            String email = LoggedInUser.getEmail();
            UserService userService = new UserService();
            userService.updateUserField(email, "gender", genderField.getText());
            userService.updateUserField(email, "age", ageField.getText());
            userService.updateUserField(email, "height", heightField.getText());
            userService.updateUserField(email, "weight", weightField.getText());

            setFieldsEditable(false);
            editButton.setText("Edit");
            isEditing = false;
        }
    }

    private void onProfileImageClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (file != null) {
            try {
                String imageUrl = StorageService.uploadProfileImage(LoggedInUser.getEmail(), file);
                new UserService().updateProfileImageUrl(LoggedInUser.getEmail(), imageUrl);
                profileImageView.setImage(new Image(imageUrl));
                uploadLabel.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
