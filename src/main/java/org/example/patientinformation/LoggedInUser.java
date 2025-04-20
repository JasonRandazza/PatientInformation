package org.example.patientinformation;

public class LoggedInUser {
    private static String name;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        LoggedInUser.name = name;
    }
}
