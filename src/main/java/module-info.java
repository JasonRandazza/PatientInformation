module org.example.patientinformation {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires firebase.admin;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires com.google.api.apicommon;
    requires proto.google.cloud.firestore.v1;
    requires google.cloud.storage;

    opens org.example.patientinformation to javafx.fxml;
    exports org.example.patientinformation;
}