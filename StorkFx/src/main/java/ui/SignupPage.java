package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SignupPage {

    public static StackPane createSignupPage(BorderPane root) {
        StackPane signupRoot = new StackPane();
        signupRoot.getStyleClass().add("login-root");

        // LINKING THE STYLESHEET
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

        // Label to show confirmation success messages or database constraint errors
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-msg");
        errorLabel.setStyle("-fx-text-fill: #EF4444;");

        Hyperlink backToLogin = new Hyperlink("Already have an account? Sign In");
        backToLogin.getStyleClass().add("login-link");
        backToLogin.setOnAction(e -> {
            root.setCenter(LoginPage.createLoginPage(root));
        });

        // Trigger Account Registration Database Check
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
                    URL url = new URL("http://localhost:8080/api/auth/signup");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);

                    // Form raw JSON string safely passing along structural values
                    String jsonInputString = String.format(
                        "{\"fullName\": \"%s\", \"username\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}",
                        fName, uName, uEmail, uPassword
                    );

                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] inputBytes = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(inputBytes, 0, inputBytes.length);
                    }

                    int responseCode = conn.getResponseCode();
Platform.runLater(() -> {
    create.setDisable(false);
    try {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            root.setCenter(LoginPage.createLoginPage(root));
        } else {
            // Check if there is an error stream to read from
            java.io.InputStream errorStream = conn.getErrorStream();
            if (errorStream != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    errorLabel.setStyle("-fx-text-fill: #EF4444;");
                    errorLabel.setText(response.length() > 0 ? response.toString() : "Server Error: " + responseCode);
                }
            } else {
                // No stream available, print out the raw HTTP status code directly
                errorLabel.setStyle("-fx-text-fill: #EF4444;");
                errorLabel.setText("Server returned HTTP error status: " + responseCode);
            }
        }
    } catch (Exception ex) {
        errorLabel.setStyle("-fx-text-fill: #EF4444;");
        errorLabel.setText("Failed to process server response.");
    }
});
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        create.setDisable(false);
                        errorLabel.setStyle("-fx-text-fill: #EF4444;");
                        errorLabel.setText("Cannot connect to backend server.");
                    });
                }
            }).start();
        });

        box.getChildren().addAll(
                title,
                subtitle,
                fullName,
                username,
                email,
                password,
                create,
                errorLabel, // Added to the visual container
                backToLogin
        );

        signupRoot.getChildren().add(box);
        return signupRoot;
    }
}
