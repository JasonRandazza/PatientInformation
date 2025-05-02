package org.example.patientinformation;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class StorageService {
    public static String uploadProfileImage(String email, File imageFile) throws IOException {
        String fileName = "profilePictures/" + email + ".jpg";
        FileInputStream fileStream = new FileInputStream(imageFile);

        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(fileName, fileStream, "image/jpeg");

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        );
    }
}
