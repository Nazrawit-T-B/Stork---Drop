package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

import static ui.Dashboard.*;
import static ui.FilesUI.*;

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


        ToggleButton btnSync = new ToggleButton("Sync & Activity");
        FontIcon syncIcon = new FontIcon("mdi2h-home-outline");
        syncIcon.setIconSize(20);
        syncIcon.setIconColor(Color.WHITE);
        btnSync.setToggleGroup(group);
        btnSync.setSelected(true);
        btnSync.setGraphic(syncIcon);
        btnSync.getStyleClass().add("nav-button");
        btnSync.setOnAction(e -> {
            BorderPane mainArea = new BorderPane();
            mainArea.setTop(SyncActivity());
            VBox leftComponent = new VBox(10);
            leftComponent.getChildren().addAll(/*createSysHealth(), */transfers());
            VBox rightComponent = new VBox(10);
            rightComponent.getChildren().addAll(activeStat(), recent());
            mainArea.setLeft(leftComponent);
            mainArea.setRight(rightComponent);
            mainArea.setPadding(new Insets(24, 24, 24, 24));
            root.setCenter(mainArea);
        });

        ToggleButton btnFiles = new ToggleButton("Files");
        FontIcon filesIcon = new FontIcon("mdi2f-folder");
        filesIcon.setIconSize(20);
        filesIcon.setIconColor(Color.WHITE);
        btnFiles.setToggleGroup(group);
        btnFiles.setGraphic(filesIcon);
        btnFiles.getStyleClass().add("nav-button");
        btnFiles.setOnAction(e -> {
            BorderPane mainArea = new BorderPane();
            mainArea.setTop(FilesHeader());
            mainArea.setCenter(Area());
            root.setCenter(mainArea);
        });

        // 3. PERMISSIONS
        ToggleButton btnPermission = new ToggleButton("Permissions");
        FontIcon permissionIcon = new FontIcon("mdi2l-link-lock");
        permissionIcon.setIconSize(20);
        permissionIcon.setIconColor(Color.WHITE);
        btnPermission.setToggleGroup(group);
        btnPermission.setGraphic(permissionIcon);
        btnPermission.getStyleClass().add("nav-button");
        btnPermission.setOnAction(e -> root.setCenter(Permissions.permissionsPage()));

        // 4. HISTORY
        ToggleButton btnHistory = new ToggleButton("History");
        FontIcon historyIcon = new FontIcon("mdi2r-refresh");
        historyIcon.setIconSize(20);
        historyIcon.setIconColor(Color.WHITE);
        btnHistory.setToggleGroup(group);
        btnHistory.setGraphic(historyIcon);
        btnHistory.getStyleClass().add("nav-button");
        btnHistory.setOnAction(e -> root.setCenter(History.historyPage()));

        // 5. AUTHENTICATION SECTION (SIGN IN / LOGOUT TOGGLE)
        ToggleButton btnAuth = new ToggleButton();
        btnAuth.setToggleGroup(group);
        btnAuth.getStyleClass().add("nav-button");
        
        FontIcon authIcon = new FontIcon();
        authIcon.setIconSize(20);
        authIcon.setIconColor(Color.WHITE);
        btnAuth.setGraphic(authIcon);

        // REAL-TIME LISTENER: Runs automatically whenever someone logs in or logs out
        SessionManager.loggedInProperty().addListener((observable, wasLoggedIn, isLoggedIn) -> {
            if (isLoggedIn) {
                btnAuth.setText("Logout");
                authIcon.setIconCode(org.kordamp.ikonli.materialdesign2.MaterialDesignL.LOGOUT);
                btnAuth.setOnAction(e -> {
                    SessionManager.logout();
                    updateSidebarUi(root);
                    SceneManager.showDashboard();
                });
            } else {
                btnAuth.setText("Sign In");
                authIcon.setIconCode(org.kordamp.ikonli.materialdesign2.MaterialDesignL.LOGIN);
                btnAuth.setOnAction(e -> {
                    root.setCenter(LoginPage.createLoginPage(root));
                });
            }
        });

        // Establish initial startup visibility state configuration parameters
        if (SessionManager.isLoggedIn()) {
            btnAuth.setText("Logout");
            authIcon.setIconCode(org.kordamp.ikonli.materialdesign2.MaterialDesignL.LOGOUT);
            btnAuth.setOnAction(e -> {
                SessionManager.logout();
                updateSidebarUi(root);
                SceneManager.showDashboard();
            });
        } else {
            btnAuth.setText("Sign In");
            authIcon.setIconCode(org.kordamp.ikonli.materialdesign2.MaterialDesignL.LOGIN);
            btnAuth.setOnAction(e -> {
                root.setCenter(LoginPage.createLoginPage(root));
            });
        }

        // CARDS & CAROUSELS
        VBox storageCard = createStorageCard();
        VBox.setMargin(storageCard, new Insets(10, 0, 0, 0));

        VBox promoCard = createPromoCard();
        VBox.setMargin(promoCard, new Insets(10, 0, 0, 0));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 6. PROFILE SECTION
        HBox profile = createProfileSection();
        logo.getStyleClass().add("brand-label");
        
        sidebar.getChildren().addAll(
            btnSync, btnFiles, btnPermission, btnHistory, btnAuth, 
            storageCard, promoCard, spacer, profile
        );
        
        sidebar.getStylesheets().add("sidebar.css");
        return sidebar;
    }

    private static VBox createStorageCard() {
        VBox card = new VBox(10); card.getStyleClass().add("storage-card"); card.setPadding(new Insets(15));
        Label title = new Label("Storage"); title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13;");
        Label stats = new Label("42.8 GB of 100 GB used"); stats.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11;");
        ProgressBar pb = new ProgressBar(0.42); pb.setMaxWidth(Double.MAX_VALUE); pb.setPrefHeight(6);
        title.getStyleClass().add("storage-card-title"); stats.getStyleClass().add("storage-card-stats");
        card.getChildren().addAll(title, stats, pb); return card;
    }

    private static VBox createPromoCard() {
        VBox card = new VBox(12); card.getStyleClass().add("promo-card"); card.setAlignment(Pos.CENTER); card.setPadding(new Insets(20));
        FontIcon cloudIcon = new FontIcon("mdi2r-refresh"); cloudIcon.setIconSize(40); cloudIcon.setIconColor(Color.WHITE);
        Label promoText = new Label("Keep your files safe"); promoText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Button learnMore = new Button("Learn more →"); learnMore.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 11;");
        promoText.getStyleClass().add("promo-card-text"); learnMore.getStyleClass().add("promo-learn-more");
        
        // Modal logic snippet remaining structurally intact...
        learnMore.setOnAction(e -> {
            StackPane overlay = new StackPane(); overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
            VBox modal = new VBox(20); modal.setMaxWidth(400); modal.setMaxHeight(400); modal.getStyleClass().add("card"); modal.setPadding(new Insets(30));
            Button closeBtn = new Button("✕"); closeBtn.getStyleClass().add("btn-cancel-transfer");
            HBox closeRow = new HBox(); closeRow.setAlignment(Pos.CENTER_RIGHT); closeRow.getChildren().add(closeBtn);
            Label title = new Label("How Stork Drop Works"); title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            Label subtitle = new Label("Your files, safely synced across all your devices."); subtitle.setWrapText(true);
            Label description = new Label("1. Upload- Queue files.\n2. Sync- Real-time sync.\n3. Secure- Encrypted.\n4. Share- Manage access."); description.setWrapText(true);
            Button gotItBtn = new Button("Got it"); gotItBtn.setMaxWidth(Double.MAX_VALUE);
            gotItBtn.setStyle("-fx-background-color: #2D7FF9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 0 10 0; -fx-cursor: hand;");
            modal.getChildren().addAll(closeRow, title, subtitle, description, gotItBtn); overlay.getChildren().add(modal);
            StackPane rootNode = (StackPane) learnMore.getScene().getRoot(); rootNode.getChildren().add(overlay);
            closeBtn.setOnAction(ev -> rootNode.getChildren().remove(overlay)); gotItBtn.setOnAction(ev -> rootNode.getChildren().remove(overlay));
            overlay.setOnMouseClicked(ev -> { if (ev.getTarget() == overlay) rootNode.getChildren().remove(overlay); });
        });
        card.getChildren().addAll(cloudIcon, promoText, learnMore); return card;
    }

    private static HBox createProfileSection() {
        HBox profile = new HBox(12);
        profile.setAlignment(Pos.CENTER_LEFT);
        profile.setStyle("-fx-padding: 10; -fx-background-color: #1A2236; -fx-background-radius: 10;");
        Circle avatar = new Circle(15, Color.web("#5C6BC0"));
        VBox nameBox = new VBox(2);
        Label name = new Label(SessionManager.getFullName());
        name.setId("profileNameLabel");
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        Label email = new Label(SessionManager.getEmail());
        email.setId("profileEmailLabel");
        email.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 10;");
        nameBox.getChildren().addAll(name, email);
        profile.getStyleClass().add("profile-box");
        profile.getChildren().addAll(avatar, nameBox);
        return profile;
    }

    public static void updateSidebarUi(BorderPane root) {
        if (root == null || root.getLeft() == null) return;
        Label nameLabel = (Label) root.getLeft().lookup("#profileNameLabel");
        Label emailLabel = (Label) root.getLeft().lookup("#profileEmailLabel");
        if (nameLabel != null) nameLabel.setText(SessionManager.getFullName());
        if (emailLabel != null) emailLabel.setText(SessionManager.getEmail());
    }
}
