package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;
import service.FileTransferService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class History {

    // HEADER
    public static HBox historyHeader() {
        Label title = new Label("History");
        title.getStyleClass().add("page-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(18);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(title, spacer);

        return header;
    }

    // FILE HEADER CARD — dynamic
    public static HBox fileHeaderCard(String filename, String meta) {
        HBox card = new HBox(20);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add("history-file-icon-box");

        FontIcon pdfIcon = new FontIcon("fas-file");
        pdfIcon.setIconSize(34);
        pdfIcon.setIconColor(Color.web("#2D7FF9"));
        iconBox.getChildren().add(pdfIcon);

        VBox info = new VBox(8);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label fileName = new Label(filename);
        fileName.getStyleClass().add("history-file-title");

        Label currentBadge = new Label("CURRENT");
        currentBadge.getStyleClass().add("current-badge");

        topRow.getChildren().addAll(fileName, currentBadge);

        Label metaLabel = new Label(meta);
        metaLabel.getStyleClass().add("activity-time");

        info.getChildren().addAll(topRow, metaLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(12);

        FontIcon downloadIcon = new FontIcon("fas-download");
        downloadIcon.setIconColor(Color.WHITE);

        Button downloadBtn = new Button("Download Latest");
        downloadBtn.setGraphic(downloadIcon);
        downloadBtn.getStyleClass().add("history-download-btn");
        downloadBtn.setOnAction(e -> {
            Thread t = new Thread(() -> {
                FileTransferService service = new FileTransferService();
                try {
                    service.downloadFromServer(filename);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            t.setDaemon(true);
            t.start();
        });

        actions.getChildren().add(downloadBtn);
        card.getChildren().addAll(iconBox, info, spacer, actions);

        return card;
    }

    // VERSION HISTORY CARD — dynamic
    public static VBox versionHistoryCard(VBox pageRoot) {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(22));

        // TOP ROW
        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Version History");
        title.getStyleClass().add("section-title");

        // DROPDOWN
        ComboBox<String> fileDropdown = new ComboBox<>();
        fileDropdown.setPromptText("Select a file...");

        // populate dropdown
        Thread loadThread = new Thread(() -> {
            FileTransferService service = new FileTransferService();
            try {
                List<String> filenames = service.fetchAllFilenames();
                Platform.runLater(() -> fileDropdown.getItems().addAll(filenames));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(title, spacer, fileDropdown);

        // VERSIONS LIST
        VBox versions = new VBox();
        Label placeholder = new Label("Select a file to view its version history");
        placeholder.getStyleClass().add("activity-time");
        versions.getChildren().add(placeholder);

        // on file selected
        fileDropdown.setOnAction(e -> {
            String selected = fileDropdown.getValue();
            if (selected == null) return;
            versions.getChildren().clear();

            Label loading = new Label("Loading versions...");
            loading.getStyleClass().add("activity-time");
            versions.getChildren().add(loading);

            // update file header card
            pageRoot.getChildren().set(0, fileHeaderCard(selected, "Loading..."));

            Thread t = new Thread(() -> {
                FileTransferService service = new FileTransferService();
                try {
                    List<Map<String, String>> versionList = service.fetchFileVersions(selected);
                    Platform.runLater(() -> {
                        versions.getChildren().clear();

                        if (versionList.isEmpty()) {
                            versions.getChildren().add(new Label("No versions found"));
                            return;
                        }

                        // update header with real meta
                        Map<String, String> latest = versionList.get(0);
                        String meta = latest.get("size") + "   •   Uploaded " + latest.get("uploadedAt");
                        pageRoot.getChildren().set(0, fileHeaderCard(selected, meta));

                        for (int i = 0; i < versionList.size(); i++) {
                            Map<String, String> v = versionList.get(i);
                            versions.getChildren().add(createVersionRow(
                                    "Version " + v.get("version"),
                                    v.get("uploadedAt"),
                                    v.get("size"),
                                    i==0,
                                    null,
                                    v.get("filename")
                            ));
                        }
                    });
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        versions.getChildren().clear();
                        Label error = new Label("Failed to load versions");
                        error.setStyle("-fx-text-fill: red;");
                        versions.getChildren().add(error);
                    });
                }
            });
            t.setDaemon(true);
            t.start();
        });

        card.getChildren().addAll(topRow, versions);
        return card;
    }

    // VERSION ROW
    private static HBox createVersionRow(
            String version, String date, String size, boolean current, String note,String filename) {

        HBox row = new HBox(18);
        row.getStyleClass().add("history-version-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(18));

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

        // SIZE
        Label sizeLabel = new Label(size);
        sizeLabel.getStyleClass().add("activity-detail");
        sizeLabel.setPrefWidth(100);

        // ACTIONS
        HBox actions = new HBox(24);
        actions.setAlignment(Pos.CENTER_RIGHT);

        FontIcon downloadIcon = new FontIcon("fas-download");
        downloadIcon.setIconColor(Color.web("#64748B"));
        Button downloadBtn = new Button();
        downloadBtn.setGraphic(downloadIcon);
        downloadBtn.getStyleClass().add("icon-btn");
        downloadBtn.setOnAction(e -> {
            Thread t = new Thread(() -> {
                FileTransferService service = new FileTransferService();
                try {
                    service.downloadFromServer(filename);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            t.setDaemon(true);
            t.start();
        });


        actions.getChildren().addAll(downloadBtn);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(leftInfo, sizeLabel, spacer, actions);

        return row;
    }

    // MAIN PAGE
    public static BorderPane historyPage() {
        BorderPane root = new BorderPane();
        root.setTop(historyHeader());

        VBox center = new VBox(24);
        center.setPadding(new Insets(24));

        // placeholder header card until file is selected
        HBox headerCard = fileHeaderCard("No file selected", "Select a file from the dropdown");
        VBox versionCard = versionHistoryCard(center);

        center.getChildren().addAll(headerCard, versionCard);
        root.setCenter(center);

        return root;
    }
}