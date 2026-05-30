package ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.application.Platform;

public class SessionManager {
    private static final BooleanProperty loggedIn = new SimpleBooleanProperty(false);
    private static final StringProperty fullName = new SimpleStringProperty("");
    private static final StringProperty email = new SimpleStringProperty("");
    
    // 1. Keep a raw volatile String for instant background thread access
    private static final StringProperty token = new SimpleStringProperty("");
    private static volatile String rawToken = ""; 

    public static BooleanProperty loggedInProperty() { return loggedIn; }
    public static StringProperty fullNameProperty() { return fullName; }
    public static StringProperty emailProperty() { return email; }
    public static StringProperty tokenProperty() { return token; }

    public static boolean isLoggedIn() { return loggedIn.get(); }
    public static String getFullName() { return fullName.get(); }
    public static String getEmail() { return email.get(); }
    
    public static String getActiveToken() { return rawToken; }

    public static void login(String name, String userEmail, String userToken) {
            fullName.set(name);
            email.set(userEmail);
            token.set(userToken);
            loggedIn.set(true);
            System.out.println("SessionManager successfully updated for: " + name);
    }

    public static void logout() {
            fullName.set("");
            email.set("");
            token.set("");
            loggedIn.set(false);
    }
}
