package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Dashboard {

    public static HBox SyncActivity(){

        Label label=new Label("Sync & Activity ");
        HBox searchandnotifications=new HBox(7);
        TextField search=new TextField();
        search.setPromptText("Search files or activity...");
        searchandnotifications.getChildren().addAll(search);
        HBox header=new HBox(400);
        header.getChildren().addAll(label,searchandnotifications);

        header.getStyleClass().add("sync-header");

        return header;
    }

    public static VBox createSysHealth(){
        //component showing the system health of a device
       VBox component=new VBox(15);
       HBox topbar=new HBox(10);
       VBox toplabel=new VBox(10);
       VBox bottombar=new VBox(5);
        Image icon=new Image(Dashboard.class.getResourceAsStream("/img_1.png"));
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(50);
        iconView.setPreserveRatio(true);
       Label label=new Label("System Health");
       HBox sublabel=new HBox(5);
       Label statlabel=new Label("All systems operational");
       Label latencylabel=new Label("12ms Latency");
       sublabel.getChildren().addAll(statlabel,latencylabel);
       toplabel.getChildren().addAll(label,sublabel);
       topbar.getChildren().addAll(iconView,toplabel);

       Label storagelabel=new Label("42.8 GB of 100 GB used");

       component.getChildren().addAll(topbar,storagelabel);

       component.getStyleClass().add("sys-health");
       component.setPrefWidth(350);
       component.setPrefHeight(170);
       component.setSpacing(20);
       component.setPadding(new Insets(20));

       return component;
    }
    public static VBox transfers(){
        VBox component=new VBox(10);
        VBox files=new VBox();
        Label desc=new Label("Active Transfers");
        Button actionbtn=new Button("Pause All");
        HBox title=new HBox(30);
        title.getChildren().addAll(desc,actionbtn);
        Button transferBtn=new Button("View all transfers");
        component.getChildren().addAll(title,files,transferBtn);
        return component;

    }

    public static HBox activeStat(){
        Label label=new Label("Active Transfers");
        Label transfernum=new Label("08");
        Label desc=new Label("Files currently syncing");
        Button transferBtn=new Button("View all transfers");
        VBox stat=new VBox(10);
        stat.getChildren().addAll(label,transfernum,desc,transferBtn);
        Label placeholder=new Label("This is an image placeholder for now");
        HBox active=new HBox(15);
        active.getChildren().addAll(stat,placeholder);
        return active;
    }

    public static VBox recent(){
        VBox activites=new VBox(10);
        Label title=new Label("Recent Activity");
        HBox toplabel=new HBox(20);
        Button historyBtn=new Button("View full history");
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("Filter", "Files", "People");
        filter.setValue("Filter");
        filter.getStyleClass().add("filter-combo");
        toplabel.getChildren().addAll(title,filter);
        activites.getChildren().addAll(toplabel,historyBtn);
        return activites;
    }




}
