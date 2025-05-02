package org.example.patientinformation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    @FXML
    public void initialize() {
        String userName = LoggedInUser.getName();
        welcomeLabel.setText("Welcome back, " + userName);
        nameLabel.setText(userName);

        // Apply circular clip to make image round
        Circle clip = new Circle(50, 50, 50);
        profileImageView.setClip(clip);

        uploadLabel.setVisible(true); // show "Upload" by default
        imageContainer.setOnMouseClicked(this::onProfileImageClick);

        // 🔥 Load saved image from Firestore if exists
        loadProfileImage();  // <-- Add this line
    }
    private void loadProfileImage() {
        String email = LoggedInUser.getEmail(); // get logged-in user's email
        if (email == null || email.isEmpty()) return;

        Map<String, Object> userData = new UserService().getUserByEmail(email);
        if (userData != null && userData.get("profileImage") != null) {
            String imageUrl = userData.get("profileImage").toString();
            profileImageView.setImage(new Image(imageUrl));
            uploadLabel.setVisible(false); // hide "Upload" text if image exists
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
                // Upload to Firebase Storage
                String imageUrl = StorageService.uploadProfileImage(LoggedInUser.getEmail(), file);

                // Save URL in Firestore
                new UserService().updateProfileImageUrl(LoggedInUser.getEmail(), imageUrl);

                // Display the image in the app
                profileImageView.setImage(new Image(imageUrl));
                uploadLabel.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
