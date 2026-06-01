package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dashboard {

    // Containers for functional tracking
    public record SharedFile(String filename, String ownerName, String size) {}

    // Callback event listener definitions
    public interface DownloadHandler { void onDownload(SharedFile file, File destinationPath); }
    public interface RefreshHandler { void onRefresh(); }

    // Screen State Containers
    private static List<SharedFile> globalSharedFiles = new ArrayList<>();
    private static final List<String> runtimeNotifications = new ArrayList<>();

    // Re-renderable Layout Sub-containers
    private static final VBox discoveryFilesContainer = new VBox(8);
    private static final Label notificationBadge = new Label("0");
    private static TextField searchField;

    // Operational Handlers
    private static DownloadHandler downloadHandler;
    private static RefreshHandler refreshHandler;

    public static void setHandlers(DownloadHandler dl, Object ignoredPing, RefreshHandler refresh) {
        downloadHandler = dl;
        refreshHandler = refresh;
    }

    /**
     * Safely populates background threads discoveries straight into layout view items
     */
    public static void updateDashboardData(List<SharedFile> files, List<Object> ignoredPeers) {
        Platform.runLater(() -> {
            globalSharedFiles = new ArrayList<>(files);
            String currentSearch = (searchField != null) ? searchField.getText() : "";
            renderDiscoveryFeed(currentSearch);
        });
    }

    public static void pushNotification(String message) {
        Platform.runLater(() -> {
            runtimeNotifications.add(message);
            int count = runtimeNotifications.size();
            notificationBadge.setText(String.valueOf(count));
            notificationBadge.setVisible(count > 0);
        });
    }

  
    public static StackPane createTopHeroBanner() {
        // Left Side: Bold Title text layout
        Label brandTitle = new Label("Sync & Activity");
        brandTitle.setStyle("-fx-font-size: 35 ; -fx-font-weight: 900; -fx-text-fill: white; -fx-letter-spacing: 1px;");

        VBox textLayout = new VBox(brandTitle);
        textLayout.setAlignment(Pos.CENTER_LEFT);
        textLayout.setPadding(new Insets(0, 0, 0, 32));

        ImageView storkLogoView = new ImageView();
        try {
            Image image = new Image(Dashboard.class.getResourceAsStream("/img_2.png"));
            storkLogoView.setImage(image);
            storkLogoView.setFitWidth(160); // Increased sizing for a true oversized hero aesthetic
            storkLogoView.setPreserveRatio(true);
            storkLogoView.setSmooth(true);
        } catch (Exception e) {

        }
        
        StackPane.setAlignment(textLayout, Pos.CENTER_LEFT);
        StackPane.setAlignment(storkLogoView, Pos.CENTER_RIGHT);
        StackPane.setMargin(storkLogoView, new Insets(0, 32, 0, 0));

        // High-end background gradient profile match
        StackPane heroBanner = new StackPane(textLayout, storkLogoView);
        heroBanner.setPrefHeight(160); // Enlarged container frame footprint height
        heroBanner.setStyle("-fx-background-color: linear-gradient(to right, #1E293B, #0F172A); -fx-background-radius: 12;");
        
        return heroBanner;
    }

    public static VBox publicSharedDiscoveriesPanel() {
        VBox component = new VBox(12);
        component.getStyleClass().add("card");
        component.setPadding(new Insets(24));

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label desc = new Label("Shared Discoveries");
        desc.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Dynamic Search Field Box Control
        searchField = new TextField();
        searchField.setPromptText("Search files...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> renderDiscoveryFeed(newVal));

        FontIcon refreshIcon = new FontIcon("fas-sync");
        refreshIcon.setIconSize(12);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setGraphic(refreshIcon);
        refreshBtn.getStyleClass().add("btn-pause");
        refreshBtn.setOnAction(e -> {
            if (refreshHandler != null) refreshHandler.onRefresh();
        });

        titleRow.getChildren().addAll(desc, spacer, searchField, refreshBtn);
        titleRow.setSpacing(14);
        discoveryFilesContainer.setPadding(new Insets(5, 0, 5, 0));

        ScrollPane scrollPane = new ScrollPane(discoveryFilesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(460); // Tall structural viewport layout allocation frame
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-viewport-background: transparent;");

        component.getChildren().addAll(titleRow, scrollPane);
        return component;
    }


    private static void renderDiscoveryFeed(String filterText) {
        discoveryFilesContainer.getChildren().clear();
        String query = filterText.toLowerCase().trim();

        List<SharedFile> activeList = globalSharedFiles.stream()
                .filter(f -> query.isEmpty() || f.filename().toLowerCase().contains(query) || f.ownerName().toLowerCase().contains(query))
                .toList();

        if (activeList.isEmpty()) {
            Label blankLabel = new Label("No shared file records found.");
            blankLabel.setStyle("-fx-text-fill: #64748B; -fx-font-style: italic; -fx-padding: 16;");
            discoveryFilesContainer.getChildren().add(blankLabel);
            return;
        }

        for (SharedFile file : activeList) {
            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color: #162235; -fx-background-radius: 6;");

            FontIcon fileIcon = new FontIcon("fas-file-download");
            fileIcon.setIconSize(18);
            fileIcon.setStyle("-fx-icon-color: #38BDF8;");

            VBox textMeta = new VBox(4);
            Label nameLabel = new Label(file.filename());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13;");
            Label ownerLabel = new Label("Origin Node: " + file.ownerName() + " • " + file.size());
            ownerLabel.setStyle("-fx-text-fill: #A0AEC0; -fx-font-size: 11;");
            textMeta.getChildren().addAll(nameLabel, ownerLabel);

            Region innerSpacer = new Region();
            HBox.setHgrow(innerSpacer, Priority.ALWAYS);

            Hyperlink downloadLink = new Hyperlink("Download");
            downloadLink.setStyle("-fx-text-fill: #38BDF8; -fx-underline: false; -fx-font-weight: bold;");
            downloadLink.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Target Network Object");
                fileChooser.setInitialFileName(file.filename());
                
                Stage activeStage = (Stage) downloadLink.getScene().getWindow();
                File destination = fileChooser.showSaveDialog(activeStage);

                if (destination != null && downloadHandler != null) {
                    downloadLink.setDisable(true);
                    downloadLink.setText("Streaming...");
                    downloadHandler.onDownload(file, destination);
                }
            });

            row.getChildren().addAll(fileIcon, textMeta, innerSpacer, downloadLink);
            discoveryFilesContainer.getChildren().add(row);
        }
    }


    public static BorderPane createMainWorkspaceView() {
        BorderPane mainArea = new BorderPane();
        
        mainArea.setTop(createTopHeroBanner());
        VBox contentLayout = new VBox(20);
        contentLayout.setPadding(new Insets(20, 0, 0, 0)); // Clean spacing gap split separating the top banner and content body
        
        VBox filesPane = publicSharedDiscoveriesPanel();
        VBox.setVgrow(filesPane, Priority.ALWAYS);
        contentLayout.getChildren().add(filesPane);
        
        mainArea.setCenter(contentLayout);
        mainArea.setPadding(new Insets(24));
        return mainArea;
    }

    public static BorderPane createDashboard() {
        BorderPane root = new BorderPane();
        VBox sidebar = Sidebar.createsidebar(root);
        root.setLeft(sidebar);
        root.setCenter(createMainWorkspaceView());

        Platform.runLater(() -> {
            if (refreshHandler != null) {
                refreshHandler.onRefresh();
            }
        });
        return root;
    }
}
