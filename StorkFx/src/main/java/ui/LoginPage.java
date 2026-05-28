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
        createAccount.setOnAction(e -> root.setCenter(SignupPage.createSignupPage(root)));

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

            new Thread(() -> {
                try {
                    URL url = new URL("http://localhost:8080/api/auth/login");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);

                    String jsonInputString = String.format("{\"identifier\": \"%s\", \"password\": \"%s\"}", input, password);

                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] inputBytes = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(inputBytes, 0, inputBytes.length);         
                    }

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                        }

                        String rawJson = response.toString();

                        String finalName = input;
                        String finalEmail = input.contains("@") ? input : input + "@storkdrop.com";

                        if (rawJson.contains("\"username\"")) {
                            finalName = rawJson.split("\"username\":\"")[1].split("\"")[0];
                        } else if (rawJson.contains("\"name\"")) {
                            finalName = rawJson.split("\"name\":\"")[1].split("\"")[0];
                        }
                        
                        if (rawJson.contains("\"email\"")) {
                            finalEmail = rawJson.split("\"email\":\"")[1].split("\"")[0];
                        }

                        SessionManager.login(finalName, finalEmail);

                        Platform.runLater(() -> {
                            loginBtn.setDisable(false);
                            errorLabel.setStyle("-fx-text-fill: #10B981;");
                            errorLabel.setText("Login successful!");
                            
                        });

                    } else {
                        Platform.runLater(() -> {
                            loginBtn.setDisable(false);
                            errorLabel.setStyle("-fx-text-fill: #EF4444;");
                            errorLabel.setText("Invalid username/email or password.");
                        });
                    }

                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        loginBtn.setDisable(false);
                        errorLabel.setStyle("-fx-text-fill: #EF4444;");
                        errorLabel.setText("Cannot connect to backend server.");
                    });
                }
            }).start();
        });

        card.getChildren().addAll(title, subtitle, userField, passwordField, loginBtn, errorLabel, createAccount);
        loginRoot.getChildren().add(card);
        return loginRoot;
    }
}
