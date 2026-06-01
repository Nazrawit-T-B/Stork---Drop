package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

public class Sidebar {

    public static VBox createsidebar(BorderPane root) {
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(200);

        Image image = new Image(Sidebar.class.getResourceAsStream("/img_2.png"));
        ImageView logoView = new ImageView(image);
        logoView.setFitWidth(70);
        logoView.setPreserveRatio(true);

        Label logo = new Label("Stork Drop");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold; -fx-padding: 30 0 0 0;");
        HBox brand = new HBox();
        brand.getChildren().addAll(logoView, logo);
        sidebar.getChildren().add(brand);

        ToggleGroup group = new ToggleGroup();

        // 1. SYNC & ACTIVITY
        ToggleButton btnSync = new ToggleButton("Sync & Activity");
        FontIcon syncIcon = new FontIcon("mdi2h-home-outline");
        syncIcon.setIconSize(20); syncIcon.setIconColor(Color.WHITE);
        btnSync.setToggleGroup(group);  btnSync.setGraphic(syncIcon);
        btnSync.getStyleClass().add("nav-button");
        btnSync.setOnAction(e -> {
            root.setCenter(Dashboard.createMainWorkspaceView());
        });

        // 2. FILES
        ToggleButton btnFiles = new ToggleButton("Files");
        FontIcon filesIcon = new FontIcon("mdi2f-folder");
        filesIcon.setIconSize(20); filesIcon.setIconColor(Color.WHITE);
        btnFiles.setToggleGroup(group); btnFiles.setGraphic(filesIcon); btnFiles.getStyleClass().add("nav-button");
        btnFiles.setOnAction(e -> {
            BorderPane mainArea = new BorderPane();
            mainArea.setTop(FilesUI.createTopHeroBanner());
            VBox contentLayout = new VBox(20);
            contentLayout.setPadding(new Insets(20, 0, 0, 0)); // Clean spacing gap split separating the top banner and content body

            VBox filesPane = FilesUI.Area();
            VBox.setVgrow(filesPane, Priority.ALWAYS);
            contentLayout.getChildren().add(filesPane);
            mainArea.setCenter(contentLayout);
            mainArea.setPadding(new Insets(24));


            root.setCenter(mainArea);
        });

        // 3. PERMISSIONS
        ToggleButton btnPermission = new ToggleButton("Permissions");
        FontIcon permissionIcon = new FontIcon("mdi2l-link-lock");
        permissionIcon.setIconSize(20); permissionIcon.setIconColor(Color.WHITE);
        btnPermission.setToggleGroup(group); btnPermission.setGraphic(permissionIcon); btnPermission.getStyleClass().add("nav-button");
        btnPermission.setOnAction(e -> root.setCenter(Permissions.permissionsPage()));

        // 4. HISTORY
        ToggleButton btnHistory = new ToggleButton("History");
        FontIcon historyIcon = new FontIcon("mdi2r-refresh");
        historyIcon.setIconSize(20); historyIcon.setIconColor(Color.WHITE);
        btnHistory.setToggleGroup(group); btnHistory.setGraphic(historyIcon); btnHistory.getStyleClass().add("nav-button");
        btnHistory.setOnAction(e -> root.setCenter(History.historyPage()));

        // 5. AUTHENTICATION TOGGLE BUTTON
        ToggleButton btnAuth = new ToggleButton();
        btnAuth.setToggleGroup(group);
        btnAuth.setSelected(true);
        btnAuth.getStyleClass().add("nav-button");
        
        FontIcon authIcon = new FontIcon();
        authIcon.setIconSize(20);
        authIcon.setIconColor(Color.WHITE);
        btnAuth.setGraphic(authIcon);

        // Configure authentication button look and action strictly via active session indicators
        if (SessionManager.isLoggedIn()) {
            btnAuth.setText("Logout");
            authIcon.setIconCode(org.kordamp.ikonli.materialdesign2.MaterialDesignL.LOGOUT);
            btnAuth.setOnAction(e -> {
                SessionManager.logout();
                Platform.runLater(() -> {
                    root.setLeft(Sidebar.createsidebar(root));
                    root.setCenter(LoginPage.createLoginPage(root));
                });
            });
        } else {
            btnAuth.setText("Sign In");
            authIcon.setIconCode(org.kordamp.ikonli.materialdesign2.MaterialDesignL.LOGIN);
            btnAuth.setOnAction(e -> {
                root.setCenter(LoginPage.createLoginPage(root));
            });
        }

        // CARDS & CAROUSELS


        VBox promoCard = createPromoCard();
        VBox.setMargin(promoCard, new Insets(10, 0, 0, 0));
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 6. PROFILE SECTION CONTAINER
        HBox profile = createProfileSection();
        logo.getStyleClass().add("brand-label");
        
        sidebar.getChildren().addAll(
            btnSync, btnFiles, btnPermission, btnHistory, btnAuth, 
            promoCard, spacer, profile
        );
        
        sidebar.getStylesheets().add("sidebar.css");
        return sidebar;
    }

    private static HBox createProfileSection() {
        HBox profile = new HBox(12);
        profile.setAlignment(Pos.CENTER_LEFT);
        profile.setStyle("-fx-padding: 10; -fx-background-color: #1A2236; -fx-background-radius: 10;");
        
        Circle avatar = new Circle(15, Color.web("#5C6BC0"));
        VBox nameBox = new VBox(2);
        
        Label name = new Label(SessionManager.isLoggedIn() ? SessionManager.getFullName() : "Guest User");
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

        Label email = new Label(SessionManager.isLoggedIn() ? SessionManager.getEmail() : "Not signed in");
        email.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10;");

        nameBox.getChildren().addAll(name, email);
        profile.getStyleClass().add("profile-box");
        profile.getChildren().addAll(avatar, nameBox);
        return profile;
    }

    
    private static VBox createPromoCard() { 
        VBox card = new VBox(12); card.getStyleClass().add("promo-card"); card.setAlignment(Pos.CENTER); card.setPadding(new Insets(20)); 
        FontIcon cloudIcon = new FontIcon("mdi2c-cloud-lock"); cloudIcon.setIconSize(40); cloudIcon.setIconColor(Color.WHITE);
        Label promoText = new Label("Keep your files safe"); promoText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;"); 
        Button learnMore = new Button("Learn more →"); learnMore.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 11;");
        learnMore.setOnAction(e->{
            showCloudBackupInfoDialog();
        });
        card.getChildren().addAll(cloudIcon, promoText, learnMore); return card; 
    }
    private static void showCloudBackupInfoDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setResizable(false);


        StackPane iconWrap = new StackPane();
        iconWrap.setMinSize(64, 64);
        iconWrap.setMaxSize(64, 64);
        iconWrap.setStyle(
                "-fx-background-color: rgba(56,130,221,0.15);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(99,155,235,0.3);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;"
        );
        FontIcon icon = new FontIcon("mdi2c-cloud-lock");
        icon.setIconSize(28);
        icon.setIconColor(Color.web("#7ab4f0"));
        iconWrap.getChildren().add(icon);


        Label title = new Label("Keep your files safe");
        title.setStyle(
                "-fx-text-fill: #e2eaf6;" +
                        "-fx-font-size: 17;" +
                        "-fx-font-weight: bold;"
        );

        // Body
        Label body = new Label(
                "Your files are automatically encrypted and backed up\n" +
                        "to the cloud. Access them from anywhere, anytime —\n" +
                        "nothing gets lost."
        );
        body.setStyle("-fx-text-fill: #7a93b4; -fx-font-size: 13;");
        body.setAlignment(Pos.CENTER);
        body.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Divider


        // Buttons
        Button dismiss = new Button("Dismiss");
        dismiss.setPrefWidth(120);
        dismiss.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: rgba(99,155,235,0.25);" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-text-fill: #7a93b4;" +
                        "-fx-font-size: 13;" +
                        "-fx-padding: 10 0;"
        );

        Button gotIt = new Button("Got it");
        gotIt.setPrefWidth(120);
        gotIt.setStyle(
                "-fx-background-color: rgba(56,130,221,0.2);" +
                        "-fx-border-color: rgba(99,155,235,0.4);" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-text-fill: #7ab4f0;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 0;"
        );

        dismiss.setOnAction(e -> dialog.close());
        gotIt.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, dismiss, gotIt);
        buttons.setAlignment(Pos.CENTER);

        // Layout
        VBox layout = new VBox(0);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(32, 32, 24, 32));
        layout.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e293b, #0f2744);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: rgba(99,155,235,0.18);" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 0.5;"
        );

        VBox.setMargin(iconWrap,  new Insets(0, 0, 20, 0));
        VBox.setMargin(title,     new Insets(0, 0, 8, 0));
        VBox.setMargin(body,      new Insets(0, 0, 24, 0));


        layout.getChildren().addAll(iconWrap, title, body, buttons);

        Scene scene = new Scene(layout, 400, 300);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.show();
    }
}
