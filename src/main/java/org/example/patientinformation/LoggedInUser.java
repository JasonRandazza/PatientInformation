package org.example.patientinformation;

public class LoggedInUser {
    private static String name;
    private static String email;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        LoggedInUser.name = name;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        LoggedInUser.email = email;
    }
}
