package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LoginPage {

    public static StackPane createLoginPage(BorderPane root) {
        StackPane loginRoot = new StackPane();
        loginRoot.getStyleClass().add("login-root");

        // LINKING THE STYLESHEET
        loginRoot.getStylesheets().add(LoginPage.class.getResource("/login.css").toExternalForm());

        VBox card = new VBox(20);
        card.setMaxWidth(420);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.getStyleClass().add("login-card");

        Label title = new Label("Sign In");
        title.getStyleClass().add("login-title");

        Label subtitle = new Label("Continue to Stork Drop");
        subtitle.getStyleClass().add("login-subtitle");

        TextField userField = new TextField();
        userField.setPromptText("Username or Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("login-btn");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
       
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-msg");
        errorLabel.setStyle("-fx-text-fill: #EF4444;");

        Hyperlink createAccount = new Hyperlink("Create Account");
        createAccount.getStyleClass().add("login-link");
        createAccount.setOnAction(e -> {
            root.setCenter(SignupPage.createSignupPage(root));
        });

        // Trigger Login validation logic block when clicked
        loginBtn.setOnAction(e -> {
            String input = userField.getText().trim();
            String password = passwordField.getText();

            if(input.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Fill all fields");
                return;
            }

            errorLabel.setStyle("-fx-text-fill: #94A3B8;");
            errorLabel.setText("Connecting to server...");
            loginBtn.setDisable(true);

            // Execute network communication on a worker thread to keep the UI smooth
            new Thread(() -> {
                try {
                    URL url = new URL("http://localhost:8080/api/auth/login");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);

                    // Build raw JSON payload mapping cleanly to the LoginRequest class
                    String jsonInputString = String.format("{\"identifier\": \"%s\", \"password\": \"%s\"}", input, password);

                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] inputBytes = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(inputBytes, 0, inputBytes.length);         
                    }

                    int responseCode = conn.getResponseCode();

                    // Push mutations back to the main UI loop via runLater()
                    Platform.runLater(() -> {
                        loginBtn.setDisable(false);
                        try {
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                // Session verified successfully! Set auth state and show dashboard
                                // SessionManager.login();
                                // SceneManager.showDashboard();
                                errorLabel.setStyle("-fx-text-fill: #10B981;"); // Mint green success
                                errorLabel.setText("Login successful!");
            
                            } else {
                                java.io.InputStream errorStream = conn.getErrorStream();
                                if (errorStream != null) {
                                    try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
                                        StringBuilder response = new StringBuilder();
                                        String responseLine;
                                        while ((responseLine = br.readLine()) != null) {
                                            response.append(responseLine.trim());
                                        }
                                        errorLabel.setStyle("-fx-text-fill: #EF4444;");
                                        errorLabel.setText(response.length() > 0 ? response.toString() : "Unauthorized (" + responseCode + ")");
                                    }
                                } else {
                                    errorLabel.setStyle("-fx-text-fill: #EF4444;");
                                    errorLabel.setText("Invalid username/email or password.");
                                }
                            }
                        } catch (Exception ex) {
                            errorLabel.setStyle("-fx-text-fill: #EF4444;");
                            errorLabel.setText("Error reading login response.");
                        }
                    });

                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        loginBtn.setDisable(false);
                        errorLabel.setStyle("-fx-text-fill: #EF4444;");
                        errorLabel.setText("Cannot connect to Spring Boot back-end server.");
                    });
                }
            }).start();
        });

        card.getChildren().addAll(
                title,
                subtitle,
                userField,
                passwordField,
                loginBtn,
                errorLabel,
                createAccount
        );

        loginRoot.getChildren().add(card);
        return loginRoot;
    }
}
