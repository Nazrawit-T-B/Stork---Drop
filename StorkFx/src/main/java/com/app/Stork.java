package com.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static ui.Dashboard.SyncActivity;
import static ui.Dashboard.activeStat;
//import static ui.Dashboard.createSysHealth;
import static ui.Dashboard.recent;
import static ui.Dashboard.transfers;
import static ui.FilesUI.Area;
import static ui.FilesUI.FilesHeader;
import static ui.Sidebar.createsidebar;

public class Stork extends Application{
    BorderPane root=new BorderPane();
    StackPane layout=new StackPane();

    @Override
    public void start(Stage stage){
        root.setLeft(createsidebar(root));

        BorderPane mainArea=new BorderPane();
        mainArea.setTop(FilesHeader());
        VBox leftComponent=new VBox(10);
        leftComponent.getChildren().addAll(/*createSysHealth(),*/transfers());
        VBox rightComponent=new VBox(10);
        rightComponent.getChildren().addAll(activeStat(),recent());
        mainArea.setCenter(Area());
        mainArea.setPadding(new Insets(24, 24, 24, 24));
        root.setCenter(mainArea);
        layout.getChildren().add(root);
        Scene scene=new Scene(layout,1100,800);
        scene.getStylesheets().addAll(
                getClass().getResource("/dashboard.css").toExternalForm(),
                getClass().getResource("/files.css").toExternalForm()
        );
        stage.setResizable(true);
        stage.setScene(scene);
        stage.setTitle("Stork-Drop");
        stage.show();
    }
    public static void main(String[] args){
        launch(args);
    }

}
