package ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import service.FileTransferService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FilesUI {
    static FontIcon bellIcon = new FontIcon("fas-bell");
    
    public static class FileEntry {
        private final SimpleStringProperty name;
        private final SimpleStringProperty size;
        private final SimpleStringProperty lastModified;
        private final SimpleStringProperty action;

        public FileEntry(String name, String size, String lastModified, String action) {
            this.name         = new SimpleStringProperty(name);
            this.size         = new SimpleStringProperty(size);
            this.lastModified = new SimpleStringProperty(lastModified);
            this.action       = new SimpleStringProperty(action);
        }

        public SimpleStringProperty nameProperty()         { return name; }
        public SimpleStringProperty sizeProperty()         { return size; }
        public SimpleStringProperty lastModifiedProperty() { return lastModified; }
    }

    public static StackPane createTopHeroBanner() {
        Label brandTitle = new Label("Files");
        brandTitle.setStyle("-fx-font-size: 35 ; -fx-font-weight: 900; -fx-text-fill: white; -fx-letter-spacing: 1px;");
        Label subdesc=new Label("Manage your workspace");
        subdesc.setStyle("-fx-text-fill:white; -fx-letter-spacing: 2px; ");

        VBox textLayout = new VBox(brandTitle, subdesc);
        textLayout.setAlignment(Pos.CENTER_LEFT);
        textLayout.setPadding(new Insets(0, 0, 0, 32));

        ImageView storkLogoView = new ImageView();
        try {
            Image image = new Image(Dashboard.class.getResourceAsStream("/img_2.png"));
            storkLogoView.setImage(image);
            storkLogoView.setFitWidth(160);
            storkLogoView.setPreserveRatio(true);
            storkLogoView.setSmooth(true);
        } catch (Exception e) {

        }

        StackPane.setAlignment(textLayout, Pos.CENTER_LEFT);
        StackPane.setAlignment(storkLogoView, Pos.CENTER_RIGHT);
        StackPane.setMargin(storkLogoView, new Insets(0, 32, 0, 0));


        StackPane heroBanner = new StackPane(textLayout, storkLogoView);
        heroBanner.setPrefHeight(160);
        heroBanner.setStyle("-fx-background-color: linear-gradient(to right, #1E293B, #0F172A); -fx-background-radius: 12;");

        return heroBanner;
    }
    public static VBox Area() {
        VBox main = new VBox(20);
        main.setPadding(new Insets(24));

        TableView<FileEntry> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Upload file to continue"));
        loadFiles(table);

        TableColumn<FileEntry, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());

        TableColumn<FileEntry, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(cell -> cell.getValue().sizeProperty());

        TableColumn<FileEntry, String> modifiedCol = new TableColumn<>("Last Modified");
        modifiedCol.setCellValueFactory(cell -> cell.getValue().lastModifiedProperty());

        TableColumn<FileEntry, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink dellink = new Hyperlink("Delete");
            private final Hyperlink link = new Hyperlink("Download"); {
                dellink.setStyle("-fx-text-fill: red");

                link.setOnAction(e -> {
                    FileEntry entry = getTableView().getItems().get(getIndex());
                    String filename = entry.nameProperty().get();
                    
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select Destination Folder to Save File");
                    fileChooser.setInitialFileName(filename);
                    
                    Stage stage = (Stage) link.getScene().getWindow();
                    File destinationFile = fileChooser.showSaveDialog(stage);
                    
                    if (destinationFile != null) {
                        link.setDisable(true);
                        link.setText("Downloading...");
                        
                        Thread thread = new Thread(() -> {
                            FileTransferService service = new FileTransferService();
                            try {
                                // Update your service to accept the picked destination path!
                                service.downloadFromServer(filename, destinationFile);
                                Platform.runLater(() -> {
                                    link.setText("Downloaded");
                                    link.setStyle("-fx-text-fill: green");
                                    link.setDisable(false);
                                });
                            } catch (IOException ex) {
                                Platform.runLater(() -> {
                                    link.setText("Failed");
                                    link.setStyle("-fx-text-fill: red");
                                    link.setDisable(false);
                                });
                                ex.printStackTrace();
                            }
                        });
                        thread.setDaemon(true);
                        thread.start();
                    }
                });

                dellink.setOnAction(e -> {
                    FileEntry entry = getTableView().getItems().get(getIndex());
                    String filename = entry.nameProperty().get();
                    dellink.setDisable(true);
                    dellink.setText("Deleting...");
                    Thread thread = new Thread(() -> {
                        FileTransferService service = new FileTransferService();
                        try {
                            service.deleteFromServer(filename);
                            Platform.runLater(() -> {
                                table.getItems().remove(entry);
                                table.refresh();
                            });
                        } catch (IOException ex) {
                            Platform.runLater(() -> {
                                dellink.setText("Delete");
                                dellink.setStyle("-fx-text-fill: red");
                                dellink.setDisable(false);
                            });
                            ex.printStackTrace();
                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(10, link, dellink));
            }
        });

        table.getColumns().addAll(nameCol, sizeCol, modifiedCol, actionCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        Button upload = Upload(table);
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(upload);

        main.getChildren().addAll(table, buttonBox);
        return main;
    }

    public static Button Upload(TableView<FileEntry> table) {
        FontIcon uploadicon = new FontIcon("fas-plus");
        uploadicon.setIconSize(20);
        uploadicon.setIconColor(Color.WHITE);

        Button upload = new Button();
        upload.setGraphic(uploadicon);
        upload.setPadding(new Insets(10, 20, 10, 20));
        upload.getStyleClass().add("upload-btn");
        upload.setAlignment(Pos.BASELINE_LEFT);
        
        upload.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            Stage stage = (Stage) upload.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(stage);
            
            if (selectedFile != null) {
                Alert choiceAlert = new Alert(Alert.AlertType.WARNING);
                choiceAlert.setTitle("Upload Visibility Settings");
                choiceAlert.setHeaderText("Choose visibility status for: " + selectedFile.getName());
                choiceAlert.setContentText("Public files are discoverable by everyone on the cluster network.");

                ButtonType btnPublic = new ButtonType("Public Global Access");
                ButtonType btnPrivate = new ButtonType("Private Restrictive");
                ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                choiceAlert.getButtonTypes().setAll(btnPublic, btnPrivate, btnCancel);

                choiceAlert.showAndWait().ifPresent(response -> {
                    if (response == btnCancel) return;
                    
                    boolean isPublicSelected = (response == btnPublic);
                    
                    Thread uploadThread = new Thread(() -> {
                        FileTransferService service = new FileTransferService();
                        try {
                            // Pass your visibility state through to your file transmission client
                            service.uploadFileToServer(selectedFile, isPublicSelected);
                            
                            Platform.runLater(() -> {
                                loadFiles(table);
                                showNotificationPopup(stage, "Successfully uploaded: " + selectedFile.getName());
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> showNotificationPopup(stage, "Upload execution failed!"));
                            ex.printStackTrace();
                        }
                    });
                    uploadThread.setDaemon(true);
                    uploadThread.start();
                });
            }
        });

        return upload;
    }

    private static void showNotificationPopup(Stage stage, String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        VBox card = new VBox(l);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #0F1B2D; -fx-background-radius: 8; -fx-border-color: #38BDF8; -fx-border-radius: 8;");
        Popup popup = new Popup();
        popup.getContent().add(card);
        popup.setAutoHide(true);
        popup.show(stage);
    }

    private static void loadFiles(TableView<FileEntry> table) {
        Thread thread = new Thread(() -> {
            FileTransferService service = new FileTransferService();
            try {
                List<FileEntry> files = service.fetchFilesFromServer();
                Platform.runLater(() -> {
                    table.getItems().clear();
                    table.getItems().setAll(files);
                    table.refresh();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
