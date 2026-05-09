package com.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static ui.Dashboard.*;
import static ui.Sidebar.createsidebar;

public class Stork extends Application{
    BorderPane root=new BorderPane();
    public void start(Stage stage){
        StackPane layout=new StackPane();
        root.setLeft(createsidebar());

        BorderPane mainArea=new BorderPane();
        mainArea.setTop(SyncActivity());
        VBox leftComponent=new VBox(10);
        leftComponent.getChildren().addAll(createSysHealth(),transfers());
        VBox rightComponent=new VBox(10);
        rightComponent.getChildren().addAll(activeStat(),recent());
        mainArea.setLeft(leftComponent);
        mainArea.setRight(rightComponent);
        mainArea.setPadding(new Insets(24, 24, 24, 24));

        root.setCenter(mainArea);
        layout.getChildren().add(root);
        Scene scene=new Scene(layout,1100,800);
        scene.getStylesheets().add(
                getClass().getResource("/dashboard.css").toExternalForm()
        );
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args){
        launch(args);
    }

}
