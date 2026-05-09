package ui;

import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;



public class Sidebar {
    public static VBox createsidebar() {
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(190);

        Image image=new Image(Sidebar.class.getResourceAsStream("/img.png"));
        ImageView logoView = new ImageView(image);
        logoView.setFitWidth(30);
        logoView.setPreserveRatio(true);

        Label logo = new Label("Stork Drop");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold; -fx-padding: 30 0 0 0;");
        HBox brand=new HBox();
        brand.getChildren().addAll(logoView,logo);
        sidebar.getChildren().add(brand);


        ToggleGroup group = new ToggleGroup();

        ToggleButton btnSync = new ToggleButton("Sync & Activity");
        FontIcon syncIcon = new FontIcon("mdi2h-home-outline");
        syncIcon.setIconSize(24);
        syncIcon.setIconColor(Color.WHITE);
        btnSync.setToggleGroup(group);
        btnSync.setSelected(true);
        btnSync.setGraphic(syncIcon);
        btnSync.getStyleClass().add("nav-button");

        ToggleButton btnFiles = new ToggleButton("Files");
        btnFiles.setToggleGroup(group);
        btnFiles.getStyleClass().add("nav-button");

        ToggleButton btnActivity = new ToggleButton("Activity");
        btnActivity.setToggleGroup(group);
        btnActivity.getStyleClass().add("nav-button");

        ToggleButton btnPermission = new ToggleButton("Permissions");
        btnPermission.setToggleGroup(group);
        btnPermission.getStyleClass().add("nav-button");

        ToggleButton btnHistory = new ToggleButton("History");
        btnHistory.setToggleGroup(group);
        btnHistory.getStyleClass().add("nav-button");


        sidebar.getChildren().addAll(btnSync,btnFiles,btnActivity,btnPermission,btnHistory);
        sidebar.getStylesheets().add("sidebar.css");
        return sidebar;
    }
}
