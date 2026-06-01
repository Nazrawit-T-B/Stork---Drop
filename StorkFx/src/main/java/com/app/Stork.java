package com.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.Dashboard;
import service.FileTransferService;

import static ui.Sidebar.createsidebar;

import java.util.List;

public class Stork extends Application {
    
    private final BorderPane root = new BorderPane();
    private final StackPane layout = new StackPane();
    
    // 🆕 THE FIX: Added the instance field so methods inside start() can resolve it!
    private final FileTransferService fileTransferService = new FileTransferService();

    @Override
    public void start(Stage stage) {
        // Render the navigation structure base frames
        root.setLeft(createsidebar(root));
        
        // 🎯 Enforce the Login Page view up front on start
        root.setCenter(ui.LoginPage.createLoginPage(root));
         
        // Define operational hooks for background processing 
        Dashboard.setHandlers(
            (file, destinationPath) -> {
                new Thread(() -> {
                    try {
                        System.out.println("Downloading target network file item: " + file.filename());
                        fileTransferService.downloadFromServer(file.filename(), destinationPath);
                        Dashboard.pushNotification("Downloaded: " + file.filename());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Dashboard.pushNotification("Download Failed: " + file.filename());
                    }
                }).start();
            },
            null, 
            () -> {
                new Thread(() -> {
                    try {
                        System.out.println("Syncing discovery feeds straight from remote cloud instances...");
                        List<Dashboard.SharedFile> publicFiles = fileTransferService.fetchPublicFeedFromServer();
                        Dashboard.updateDashboardData(publicFiles, null);
                    } catch (Exception e) {
                        System.err.println("Failed to fetch discovery feed lists structural records: " + e.getMessage());
                        e.printStackTrace();
                    }
                }).start();
            }
        );

        layout.getChildren().add(root);
        Scene scene = new Scene(layout, 1100, 800);
        scene.getStylesheets().addAll(
                getClass().getResource("/dashboard.css").toExternalForm(),
                getClass().getResource("/files.css").toExternalForm()
        );
        
        stage.setResizable(true);
        stage.setScene(scene);
        stage.setTitle("Stork-Drop");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
