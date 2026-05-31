package ui;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.PermissionModel;
import service.PermissionService;

public class Permissions {

    private static final PermissionService permissionService =
            new PermissionService();

    private static final ObservableList<PermissionModel> permissions =
            FXCollections.observableArrayList();

    private static Long currentFileId;

    private static Label selectedFileLabel;
    private static Label ownerLabel;

    public static BorderPane permissionsPage() {

        BorderPane root = new BorderPane();

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        content.getChildren().addAll(
                permissionsHeader(),
                fileInfoCard(),
                permissionsTableCard(),
                shareCard()
        );

        root.setCenter(content);

        return root;
    }

    private static HBox permissionsHeader() {

        Label title = new Label("Permissions");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        header.getChildren().addAll(
                title,
                spacer
        );

        return header;
    }

    private static VBox fileInfoCard() {

        VBox card = new VBox(8);

        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        selectedFileLabel =
                new Label("Selected File: None");

        ownerLabel =
                new Label("Owner: Unknown");

        card.getChildren().addAll(
                selectedFileLabel,
                ownerLabel
        );

        return card;
    }

    private static VBox permissionsTableCard() {

        VBox card = new VBox(15);

        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label title = new Label("Current Access");
        title.getStyleClass().add("section-title");

        TableView<PermissionModel> table =
                new TableView<>();

        table.setItems(permissions);

        TableColumn<PermissionModel, String> userColumn =
                new TableColumn<>("User");

        userColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getUsername()
                )
        );

        userColumn.setPrefWidth(250);

        TableColumn<PermissionModel, String> permissionColumn =
                new TableColumn<>("Permission");

        permissionColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getPermission().toString()
                )
        );

        permissionColumn.setPrefWidth(150);

        TableColumn<PermissionModel, Void> actionColumn =
                new TableColumn<>("Actions");

        actionColumn.setPrefWidth(250);

        actionColumn.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn =
                    new Button("Edit");

            private final Button removeBtn =
                    new Button("Remove");

            private final HBox container =
                    new HBox(8, editBtn, removeBtn);

            {

                container.setAlignment(Pos.CENTER);

                editBtn.setOnAction(event -> {

                    PermissionModel permission =
                            getTableView()
                                    .getItems()
                                    .get(getIndex());

                    showEditPermissionDialog(permission);
                });

                removeBtn.setOnAction(event -> {

                    PermissionModel permission =
                            getTableView()
                                    .getItems()
                                    .get(getIndex());

                    permissionService.revokeAccess(
                            currentFileId,
                            permission.getUsername()
                    );

                    refreshPermissions();
                });
            }

            @Override
            protected void updateItem(
                    Void item,
                    boolean empty
            ) {

                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                PermissionModel permission =
                        getTableView()
                                .getItems()
                                .get(getIndex());

                if ("OWNER".equals(
                        permission.getPermission().name()
                )) {

                    setGraphic(new Label("-"));
                } else {

                    setGraphic(container);
                }
            }
        });

        table.getColumns().addAll(
                userColumn,
                permissionColumn,
                actionColumn
        );

        card.getChildren().addAll(
                title,
                table
        );

        return card;
    }

    private static VBox shareCard() {

        VBox card = new VBox(15);

        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label title = new Label("Share File");
        title.getStyleClass().add("section-title");

        Label usernameLabel =
                new Label("Username");

        TextField usernameField =
                new TextField();

        usernameField.setPromptText(
                "Enter username"
        );

        Label permissionLabel =
                new Label("Permission");

        ComboBox<String> permissionBox =
                new ComboBox<>();

        permissionBox.getItems().addAll(
                "READ",
                "WRITE"
        );

        permissionBox.setValue("READ");

        Button grantBtn =
                new Button("Grant Access");

        grantBtn.getStyleClass().add(
                "btn-primary"
        );

        grantBtn.setOnAction(event -> {

            permissionService.grantAccess(
                    currentFileId,
                    usernameField.getText(),
                    permissionBox.getValue()
            );

            usernameField.clear();

            refreshPermissions();
        });

        HBox buttonRow =
                new HBox(grantBtn);

        buttonRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        card.getChildren().addAll(
                title,
                usernameLabel,
                usernameField,
                permissionLabel,
                permissionBox,
                buttonRow
        );

        return card;
    }

    private static void showEditPermissionDialog(
            PermissionModel permission
    ) {

        ChoiceDialog<String> dialog =
                new ChoiceDialog<>(
                        permission.getPermission().name(),
                        "READ",
                        "WRITE"
                );

        dialog.setTitle(
                "Edit Permission"
        );

        dialog.setHeaderText(
                "Change permission for "
                        + permission.getUsername()
        );

        dialog.showAndWait().ifPresent(newPermission -> {

            permissionService.updatePermission(
                    currentFileId,
                    permission.getUsername(),
                    newPermission
            );

            refreshPermissions();
        });
    }

    public static void loadFile(
            Long fileId,
            String fileName,
            String owner
    ) {

        currentFileId = fileId;

        selectedFileLabel.setText(
                "Selected File: " + fileName
        );

        ownerLabel.setText(
                "Owner: " + owner
        );

        refreshPermissions();
    }

    private static void refreshPermissions() {

        if (currentFileId == null) {
            return;
        }

        List<PermissionModel> result =
                permissionService.getPermissions(
                        currentFileId
                );

        permissions.setAll(result);
    }
}