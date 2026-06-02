package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import service.FileTransferService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class History {

    private static final StackPane headerCardContainer = new StackPane();

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

    public static StackPane createTopHeroBanner() {
        Label brandTitle = new Label("History");
        brandTitle.setStyle("-fx-font-size: 35; -fx-font-weight: 900; -fx-text-fill: white;");

        Label subdesc = new Label("Access different Versions");
        subdesc.setStyle("-fx-text-fill: white;");

        VBox textLayout = new VBox(brandTitle, subdesc);
        textLayout.setAlignment(Pos.CENTER_LEFT);
        textLayout.setPadding(new Insets(0, 0, 0, 32));

        ImageView logo = new ImageView();
        try {
            Image image = new Image(History.class.getResourceAsStream("/img_2.png"));
            logo.setImage(image);
            logo.setFitWidth(160);
            logo.setPreserveRatio(true);
        } catch (Exception ignored) {}

        StackPane.setAlignment(textLayout, Pos.CENTER_LEFT);
        StackPane.setAlignment(logo, Pos.CENTER_RIGHT);
        StackPane.setMargin(logo, new Insets(0, 32, 0, 0));

        StackPane banner = new StackPane(textLayout, logo);
        banner.setPrefHeight(160);
        banner.setStyle("-fx-background-color: linear-gradient(to right, #1E293B, #0F172A); -fx-background-radius: 12;");

        return banner;
    }

    public static HBox fileHeaderCard(String masterFilename, String meta) {
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

        Label fileName = new Label(masterFilename);
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
        
        if ("No file selected".equals(masterFilename)) {
            downloadBtn.setDisable(true);
        }

        downloadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Latest File Version");
            fileChooser.setInitialFileName(masterFilename);
            
            Stage stage = (Stage) downloadBtn.getScene().getWindow();
            File dest = fileChooser.showSaveDialog(stage);
            
            if (dest != null) {
                downloadBtn.setDisable(true);
                downloadBtn.setText("Downloading...");
                
                Thread t = new Thread(() -> {
                    FileTransferService service = new FileTransferService();
                    try {
                        service.downloadFromServer(masterFilename, dest);
                        Platform.runLater(() -> {
                            downloadBtn.setText("Downloaded");
                            downloadBtn.setDisable(false);
                        });
                    } catch (IOException ex) {
                        Platform.runLater(() -> {
                            downloadBtn.setText("Download Failed");
                            downloadBtn.setDisable(false);
                        });
                        ex.printStackTrace();
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });

        actions.getChildren().add(downloadBtn);
        card.getChildren().addAll(iconBox, info, spacer, actions);

        return card;
    }

    public static VBox versionHistoryCard() {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(22));

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Version History");
        title.getStyleClass().add("section-title");

        ComboBox<String> fileDropdown = new ComboBox<>();
        fileDropdown.setPromptText("Select a file...");

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

        VBox versions = new VBox();
        Label placeholder = new Label("Select a file to view its version history");
        placeholder.getStyleClass().add("activity-time");
        versions.getChildren().add(placeholder);

        fileDropdown.setOnAction(e -> {
            String selected = fileDropdown.getValue();
            if (selected == null) return;
            versions.getChildren().clear();

            Label loading = new Label("Loading versions...");
            loading.getStyleClass().add("activity-time");
            versions.getChildren().add(loading);

            headerCardContainer.getChildren().setAll(fileHeaderCard(selected, "Loading..."));

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

                        Map<String, String> latest = versionList.get(0);
                        String meta = latest.get("size") + "   •   Uploaded " + latest.get("uploadedAt");
                        
                        headerCardContainer.getChildren().setAll(fileHeaderCard(selected, meta));

                        for (int i = 0; i < versionList.size(); i++) {
                            Map<String, String> v = versionList.get(i);
                            
                            versions.getChildren().add(createVersionRow(
                                    "Version " + v.get("version"),
                                    v.get("uploadedAt"),
                                    v.get("size"),
                                    i == 0,
                                    null,
                                    v.get("filename"),
                                    selected 
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
                    ex.printStackTrace();
                }
            });
            t.setDaemon(true);
            t.start();
        });

        card.getChildren().addAll(topRow, versions);
        return card;
    }

    private static HBox createVersionRow(
            String version, String date, String size, boolean current, String note, String storageFilename, String masterFilename) {

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

        Label sizeLabel = new Label(size);
        sizeLabel.getStyleClass().add("activity-detail");
        sizeLabel.setPrefWidth(100);

        HBox actions = new HBox(24);
        actions.setAlignment(Pos.CENTER_RIGHT);

        FontIcon downloadIcon = new FontIcon("fas-download");
        downloadIcon.setIconColor(Color.web("#64748B"));
        Button downloadBtn = new Button();
        downloadBtn.setGraphic(downloadIcon);
        downloadBtn.getStyleClass().add("icon-btn");
        
        downloadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Historical File Version");
            fileChooser.setInitialFileName(storageFilename);
            
            Stage stage = (Stage) downloadBtn.getScene().getWindow();
            File dest = fileChooser.showSaveDialog(stage);
            
            if (dest != null) {
                downloadBtn.setDisable(true);
                
                Thread t = new Thread(() -> {
                    FileTransferService service = new FileTransferService();
                    try {
                        service.downloadFromServer(storageFilename, dest);
                        Platform.runLater(() -> {
                            downloadBtn.setDisable(false);
                            downloadIcon.setIconColor(Color.web("#10B981")); 
                        });
                    } catch (IOException ex) {
                        Platform.runLater(() -> {
                            downloadBtn.setDisable(false);
                            downloadIcon.setIconColor(Color.web("#EF4444")); 
                        });
                        ex.printStackTrace();
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });

        actions.getChildren().addAll(downloadBtn);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(leftInfo, sizeLabel, spacer, actions);

        return row;
    }

    public static BorderPane historyPage() {
        BorderPane mainArea = new BorderPane();

        mainArea.setTop(createTopHeroBanner());

        VBox contentLayout = new VBox(20);
        contentLayout.setPadding(new Insets(20, 0, 0, 0));

        VBox centerContentWrapper = new VBox(24);
        headerCardContainer.getChildren().setAll(fileHeaderCard("No file selected", "Select a file from the dropdown"));
        
        VBox filesPane = versionHistoryCard();
        VBox.setVgrow(filesPane, Priority.ALWAYS);

        centerContentWrapper.getChildren().addAll(headerCardContainer, filesPane);
        contentLayout.getChildren().add(centerContentWrapper);

        mainArea.setCenter(contentLayout);
        mainArea.setPadding(new Insets(24));

        return mainArea;
    }
}
