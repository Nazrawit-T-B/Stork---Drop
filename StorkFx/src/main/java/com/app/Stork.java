package com.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Stork extends Application{
    public void start(Stage stage){
        BorderPane root=new BorderPane();
        Scene scene=new Scene(root);

        stage.setTitle("Stork");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args){
        launch(args);
    }

}
