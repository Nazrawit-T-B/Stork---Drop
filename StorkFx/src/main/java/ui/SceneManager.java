package ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void showDashboard() {
        Scene scene = new Scene(Dashboard.createDashboard(), 1200, 800);
        stage.setScene(scene);
    }

    public static void showLogin() {
        javafx.scene.layout.BorderPane baseDashboard = Dashboard.createDashboard();
        baseDashboard.setCenter(LoginPage.createLoginPage(baseDashboard));
        
        Scene scene = new Scene(baseDashboard, 1200, 800);
        stage.setScene(scene);
    }

    public static void showSignup() {
        javafx.scene.layout.BorderPane baseDashboard = Dashboard.createDashboard();
        baseDashboard.setCenter(SignupPage.createSignupPage(baseDashboard));
        
        Scene scene = new Scene(baseDashboard, 1200, 800);
        stage.setScene(scene);
    }
}
