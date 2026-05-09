package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

public class History {

    // HEADER

    public static HBox historyHeader() {

        Label title = new Label("History");
        title.getStyleClass().add("page-title");

        // SEARCH
        TextField search = new TextField();
        search.setPromptText("Search files or activity...");
        search.getStyleClass().add("search-field");
        search.setPrefWidth(260);

        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.getStyleClass().add("search-icon");

        HBox searchBox = new HBox(8, searchIcon, search);
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // NOTIFICATION
        FontIcon bellIcon = new FontIcon("fas-bell");
        bellIcon.getStyleClass().add("notif-icon");

        // AVATAR
        Circle avatar = new Circle(18);
        avatar.setFill(Color.web("#F4B183"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(18);
        header.setAlignment(Pos.CENTER_LEFT);

        header.getChildren().addAll(
                title,
                spacer,
                searchBox,
                bellIcon,
                avatar
        );

        return header;
    }

    // FILE HEADER CARD

    public static HBox fileHeaderCard() {

        HBox card = new HBox(20);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER_LEFT);

        // FILE ICON
        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add("history-file-icon-box");

        FontIcon pdfIcon = new FontIcon("fas-file-pdf");
        pdfIcon.setIconSize(34);
        pdfIcon.setIconColor(Color.web("#EF4444"));

        iconBox.getChildren().add(pdfIcon);

        // FILE INFO
        VBox info = new VBox(8);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label fileName = new Label("Project_Spec_v2.pdf");
        fileName.getStyleClass().add("history-file-title");

        Label currentBadge = new Label("CURRENT");
        currentBadge.getStyleClass().add("current-badge");

        topRow.getChildren().addAll(fileName, currentBadge);

        Label meta = new Label(
                "PDF Document   •   4.2 MB   •   Created Oct 12, 2023"
        );
        meta.getStyleClass().add("activity-time");

        info.getChildren().addAll(topRow, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ACTIONS
        HBox actions = new HBox(12);

        FontIcon shareIcon = new FontIcon("fas-share-alt");
        shareIcon.setIconColor(Color.web("#64748B"));

        Button shareBtn = new Button("Share");
        shareBtn.setGraphic(shareIcon);
        shareBtn.getStyleClass().add("history-share-btn");

        FontIcon downloadIcon = new FontIcon("fas-download");
        downloadIcon.setIconColor(Color.WHITE);

        Button downloadBtn = new Button("Download Latest");
        downloadBtn.setGraphic(downloadIcon);
        downloadBtn.getStyleClass().add("history-download-btn");

        actions.getChildren().addAll(shareBtn, downloadBtn);

        card.getChildren().addAll(
                iconBox,
                info,
                spacer,
                actions
        );

        return card;
    }

    // VERSION HISTORY CARD

    public static VBox versionHistoryCard() {

        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(22));

        // TOP ROW
        HBox topRow = new HBox();

        Label title = new Label("Version History");
        title.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox autoSaveBox = new HBox(8);
        autoSaveBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon check = new FontIcon("fas-check-circle");
        check.setIconColor(Color.web("#22C55E"));

        Label autoSave = new Label("Auto-save enabled");
        autoSave.getStyleClass().add("activity-time");

        autoSaveBox.getChildren().addAll(check, autoSave);

        topRow.getChildren().addAll(title, spacer, autoSaveBox);

        VBox versions = new VBox();

        versions.getChildren().addAll(
                createVersionRow(
                        "Version 2.0",
                        "Oct 24, 2023 at 2:25 PM",
                        "Sarah Chen",
                        "SC",
                        "4.2 MB",
                        true,
                        null
                ),

                createVersionRow(
                        "Version 1.2",
                        "Oct 22, 2023 at 11:04 AM",
                        "Marcus Thorne",
                        "MT",
                        "3.8 MB",
                        false,
                        "Updated security requirements"
                ),

                createVersionRow(
                        "Version 1.1",
                        "Oct 18, 2023 at 4:45 PM",
                        "Sarah Chen",
                        "SC",
                        "3.7 MB",
                        false,
                        null
                ),

                createVersionRow(
                        "Version 1.0",
                        "Oct 12, 2023 at 9:00 AM",
                        "Marcus Thorne",
                        "MT",
                        "3.5 MB",
                        false,
                        "Initial upload"
                )
        );

        Button olderBtn = new Button("Show 12 older versions    ▼");
        olderBtn.getStyleClass().add("view-all-link");

        HBox bottom = new HBox(olderBtn);
        bottom.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                topRow,
                versions,
                bottom
        );

        return card;
    }

    // VERSION ROW

    private static HBox createVersionRow(
            String version,
            String date,
            String author,
            String initials,
            String size,
            boolean current,
            String note
    ) {

        HBox row = new HBox(18);
        row.getStyleClass().add("history-version-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(18));

        // VERSION INFO
        VBox leftInfo = new VBox(8);

        HBox versionRow = new HBox(10);
        versionRow.setAlignment(Pos.CENTER_LEFT);

        Label versionLabel = new Label(version);
        versionLabel.getStyleClass().add("activity-actor");

        versionRow.getChildren().add(versionLabel);

        if (current) {
            Label currentBadge = new Label("CURRENT");
            currentBadge.getStyleClass().add("history-current-green");
            versionRow.getChildren().add(currentBadge);
        }

        Label dateLabel = new Label(date);
        dateLabel.getStyleClass().add("activity-time");

        leftInfo.getChildren().addAll(versionRow, dateLabel);

        if (note != null) {
            Label noteLabel = new Label(note);
            noteLabel.getStyleClass().add("activity-time");
            leftInfo.getChildren().add(noteLabel);
        }

        leftInfo.setPrefWidth(320);

        // USER
        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);

        Circle circle = new Circle(16);
        circle.setFill(Color.web("#E2E8F0"));

        Label initialsLabel = new Label(initials);
        initialsLabel.setStyle("-fx-font-size: 10; -fx-font-weight: bold;");

        StackPane avatar = new StackPane(circle, initialsLabel);

        Label authorLabel = new Label(author);
        authorLabel.getStyleClass().add("activity-detail");

        userBox.getChildren().addAll(avatar, authorLabel);

        userBox.setPrefWidth(220);

        // SIZE
        Label sizeLabel = new Label(size);
        sizeLabel.getStyleClass().add("activity-detail");
        sizeLabel.setPrefWidth(100);

        // ACTIONS
        HBox actions = new HBox(24);
        actions.setAlignment(Pos.CENTER_RIGHT);

        FontIcon download = new FontIcon("fas-download");
        download.setIconColor(Color.web("#64748B"));

        FontIcon branch = new FontIcon("fas-code-branch");
        branch.setIconColor(Color.web("#64748B"));

        FontIcon restore = new FontIcon("fas-history");
        restore.setIconColor(Color.web("#64748B"));

        actions.getChildren().addAll(
                download,
                branch,
                restore
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(
                leftInfo,
                userBox,
                sizeLabel,
                spacer,
                actions
        );

        return row;
    }

    // MAIN PAGE

    public static BorderPane historyPage() {

        BorderPane root = new BorderPane();

        root.setTop(historyHeader());

        VBox center = new VBox(24);
        center.setPadding(new Insets(24));

        center.getChildren().addAll(
                fileHeaderCard(),
                versionHistoryCard()
        );

        root.setCenter(center);

        return root;
    }
}