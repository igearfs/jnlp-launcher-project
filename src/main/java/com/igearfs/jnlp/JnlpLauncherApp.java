package com.igearfs.jnlp;

import com.igearfs.jnlp.controller.LaunchEntryController;
import com.igearfs.jnlp.model.LaunchEntry;
import com.igearfs.jnlp.util.LaunchEntryManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.igearfs.jnlp.util.LaunchEntryManager.saveEntriesToFile;

public class JnlpLauncherApp extends Application {

    private List<LaunchEntry> entries = new ArrayList<>();
    private ListView<HBox> listViewJnlp;
    private TextField nameField;
    private TextField urlField;
    private TextArea noteField;
    private Button launchButton;
    private Button saveButton;
    private Button deleteButton;
    private CheckBox ignoreDomainCheckBox;

    private boolean isModified;

    private TextField searchField;
    private Stage primaryStage;

    private LaunchEntryController controller;  // Reference to the controller

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("JNLP Launcher");

        // Initialize UI components
        listViewJnlp = new ListView<>();
        listViewJnlp.setPrefWidth(250);
        listViewJnlp.setMaxHeight(400);

        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));

        nameField = new TextField();
        urlField = new TextField();
        noteField = new TextArea();

        launchButton = new Button("Launch");
        saveButton = new Button("Save");
        deleteButton = new Button("Delete");
        launchButton.setDisable(true);
        saveButton.setDisable(true);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.getChildren().addAll(saveButton, launchButton, deleteButton);

        ignoreDomainCheckBox = new CheckBox("Ignore Domain Validation");
        ignoreDomainCheckBox.setSelected(true);

        rightPane.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("URL:"), urlField,
                new Label("Notes:"), noteField,
                ignoreDomainCheckBox,
                buttonContainer
        );

        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.4);
        splitPane.getItems().addAll(createLeftPane(), rightPane);

        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the controller
        controller = new LaunchEntryController(this);

        // Load entries and populate ListView
        LaunchEntryManager.loadEntriesFromFile(entries);
        controller.populateListView();

        // Set button actions
        launchButton.setOnAction(e -> controller.launchSelectedEntry());
        saveButton.setOnAction(e -> controller.saveSelectedEntry());
        deleteButton.setOnAction(e -> controller.deleteSelectedEntry());
    }

    private VBox createLeftPane() {
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        Button addButton = new Button("Add Entry");
        addButton.setOnAction(e -> showAddEntryPopup());

        HBox searchBox = new HBox(5);
        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(250);
        searchField.setOnKeyTyped(e -> controller.filterList(searchField.getText()));
        ImageView searchIcon = new ImageView(new Image(getClass().getResourceAsStream("/search.png")));
        searchIcon.setFitWidth(20);
        searchIcon.setFitHeight(20);

        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(searchField, searchIcon);
        searchBox.setPrefWidth(Double.MAX_VALUE);

        leftPane.getChildren().add(searchBox);
        leftPane.getChildren().add(addButton);

        listViewJnlp.setOnMouseClicked(e -> updateRightPane());
        leftPane.getChildren().add(listViewJnlp);
        return leftPane;
    }

    private void showAddEntryPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add New Entry");

        VBox popupLayout = new VBox(10);
        popupLayout.setPadding(new Insets(10));
        popupLayout.setAlignment(Pos.CENTER_LEFT);

        TextField newNameField = new TextField();
        TextField newUrlField = new TextField();
        TextArea newNoteField = new TextArea();

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10, okButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        CheckBox newIgnoreDomainCheckBox = new CheckBox("Ignore Domain Validation");
        newIgnoreDomainCheckBox.setSelected(true);

        popupLayout.getChildren().addAll(
                new Label("Name:"), newNameField,
                new Label("URL:"), newUrlField,
                new Label("Notes:"), newNoteField,
                newIgnoreDomainCheckBox,
                buttonBox
        );
        Scene popupScene = new Scene(popupLayout, 350, 300);
        popupStage.setScene(popupScene);

        okButton.setOnAction(e -> {
            LaunchEntry newEntry = new LaunchEntry(
                    newNameField.getText(),
                    newUrlField.getText(),
                    newNoteField.getText(),
                    UUID.randomUUID().toString(),
                    newIgnoreDomainCheckBox.isSelected()
            );
            entries.add(newEntry);
            saveEntriesToFile(entries);
            controller.populateListView();
            popupStage.close();
        });
        cancelButton.setOnAction(e -> popupStage.close());
        popupStage.showAndWait();
    }


    private void updateRightPane() {
        HBox selectedItem = listViewJnlp.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = listViewJnlp.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            Label idLabel = new Label("ID: " + selectedEntry.getId());
            nameField.setText(selectedEntry.getName());
            urlField.setText(selectedEntry.getUrl());
            noteField.setText(selectedEntry.getNote());
            ignoreDomainCheckBox.setSelected(selectedEntry.isIgnoreDomainValidation());

            VBox rightPane = (VBox) ((SplitPane) primaryStage.getScene().getRoot()).getItems().get(1);

            rightPane.getChildren().clear();
            rightPane.getChildren().addAll(
                    idLabel,
                    new Label("Name:"), nameField,
                    new Label("URL:"), urlField,
                    new Label("Notes:"), noteField,
                    ignoreDomainCheckBox,
                    new HBox(10, saveButton, launchButton, deleteButton)
            );

            launchButton.setDisable(false);
            saveButton.setDisable(false);
            deleteButton.setDisable(false);
            ignoreDomainCheckBox.setDisable(false);
        }
    }

    private void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public List<LaunchEntry> getEntries() {
        return this.entries;
    }

    public Button getSaveButton() {
        return this.saveButton;
    }

    public Button getLaunchButton() {
        return this.launchButton;
    }

    public TextField getNameField() {
        return this.nameField;
    }

    public TextField getUrlField() {
        return this.urlField;
    }

    public TextArea getNoteField() {
        return this.noteField;
    }

    public Button getDeleteButton() {
        return this.deleteButton;
    }

    public CheckBox getIgnoreDomainCheckBox() {
        return this.ignoreDomainCheckBox;
    }

    public ListView<HBox> getListViewJnlp() {
        return this.listViewJnlp;
    }

    public TextField getSearchField() {
        return this.searchField;
    }
}