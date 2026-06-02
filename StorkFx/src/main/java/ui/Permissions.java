package ui;

import java.io.IOException;
import java.util.ArrayList;
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
import model.FileModel;
import model.PermissionModel;
import model.PermissionType;
import service.PermissionService;

public class Permissions {

    private static final PermissionService permissionService =
            new PermissionService();

    private static final ObservableList<PermissionModel>
            permissions =
            FXCollections.observableArrayList();

    private static FileModel selectedFile;

    private static ComboBox<FileModel> fileSelector;

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

        loadFiles();

        return root;
    }

    private static HBox permissionsHeader() {

        Label title =
                new Label("Permissions");

        title.getStyleClass()
                .add("page-title");

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        return new HBox(
                20,
                title,
                spacer
        );
    }

    private static VBox fileInfoCard() {

        VBox card =
                new VBox(10);

        card.getStyleClass()
                .add("card");

        card.setPadding(
                new Insets(20)
        );

        Label fileLabel =
                new Label(
                        "Selected File"
                );

        fileSelector =
                new ComboBox<>();

        fileSelector.setPrefWidth(
                350
        );

        ownerLabel =
                new Label(
                        "Owner: -"
                );

        fileSelector.setOnAction(e -> {

            selectedFile =
                    fileSelector.getValue();

            if (selectedFile != null) {

                ownerLabel.setText(
                        "Owner: "
                                + selectedFile.getOwner()
                );

                refreshPermissions();
            }
        });

        card.getChildren().addAll(
                fileLabel,
                fileSelector,
                ownerLabel
        );

        return card;
    }

    private static VBox permissionsTableCard() {

        VBox card = new VBox(15);
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        Label title = new Label("Current Access");
        title.getStyleClass().add("section-title");
        TableView<PermissionModel> table = new TableView<>();
        table.setMaxWidth(Double.MAX_VALUE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setItems(permissions);

        TableColumn<PermissionModel, String> userColumn = new TableColumn<>("User");
        userColumn.setMinWidth(250);
        userColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<PermissionModel, String> permissionColumn = new TableColumn<>("Permission");
        permissionColumn.setMinWidth(150);
        permissionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPermission().name()));

        TableColumn<PermissionModel,Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setMinWidth(250);
        actionColumn.setCellFactory(col -> new TableCell<>() {
                private final Button editBtn = new Button("Edit");
                private final Button removeBtn = new Button("Remove");
                private final HBox controls = new HBox(8, editBtn, removeBtn);
                {
                        controls.setAlignment(Pos.CENTER);
                        editBtn.setOnAction(e -> {
                                PermissionModel permission = getTableView().getItems().get(getIndex());
                                showEditDialog(permission);
                        });
                        removeBtn.setOnAction(e -> {
                                PermissionModel permission = getTableView().getItems().get(getIndex());
                                permissionService.revokeAccess(selectedFile.getId(), permission.getUsername());
                                refreshPermissions();
                        });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                                setGraphic(null);
                                return;
                        }
                        PermissionModel permission = getTableView().getItems().get(getIndex());
                        if (permission.getPermission() == PermissionType.OWNER) {
                                setGraphic(new Label("-"));
                        } else {
                                setGraphic(controls);
                        }
                }
        });
        table.getColumns().addAll(userColumn, permissionColumn,actionColumn);

        card.getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return card;
    }

    private static VBox shareCard() {

        VBox card =
                new VBox(15);

        card.getStyleClass()
                .add("card");

        card.setPadding(
                new Insets(20)
        );

        Label title =
                new Label(
                        "Share File"
                );

        title.getStyleClass()
                .add("section-title");

        TextField usernameField =
                new TextField();

        usernameField.setPromptText(
                "Username"
        );

        ComboBox<PermissionType>
                permissionBox =
                new ComboBox<>();

        permissionBox
                .getItems()
                .addAll(
                        PermissionType.READ,
                        PermissionType.WRITE
                );

        permissionBox.setValue(
                PermissionType.READ
        );

        Button grantBtn =
                new Button(
                        "Grant Access"
                );

        grantBtn.setOnAction(
                e -> {

                    if (selectedFile == null) {
                        return;
                    }
                    
                        permissionService.grantAccess(
                                selectedFile.getId(),
                                usernameField.getText(),
                                permissionBox.getValue()
                                );
                    

                    usernameField.clear();

                    refreshPermissions();
                }
        );

        HBox buttonRow =
                new HBox(
                        grantBtn
                );

        buttonRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        card.getChildren().addAll(
                title,
                new Label(
                        "Username"
                ),
                usernameField,
                new Label(
                        "Permission"
                ),
                permissionBox,
                buttonRow
        );

        return card;
    }

    private static void showEditDialog(
            PermissionModel permission
    ) {

        ChoiceDialog<PermissionType>
                dialog =
                new ChoiceDialog<>(
                        permission.getPermission(),
                        PermissionType.READ,
                        PermissionType.WRITE
                );

        dialog.setTitle(
                "Edit Permission"
        );

        dialog.setHeaderText(
                "Change permission for "
                        + permission.getUsername()
        );

        dialog.showAndWait()
                .ifPresent(
                        newPermission -> {

                            permissionService
                                    .updatePermission(
                                            selectedFile.getId(),
                                            permission.getUsername(),
                                            newPermission
                                    );

                            refreshPermissions();
                        }
                );
    }

    private static void loadFiles() {
        List<FileModel> files = new ArrayList<>();
        try {
                files = permissionService.getAvailableFiles();
        } catch(IOException e) {
                throw new RuntimeException(e);
        }

        fileSelector
                .getItems()
                .setAll(
                        files
                );

        if (
                !fileSelector
                        .getItems()
                        .isEmpty()
        ) {

            selectedFile =
                    fileSelector
                            .getItems()
                            .get(0);

            fileSelector.setValue(
                    selectedFile
            );

            ownerLabel.setText(
                    "Owner: "
                            + selectedFile.getOwner()
            );

            refreshPermissions();
        }
    }

    private static void refreshPermissions() {
        if (selectedFile == null) {
            return;
        }
        List<PermissionModel> permissionsList = new ArrayList<>();
        try {
            permissionsList = permissionService.getPermissions(selectedFile.getId());
        } catch (IOException e) {
                throw new RuntimeException(e);
        }
        permissions.setAll(permissionsList);
    }
}