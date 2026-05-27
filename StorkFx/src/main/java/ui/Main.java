package ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        SceneManager.setStage(stage);

        SceneManager.showDashboard(); 

        stage.setTitle("Stork Drop");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
