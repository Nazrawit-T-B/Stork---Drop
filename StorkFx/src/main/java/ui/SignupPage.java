package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SignupPage {

    public static StackPane createSignupPage(BorderPane root) {
        StackPane signupRoot = new StackPane();
        signupRoot.getStyleClass().add("login-root");
        signupRoot.getStylesheets().add(SignupPage.class.getResource("/login.css").toExternalForm());

        VBox box = new VBox(20);
        box.setMaxWidth(420);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.getStyleClass().add("login-card");

        Label title = new Label("Create Account");
        title.getStyleClass().add("login-title");
        
        Label subtitle = new Label("Join Stork Drop today");
        subtitle.getStyleClass().add("login-subtitle");

        TextField fullName = new TextField();
        fullName.setPromptText("Full Name");

        TextField username = new TextField();
        username.setPromptText("Username");

        TextField email = new TextField();
        email.setPromptText("Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button create = new Button("Create Account");
        create.getStyleClass().add("login-btn");
        create.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-msg");
        errorLabel.setStyle("-fx-text-fill: #EF4444;");

        Hyperlink backToLogin = new Hyperlink("Already have an account? Sign In");
        backToLogin.getStyleClass().add("login-link");
        backToLogin.setOnAction(e -> {
            root.setCenter(LoginPage.createLoginPage(root));
        });

        create.setOnAction(e -> {
            String fName = fullName.getText().trim();
            String uName = username.getText().trim();
            String uEmail = email.getText().trim();
            String uPassword = password.getText();

            if (fName.isEmpty() || uName.isEmpty() || uEmail.isEmpty() || uPassword.isEmpty()) {
                errorLabel.setStyle("-fx-text-fill: #EF4444;");
                errorLabel.setText("Please fill out all registration fields.");
                return;
            }

            errorLabel.setStyle("-fx-text-fill: #94A3B8;");
            errorLabel.setText("Creating account...");
            create.setDisable(true);

            new Thread(() -> {
                try {
                    String url = "http://localhost:8080/api/auth/signup";
                    String jsonInputString = String.format(
                        "{\"fullName\": \"%s\", \"username\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}",
                        fName, uName, uEmail, uPassword
                    );

                    NetworkClient.sendPost(url, jsonInputString, false);

                    Platform.runLater(() -> {
                        create.setDisable(false);
                        root.setCenter(LoginPage.createLoginPage(root));
                    });

                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        create.setDisable(false);
                        errorLabel.setStyle("-fx-text-fill: #EF4444;");
                        
                        String incomingError = ex.getMessage();
                        if (incomingError != null && incomingError.contains("username")) {
                            errorLabel.setText("Username is already taken.");
                        } else if (incomingError != null && incomingError.contains("email")) {
                            errorLabel.setText("Email address is already registered.");
                        } else {
                            errorLabel.setText("Registration failed or server connection unavailable.");
                        }
                    });
                }
            }).start();
        });

        box.getChildren().addAll(
                title, subtitle, fullName, username, email, password, 
                create, errorLabel, backToLogin
        );

        signupRoot.getChildren().add(box);
        return signupRoot;
    }
}
