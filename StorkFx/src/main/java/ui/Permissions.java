package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;

public class Permissions {

    // HEADER

    public static HBox permissionsHeader() {

        VBox titles = new VBox(6);

        Label title = new Label("Folder Permissions");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label(
                "Manage who has access to “Q4 Financials” and their level of control."
        );
        subtitle.getStyleClass().add("activity-time");

        titles.getChildren().addAll(title, subtitle);

        // SEARCH
        TextField search = new TextField();
        search.setPromptText("Search files or activity...");
        search.getStyleClass().add("search-field");
        search.setPrefWidth(260);

        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.getStyleClass().add("search-icon");

        HBox searchBox = new HBox(8, searchIcon, search);
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // NOTIFICATION
        FontIcon bellIcon = new FontIcon("fas-bell");
        bellIcon.getStyleClass().add("notif-icon");

        // ADD PERSON BUTTON
        FontIcon addIcon = new FontIcon("fas-user-plus");
        addIcon.setIconColor(Color.WHITE);

        Button addBtn = new Button("Add Person");
        addBtn.setGraphic(addIcon);
        addBtn.getStyleClass().add("permissions-add-btn");

        VBox rightBox = new VBox(18);
        HBox topControls = new HBox(18, searchBox, bellIcon);

        topControls.setAlignment(Pos.CENTER_RIGHT);

        rightBox.getChildren().addAll(topControls, addBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(20);
        header.setAlignment(Pos.TOP_LEFT);

        header.getChildren().addAll(titles, spacer, rightBox);

        return header;
    }

    //PEOPLE ACCESS CARD

    public static VBox peopleAccessCard() {

        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(22));
        card.setPrefWidth(700);

        // HEADER ROW
        HBox topRow = new HBox();

        Label title = new Label("People with Access");
        title.getStyleClass().add("section-title");

        Label members = new Label("4 total members");
        members.getStyleClass().add("activity-time");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(title, spacer, members);

        VBox users = new VBox(8);

        users.getChildren().addAll(
                createUserRow("Alex Thompson", "alex.t@company.com", "Owner", false, false),
                createUserRow("Sarah Chen", "s.chen@company.com", "Editor", true, false),
                createUserRow("Marcus Wright", "m.wright@company.com", "Viewer", true, false),
                createUserRow("Emma Lofton", "emma@contractor.io", "Viewer", true, true)
        );

        Button logsBtn = new Button("View access logs  →");
        logsBtn.getStyleClass().add("view-all-link");

        HBox btnRow = new HBox(logsBtn);
        btnRow.setAlignment(Pos.CENTER);

        card.getChildren().addAll(topRow, users, btnRow);

        return card;
    }

    // USER ROW

    private static HBox createUserRow(
            String name,
            String email,
            String role,
            boolean editable,
            boolean external
    ) {

        HBox row = new HBox(16);
        row.getStyleClass().add("permission-user-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 12, 0));

        // AVATAR
        Circle avatar = new Circle(22);

        if (external) {
            avatar.setFill(Color.web("#F4D6C8"));
        } else {
            avatar.setFill(Color.web("#3B82F6"));
        }

        // USER INFO
        VBox info = new VBox(4);

        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("activity-actor");

        nameRow.getChildren().add(nameLabel);

        if (external) {
            Label badge = new Label("EXTERNAL");
            badge.getStyleClass().add("external-badge");
            nameRow.getChildren().add(badge);
        }

        Label emailLabel = new Label(email);
        emailLabel.getStyleClass().add("activity-time");

        info.getChildren().addAll(nameRow, emailLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ROLE CONTROLS
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_RIGHT);

        Label roleLabel = new Label(role);
        roleLabel.getStyleClass().add("role-chip");

        controls.getChildren().add(roleLabel);

        if (editable) {

            FontIcon chevron = new FontIcon("fas-chevron-down");
            chevron.setIconSize(12);
            chevron.setIconColor(Color.web("#64748B"));

            FontIcon person = new FontIcon("fas-user-plus");
            person.setIconSize(14);
            person.setIconColor(Color.web("#64748B"));

            controls.getChildren().addAll(chevron, person);
        }

        row.getChildren().addAll(avatar, info, spacer, controls);

        return row;
    }

    // SECURITY CARD

    public static VBox securityCard() {

        VBox card = new VBox(22);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(22));
        card.setPrefWidth(430);

        Label title = new Label("Security Settings");
        title.getStyleClass().add("section-title");

        VBox toggles = new VBox(20);

        toggles.getChildren().addAll(
                createToggleRow(
                        "Link sharing",
                        "Anyone with the link can access these files.",
                        true
                ),
                createToggleRow(
                        "Allow downloads",
                        "Viewers can download and print files.",
                        true
                )
        );

        VBox accessBox = new VBox(14);

        Label accessTitle = new Label("Access Level");
        accessTitle.getStyleClass().add("activity-actor");

        VBox option1 = createAccessOption(
                "Internal Team",
                "Only members of the “Finance” group.",
                true
        );

        VBox option2 = createAccessOption(
                "Specific People",
                "Only people added manually can view.",
                false
        );

        accessBox.getChildren().addAll(
                accessTitle,
                option1,
                option2
        );

        card.getChildren().addAll(title, toggles, accessBox);

        return card;
    }

    // TOGGLE ROW

    private static HBox createToggleRow(
            String titleText,
            String descText,
            boolean enabled
    ) {

        HBox row = new HBox();
        row.setAlignment(Pos.TOP_LEFT);

        VBox textBox = new VBox(6);

        Label title = new Label(titleText);
        title.getStyleClass().add("activity-actor");

        Label desc = new Label(descText);
        desc.getStyleClass().add("activity-time");
        desc.setWrapText(true);

        textBox.getChildren().addAll(title, desc);

        ToggleButton toggle = new ToggleButton();
        toggle.setSelected(enabled);
        toggle.getStyleClass().add("modern-toggle");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(textBox, spacer, toggle);

        return row;
    }

    // ACCESS OPTION

    private static VBox createAccessOption(
            String titleText,
            String descText,
            boolean selected
    ) {

        VBox box = new VBox(6);

        if (selected) {
            box.getStyleClass().add("access-option-selected");
        } else {
            box.getStyleClass().add("access-option");
        }

        box.setPadding(new Insets(16));

        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);

        RadioButton rb = new RadioButton();
        rb.setSelected(selected);

        VBox texts = new VBox(4);

        Label title = new Label(titleText);
        title.getStyleClass().add("activity-actor");

        Label desc = new Label(descText);
        desc.getStyleClass().add("activity-time");
        desc.setWrapText(true);

        texts.getChildren().addAll(title, desc);

        row.getChildren().addAll(rb, texts);

        box.getChildren().add(row);

        return box;
    }

    // SHARE LINK CARD

    public static VBox shareLinkCard() {

        VBox card = new VBox(18);
        card.getStyleClass().add("share-link-card");
        card.setPadding(new Insets(22));

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        FontIcon linkIcon = new FontIcon("fas-link");
        linkIcon.setIconColor(Color.WHITE);

        Label title = new Label("Shareable Link");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

        titleRow.getChildren().addAll(linkIcon, title);

        // LINK BOX
        HBox linkBox = new HBox();
        linkBox.getStyleClass().add("share-link-box");
        linkBox.setAlignment(Pos.CENTER_LEFT);
        linkBox.setPadding(new Insets(14));

        Label link = new Label("dropbox.com/s/Q4-Financials-2023...");
        link.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon copyIcon = new FontIcon("fas-copy");
        copyIcon.setIconColor(Color.WHITE);

        linkBox.getChildren().addAll(link, spacer, copyIcon);

        Button resetBtn = new Button("Reset Link");
        resetBtn.getStyleClass().add("share-reset-btn");
        resetBtn.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(titleRow, linkBox, resetBtn);

        return card;
    }

    // MAIN PAGE

    public static BorderPane permissionsPage() {

        BorderPane root = new BorderPane();

        root.setTop(permissionsHeader());

        VBox rightSide = new VBox(20);
        rightSide.getChildren().addAll(
                securityCard(),
                shareLinkCard()
        );

        HBox content = new HBox(24);
        content.setPadding(new Insets(24));

        content.getChildren().addAll(
                peopleAccessCard(),
                rightSide
        );

        root.setCenter(content);

        return root;
    }
}