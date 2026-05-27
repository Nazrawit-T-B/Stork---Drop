package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

public class Dashboard {

    public static HBox SyncActivity() {
        Label label = new Label("Sync & Activity");
        label.getStyleClass().add("page-title");

        TextField search = new TextField();
        search.setPromptText("Search files or activity...");
        search.getStyleClass().add("search-field");
        search.setPrefWidth(260);

        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.getStyleClass().add("search-icon");
        HBox searchBox = new HBox(8, searchIcon, search);
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon bellIcon = new FontIcon("fas-bell");
        bellIcon.getStyleClass().add("notif-icon");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16);
        header.getStyleClass().add("top-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(label, spacer, searchBox, bellIcon);

        return header;
    }

    public static VBox createSysHealth() {
        VBox component = new VBox(16);
        component.getStyleClass().addAll("card", "health-card");
        component.setPadding(new Insets(20));

        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Image icon = new Image(Dashboard.class.getResourceAsStream("/img_1.png"));
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(48);
        iconView.setPreserveRatio(true);

        VBox topLabels = new VBox(4);
        Label title = new Label("System Health");
        title.getStyleClass().add("card-title");

        HBox subRow = new HBox(8);
        subRow.setAlignment(Pos.CENTER_LEFT);

        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: #10B981; -fx-font-size: 10;");

        Label statLabel = new Label("All systems operational");
        statLabel.getStyleClass().add("health-status-ok");

        Label separator = new Label("•");
        separator.getStyleClass().add("health-latency");

        Label latencyLabel = new Label("12ms Latency");
        latencyLabel.getStyleClass().add("health-latency");

        subRow.getChildren().addAll(dot, statLabel, separator, latencyLabel);
        topLabels.getChildren().addAll(title, subRow);
        topBar.getChildren().addAll(iconView, topLabels);

        HBox storageRow = new HBox();
        storageRow.setAlignment(Pos.CENTER_LEFT);
        storageRow.setSpacing(8);

        Label storageLabel = new Label("42.8 GB of 100 GB used");
        storageLabel.getStyleClass().add("health-storage-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label pctLabel = new Label("42%");
        pctLabel.getStyleClass().add("health-storage-pct");

        storageRow.getChildren().addAll(storageLabel, spacer, pctLabel);

        ProgressBar pb = new ProgressBar(0.42);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(6);

        component.getChildren().addAll(topBar, storageRow, pb);
        return component;
    }


    public static VBox activeStat() {

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Active Transfers");
        titleLabel.getStyleClass().add("transfers-label");
        topRow.getChildren().add(titleLabel);

        Label countLabel = new Label("08");
        countLabel.getStyleClass().add("transfers-count");

        Label descLabel = new Label("Files currently syncing");
        descLabel.getStyleClass().add("transfers-sub");

        Button viewBtn = new Button("View all transfers");
        viewBtn.getStyleClass().add("btn-view-transfers");

        content.getChildren().addAll(topRow, countLabel, descLabel, viewBtn);

        Image image = new Image(Sidebar.class.getResourceAsStream("/img_2.png"));
        ImageView view = new ImageView(image);
        view.setFitWidth(100);
        view.setPreserveRatio(true);
        view.setOpacity(0.35);

        StackPane.setAlignment(view, Pos.TOP_RIGHT);
        StackPane.setMargin(view, new Insets(10, 10, 0, 0));

        StackPane card = new StackPane(view, content);
        card.getStyleClass().add("transfers-highlight-card");
        StackPane.setAlignment(content, Pos.TOP_LEFT);

        VBox wrapper = new VBox(card);
        VBox.setVgrow(card, Priority.ALWAYS);
        return wrapper;
    }

    
    public static VBox transfers() {
        VBox component = new VBox(12);
        component.getStyleClass().add("card");
        component.setPadding(new Insets(20));

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label desc = new Label("Active Transfers");
        desc.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon pauseIcon = new FontIcon("fas-pause");
        pauseIcon.setIconSize(12);
        Button actionBtn = new Button("Pause All");
        actionBtn.setGraphic(pauseIcon);
        actionBtn.getStyleClass().add("btn-pause");

        titleRow.getChildren().addAll(desc, spacer, actionBtn);
        
        VBox files = new VBox(8);
        files.getChildren().addAll(
                createTransferRow("04_Marketing_Campaign.mp4", "2.4 GB • 8 MB/s • 4m left", 0.60, "60%", "fas-film"),
                createTransferRow("Project_Specs_V2.pdf",      "12.5 MB • 12 MB/s • 2s left", 0.85, "85%", "fas-file-pdf"),
                createTransferRow("Hero_Section_Final_v1.png", "4.2 MB • Processing metadata...", 0.20, "20%", "fas-image")
        );

        Button viewAll = new Button("View all transfers  →");
        viewAll.getStyleClass().add("view-all-link");
        HBox viewRow = new HBox();
        viewRow.setAlignment(Pos.CENTER);
        viewRow.getChildren().add(viewAll);

        component.getChildren().addAll(titleRow, files, viewRow);
        return component;
    }


    private static HBox createTransferRow(String name, String meta, double progress, String pct, String iconLiteral) {
        HBox row = new HBox(12);
        row.getStyleClass().add("transfer-row");
        row.setAlignment(Pos.CENTER_LEFT);

        FontIcon fileIcon = new FontIcon(iconLiteral);
        fileIcon.setIconSize(20);
        fileIcon.setStyle("-fx-icon-color: #2D7FF9;");

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("transfer-filename");

        Label metaLabel = new Label(meta);
        metaLabel.getStyleClass().add("transfer-meta");

        ProgressBar pb = new ProgressBar(progress);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(5);

        info.getChildren().addAll(nameLabel, metaLabel, pb);

        Label pctLabel = new Label(pct);
        pctLabel.getStyleClass().add("transfer-pct");

        Button cancelBtn = new Button("✕");
        cancelBtn.getStyleClass().add("btn-cancel-transfer");

        row.getChildren().addAll(fileIcon, info, pctLabel, cancelBtn);
        return row;
    }


    public static VBox recent() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recent Activity");
        title.getStyleClass().add("section-title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("Filter", "Files", "People");
        filter.setValue("Filter");
        filter.getStyleClass().add("filter-combo");

        topRow.getChildren().addAll(title, spacer, filter);

        // Activity rows
        VBox activities = new VBox(4);
        activities.getChildren().addAll(
                createActivityRow("fas-file-alt",      "#2D7FF9", "Alice",   "updated",  "Report.docx",        "2 minutes ago"),
                createActivityRow("fas-sync-alt",      "#64748B", "System",  "synced 5 files to cloud storage", null, "15 minutes ago"),
                createActivityRow("fas-user-plus",     "#10B981", "James Wilson", "joined the", "Design System folder", "1 hour ago"),
                createActivityRow("fas-exclamation-triangle", "#F59E0B", "Conflict detected", "in", "index.html. Two versions saved.", "3 hours ago")
        );

        Button historyBtn = new Button("View full history  →");
        historyBtn.getStyleClass().add("view-all-link");
        HBox viewRow = new HBox();
        viewRow.setAlignment(Pos.CENTER);
        viewRow.getChildren().add(historyBtn);

        card.getChildren().addAll(topRow, activities, viewRow);
        return card;
    }

    
    private static HBox createActivityRow(String iconLiteral, String iconColor,
                                          String actor, String action,
                                          String link, String time) {
        HBox row = new HBox(12);
        row.getStyleClass().add("activity-row");
        row.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(16);
        icon.setStyle("-fx-icon-color: " + iconColor + ";");

        VBox textBox = new VBox(3);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox textRow = new HBox(4);
        textRow.setAlignment(Pos.CENTER_LEFT);
        
        Label actorLabel = new Label(actor);
        actorLabel.getStyleClass().add("activity-actor");

        Label actionLabel = new Label(" " + action + " ");
        actionLabel.getStyleClass().add("activity-detail");

        textRow.getChildren().addAll(actorLabel, actionLabel);

        if (link != null) {
            Label linkLabel = new Label(link);
            linkLabel.getStyleClass().add("activity-link");
            textRow.getChildren().add(linkLabel);
        }

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("activity-time");

        textBox.getChildren().addAll(textRow, timeLabel);
        row.getChildren().addAll(icon, textBox);
        return row;
    }
    public static BorderPane createDashboard() {
        BorderPane root = new BorderPane();

        VBox sidebar = Sidebar.createsidebar(root);
        root.setLeft(sidebar);

        BorderPane mainArea = new BorderPane();
        mainArea.setTop(SyncActivity());
        VBox leftComponent = new VBox(10);
        leftComponent.getChildren().addAll(createSysHealth(), transfers());
        VBox rightComponent = new VBox(10);
        rightComponent.getChildren().addAll(activeStat(), recent());

        mainArea.setLeft(leftComponent);
        mainArea.setRight(rightComponent);
        mainArea.setPadding(new Insets(24, 24, 24, 24));
        root.setCenter(mainArea);

        return root;
    }
}
