package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import service.FileTransferService;


import java.io.File;
import java.util.Stack;


public class FilesUI {
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
        public SimpleStringProperty actionProperty()       { return action; }
    }
    public static HBox FilesHeader() {
        Label label = new Label("Files");
        label.getStyleClass().add("page-title");

        // Search field with icon
        TextField search = new TextField();
        search.setPromptText("Search files or folders...");
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
    public static VBox Area(){
       VBox main=new VBox(20);
       main.setPadding(new Insets(24));

       Label welcome=new Label("Welcome back,Alice");
       Label desc=new Label("Manage your cloud workspace and collaborate in real-time");

       HBox recentfiles=new HBox(400);
       recentfiles.setAlignment(Pos.CENTER_LEFT);
       Label filelabel=new Label("Recent Files");
        //Region spacer = new Region();
        //HBox.setHgrow(spacer, Priority.ALWAYS);
       Hyperlink link=new Hyperlink("View all");
       recentfiles.getChildren().addAll(filelabel,link);

       HBox allfiles=new HBox();
       Label afileslabel=new Label("All Files");
       allfiles.getChildren().addAll(afileslabel);

        TableView<FileEntry> table = new TableView<>();
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FileEntry, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());

        TableColumn<FileEntry, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(cell -> cell.getValue().sizeProperty());

        TableColumn<FileEntry, String> modifiedCol = new TableColumn<>("Last Modified");
        modifiedCol.setCellValueFactory(cell -> cell.getValue().lastModifiedProperty());

        TableColumn<FileEntry, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(cell -> cell.getValue().actionProperty());

        table.getColumns().addAll(nameCol, sizeCol, modifiedCol, actionCol);
        //VBox.setVgrow(table, Priority.ALWAYS);

        Button upload=Upload();
        HBox buttonBox=new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(upload);


        main.getChildren().addAll(welcome,desc,recentfiles,allfiles,table,buttonBox);

        return main;
    }
    public static Button Upload(){
        FontIcon uploadicon=new FontIcon("fas-upload");
        uploadicon.setIconSize(16);
        uploadicon.setIconColor(Color.WHITE);

        Button upload = new Button("Upload", uploadicon);
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
                    Label l=new Label(service.response+ file.getName());
                    l.setStyle("-fx-text-fill: white;");
                    VBox card = new VBox(l);
                    card.setPadding(new Insets(16, 24, 16, 24));
                    card.setStyle("""
                                -fx-background-color: #0F1B2D;
                                -fx-background-radius: 8; 
                            """);
                    Popup popup=new Popup();
                    popup.getContent().add(card);
                    popup.setAutoHide(true);
                    popup.show(stage);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        });
        
        return upload;
    }

}
