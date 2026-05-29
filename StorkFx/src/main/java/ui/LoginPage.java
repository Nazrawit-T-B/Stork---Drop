package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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
                    String url = "http://localhost:8080/api/auth/login";
                    String jsonInputString = String.format("{\"identifier\": \"%s\", \"password\": \"%s\"}", input, password);

                    String rawJson = NetworkClient.sendPost(url, jsonInputString, false);

                    System.out.println("RAW SERVER JSON RESPONSE: " + rawJson);

                    String finalToken = "";
                    String finalName = input;
                    String finalEmail = input.contains("@") ? input : input + "@storkdrop.com";

                    if (rawJson.contains("\"token\"")) {
                        finalToken = rawJson.split("\"token\":\"")[1].split("\"")[0];
                    }
                    if (rawJson.contains("\"fullName\"")) {
                        finalName = rawJson.split("\"fullName\":\"")[1].split("\"")[0];
                    } else if (rawJson.contains("\"username\"")) {
                        finalName = rawJson.split("\"username\":\"")[1].split("\"")[0];
                    }
                    if (rawJson.contains("\"email\"")) {
                        finalEmail = rawJson.split("\"email\":\"")[1].split("\"")[0];
                    }

                    SessionManager.login(finalName, finalEmail, finalToken);

                   Platform.runLater(() -> {
    loginBtn.setDisable(false);
    errorLabel.setStyle("-fx-text-fill: #10B981;");
    errorLabel.setText("Login successful!");
    
    javafx.scene.layout.VBox updatedSidebar = Sidebar.createsidebar(root);
    root.setLeft(updatedSidebar);
    
    for (javafx.scene.Node node : updatedSidebar.getChildren()) {
        if (node instanceof javafx.scene.control.ToggleButton) {
            javafx.scene.control.ToggleButton navButton = (javafx.scene.control.ToggleButton) node;
            
            if ("Sync & Activity".equals(navButton.getText())) {
                navButton.setSelected(true); 
                navButton.fire();            
                break;
            }
        }
    }
});

                } catch (Exception ex) {
                    ex.printStackTrace(); 
                    Platform.runLater(() -> {
                        loginBtn.setDisable(false);
                        errorLabel.setStyle("-fx-text-fill: #EF4444;");
                        errorLabel.setText(ex.getMessage().contains("Server returned") ? 
                                           "Invalid username/email or password." : "Cannot connect to server.");
                    });
                }
            }).start();
        });

        card.getChildren().addAll(title, subtitle, userField, passwordField, loginBtn, errorLabel, createAccount);
        loginRoot.getChildren().add(card);
        return loginRoot;
    }
}
