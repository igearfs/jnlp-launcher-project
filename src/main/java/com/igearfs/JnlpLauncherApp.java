package com.igearfs;

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

import static com.igearfs.LaunchEntryManager.saveEntriesToFile;

public class JnlpLauncherApp extends Application {

    private List<LaunchEntry> entries = new ArrayList<>();
    private ListView<HBox> listView;
    private TextField nameField;
    private TextField urlField;
    private TextArea noteField;
    private Button launchButton;
    private Button saveButton;
    private boolean isModified;

    private Stage primaryStage; // Store primaryStage here

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;  // Store primaryStage reference

        primaryStage.setTitle("JNLP Launcher");

        // Create and set up components
        listView = new ListView<>();
        listView.setPrefWidth(250);
        listView.setMaxHeight(400);

        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));

        nameField = new TextField();
        urlField = new TextField();
        noteField = new TextArea();

        launchButton = new Button("Launch");
        saveButton = new Button("Save");
        launchButton.setDisable(true);
        saveButton.setDisable(true);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.getChildren().addAll(saveButton, launchButton);

        rightPane.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("URL:"), urlField,
                new Label("Notes:"), noteField,
                buttonContainer
        );

        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.4);
        splitPane.getItems().addAll(createLeftPane(), rightPane);

        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        launchButton.setOnAction(e -> launchSelectedEntry());
        saveButton.setOnAction(e -> saveSelectedEntry());

        LaunchEntryManager.loadEntriesFromFile(entries);
        populateListView();
    }

    private VBox createLeftPane() {
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        Button addButton = new Button("Add Entry");
        addButton.setOnAction(e -> showAddEntryPopup());

        leftPane.getChildren().add(addButton);
        listView.setOnMouseClicked(e -> updateRightPane());
        leftPane.getChildren().add(listView);
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

        popupLayout.getChildren().addAll(
                new Label("Name:"), newNameField,
                new Label("URL:"), newUrlField,
                new Label("Notes:"), newNoteField,
                buttonBox
        );

        Scene popupScene = new Scene(popupLayout, 350, 300);
        popupStage.setScene(popupScene);

        okButton.setOnAction(e -> {
            LaunchEntry newEntry = new LaunchEntry(
                    newNameField.getText(),
                    newUrlField.getText(),
                    newNoteField.getText(),
                    UUID.randomUUID().toString()
            );
            entries.add(newEntry);
            saveEntriesToFile(entries);
            populateListView();
            popupStage.close();
        });

        cancelButton.setOnAction(e -> popupStage.close());
        popupStage.showAndWait();
    }

    private void populateListView() {
        listView.getItems().clear();
        for (LaunchEntry entry : entries) {
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            ImageView imageView = new ImageView(new Image(getClass().getResource("/rocket.gif").toExternalForm()));
            imageView.setFitWidth(32);
            imageView.setFitHeight(32);
            Label nameLabel = new Label(entry.getName());
            hbox.getChildren().addAll(imageView, nameLabel);
            listView.getItems().add(hbox);
        }
    }

    private void updateRightPane() {
        HBox selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = listView.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            // Create ID label and display it
            Label idLabel = new Label("ID: " + selectedEntry.getId());
            nameField.setText(selectedEntry.getName());
            urlField.setText(selectedEntry.getUrl());
            noteField.setText(selectedEntry.getNote());

            // Update the right pane
            VBox rightPane = (VBox) ((SplitPane) primaryStage.getScene().getRoot()).getItems().get(1);
            rightPane.getChildren().clear();
            rightPane.getChildren().addAll(
                    idLabel,  // Display the ID
                    new Label("Name:"), nameField,
                    new Label("URL:"), urlField,
                    new Label("Notes:"), noteField,
                    new HBox(10, saveButton, launchButton) // Button container
            );

            launchButton.setDisable(false);
            saveButton.setDisable(false);
        }
    }

    private void saveSelectedEntry() {
        HBox selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = listView.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            // Save the original ID before changes
            String selectedEntryId = selectedEntry.getId();
            selectedEntry.setName(nameField.getText());
            selectedEntry.setUrl(urlField.getText());
            selectedEntry.setNote(noteField.getText());
            saveEntriesToFile(entries);
            populateListView();

            // Find the updated entry by its original ID and reselect it
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getId().equals(selectedEntryId)) {
                    // Find the HBox in the ListView that corresponds to the entry
                    HBox hbox = (HBox) listView.getItems().get(i);
                    listView.getSelectionModel().select(hbox);
                    break;
                }
            }
        }

    }

    private void launchSelectedEntry() {
        // Launch logic here
    }

    public static void main(String[] args) {
        launch(args);
    }
}
