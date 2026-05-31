package ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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

    public static HBox FilesHeader() {
        Label label = new Label("Files");
        label.getStyleClass().add("page-title");

        TextField search = new TextField();
        search.setPromptText("Search files or folders...");
        search.getStyleClass().add("search-field");
        search.setPrefWidth(260);

        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.getStyleClass().add("search-icon");
        HBox searchBox = new HBox(8, searchIcon, search);
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);


        bellIcon.getStyleClass().add("notif-icon");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16);
        header.getStyleClass().add("top-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(label, spacer, searchBox, bellIcon);

        return header;
    }
    public static VBox Area(){
        VBox main=new VBox(20);
        main.setPadding(new Insets(24));

        Label welcome=new Label("Welcome");
        welcome.getStyleClass().add("welcome-label");
        Label desc=new Label("Manage your cloud workspace and collaborate in real-time");
        desc.getStyleClass().add("desc-label");

        HBox allfiles=new HBox();
        allfiles.setPadding(new Insets(10,0,0,0));
        Label afileslabel=new Label("All Files");
        afileslabel.getStyleClass().add("all-files-label");
        allfiles.getChildren().addAll(afileslabel);

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

        TableColumn<FileEntry,String> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col->new TableCell<>(){
            private final Hyperlink dellink=new Hyperlink("Delete");
            private final Hyperlink link=new Hyperlink("Download");{
                dellink.setStyle("-fx-text-fill:red");
                link.setOnAction(e -> {
                    FileEntry entry = getTableView().getItems().get(getIndex());
                    String filename=entry.nameProperty().get();
                    link.setDisable(true);
                    link.setText("Downloading...");
                    Thread thread=new Thread(()->{
                        FileTransferService service=new FileTransferService();
                        try {
                            service.downloadFromServer(filename);
                            Platform.runLater(()->{
                                link.setText("Downloaded");
                                link.setStyle("-fx-text-fill:green");
                                link.setDisable(false);
                            });
                        } catch (IOException ex) {
                            Platform.runLater(()->{
                                link.setText("Failed");
                                link.setStyle("-fx-text-fill: red");
                                link.setDisable(false);

                            });
                            throw new RuntimeException(ex);
                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                });

               dellink.setOnAction(e->{
                   FileEntry entry=getTableView().getItems().get(getIndex());
                   String filename=entry.nameProperty().get();
                   dellink.setDisable(true);
                   dellink.setText("Deleting...");
                   Thread thread=new Thread(()->{
                       FileTransferService service=new FileTransferService();
                       try{
                           service.deleteFromServer(filename);
                           Platform.runLater(()->{
                            table.getItems().remove(entry);
                            table.refresh();

                           });
                       } catch (IOException ex) {
                           Platform.runLater(()->{
                               dellink.setText("Delete");
                               dellink.setStyle("-fx-text-fill: red");
                               dellink.setDisable(false);
                           });
                           throw new RuntimeException(ex);
                       }
                   });
                   thread.setDaemon(true);
                   thread.start();
               });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty? null: new HBox(10,link,dellink));
            }
        });

        table.getColumns().addAll(nameCol, sizeCol, modifiedCol, actionCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        Button upload=Upload(table);
        HBox buttonBox=new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(upload);


        main.getChildren().addAll(welcome,desc,allfiles,table,buttonBox);

        return main;
    }
    public static Button Upload(TableView<FileEntry> table){
        FontIcon uploadicon=new FontIcon("fas-plus");
        uploadicon.setIconSize(20);
        uploadicon.setIconColor(Color.WHITE);

        Button upload = new Button();
        upload.setGraphic(uploadicon);
        upload.setPadding(new Insets(10,20,10,20));
        upload.getStyleClass().add("upload-btn");
        upload.setAlignment(Pos.BASELINE_LEFT);
        upload.setOnAction(e->{
            FileTransferService service=new FileTransferService();
            try{
                FileChooser fileChooser=new FileChooser();
                Stage stage = (Stage) upload.getScene().getWindow();
                File file= fileChooser.showOpenDialog(stage);
                if (file!=null){
                    service.uploadFileToServer(file);
                    Platform.runLater(()->loadFiles(table));
                    Label l=new Label(service.response+ file.getName());
                    l.setStyle("-fx-text-fill: white;");
                    VBox card = new VBox(l);
                    card.setPadding(new Insets(16, 16, 16, 16));
                    card.setStyle("""
                                -fx-background-color: #0F1B2D;
                                -fx-background-radius: 8; 
                            """);
                    Popup popup=new Popup();
                    popup.getContent().add(card);
                    popup.setAutoHide(true);
                    popup.show(stage);
/*
                    FileEntry entry=new FileEntry(file.getName(),file.length()+"bytes",new java.util.Date(file.lastModified()).toString(),"Download");
                    table.getItems().add(entry);*/
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        return upload;
    }

    private static void loadFiles(TableView<FileEntry> table) {
        Thread thread = new Thread(() -> {
            FileTransferService service = new FileTransferService();
            try {
                List<FileEntry> files = service.fetchFilesFromServer();
                Platform.runLater(() -> {
                    table.getItems().clear();
                    table.getItems().setAll(files);
                    table.refresh();});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}