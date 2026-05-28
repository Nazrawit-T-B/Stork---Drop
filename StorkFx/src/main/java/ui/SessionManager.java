package ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SessionManager {
    private static final BooleanProperty loggedIn = new SimpleBooleanProperty(false);
    private static String fullName = "Guest User";
    private static String email = "Not signed in";

    // Returns the raw boolean value
    public static boolean isLoggedIn() { return loggedIn.get(); }
    
    // EXPOSED PROPERTY: Allows the sidebar UI components to observe changes dynamically
    public static BooleanProperty loggedInProperty() { return loggedIn; }

    public static String getFullName() { return fullName; }
    public static String getEmail() { return email; }

    public static void login(String name, String userEmail) {
        fullName = name;
        email = userEmail;
        loggedIn.set(true); // Triggers any registered listeners instantly
    }

    public static void logout() {
        fullName = "Guest User";
        email = "Not signed in";
        loggedIn.set(false); // Triggers any registered listeners instantly
    }
}package ui;

public class SessionManager {

    private static boolean loggedIn = false;

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void login() {
        loggedIn = true;
    }

    public static void logout() {
        loggedIn = false;
    }
}
