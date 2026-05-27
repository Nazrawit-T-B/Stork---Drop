package ui;

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
