/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

package com.igearfs.jnlp;

import com.igearfs.jnlp.controller.GridPaneController;
import com.igearfs.jnlp.controller.LaunchEntryController;
import com.igearfs.jnlp.model.LaunchEntry;
import com.igearfs.jnlp.util.ColorGridCell;
import com.igearfs.jnlp.util.IconLoader;
import com.igearfs.jnlp.util.LaunchEntryManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.igearfs.jnlp.util.LaunchEntryManager.saveEntriesToFile;

@Slf4j
public class JnlpLauncherApp extends Application {
    private static final Logger log = LoggerFactory.getLogger(JnlpLauncherApp.class);

    private GridPaneController gridPaneController = new GridPaneController();

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
        ImageView searchIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/search.png")));
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

    /**
     * Used to add a new entry into the list.
     */
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

        // Initial image for icon preview (empty initially)
        ImageView iconImageView = new ImageView();
        iconImageView.setFitWidth(32);
        iconImageView.setFitHeight(32);

        // Button to select an icon
        Button selectIconButton = new Button("Select Icon");
        selectIconButton.setOnAction(e -> gridPaneController.showIconChooserForNewEntry(iconImageView));

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
                new Label("Icon:"),
                iconImageView,
                selectIconButton,
                newIgnoreDomainCheckBox,
                buttonBox
        );

        Scene popupScene = new Scene(popupLayout, 350, 450);
        popupStage.setScene(popupScene);

        okButton.setOnAction(e -> {
            System.out.println("ICON SAVING FROM POPUP::"+ iconImageView.getImage().getUrl());
            // Create a new LaunchEntry with the selected icon path
            LaunchEntry newEntry = new LaunchEntry(
                    newNameField.getText(),
                    newUrlField.getText(),
                    newNoteField.getText(),
                    UUID.randomUUID().toString(),
                    newIgnoreDomainCheckBox.isSelected(),
                    ColorGridCell.lastSelectedCell.getIconName() // Store the icon URL in the entry
            );
            entries.add(newEntry);
            saveEntriesToFile(entries);
            controller.populateListView();
            popupStage.close();
        });

        cancelButton.setOnAction(e -> popupStage.close());

        popupStage.showAndWait();
    }


    /**
     * Used to update the right pane from the selection in the list.
     */
    private void updateRightPane() {
        ColorGridCell.lastSelectedCell = null;
        HBox selectedItem = listViewJnlp.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = listViewJnlp.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            // Get the icon name (as stored in the LaunchEntry object)
            String iconName = selectedEntry.getIconPath();
            System.out.println("Loading ICON:::" + iconName);
            // Get the actual image for the icon using IconLoader (or resource path)
            ImageView iconImageView = new ImageView();
            if (iconName != null && !iconName.isEmpty()) {
                // Load the icon using IconLoader
                iconImageView.setImage(IconLoader.loadIcon(iconName).getImage());
            }

            // If no valid icon is found, use a default icon (or empty placeholder)
            if (iconImageView.getImage() == null) {
                // Attempt to load the default icon
                InputStream iconStream = getClass().getResourceAsStream("/icons/rocket.png");
                if (iconStream != null) {
                    iconImageView.setImage(new Image(iconStream));
                } else {
                    // If the default icon is not found, use a placeholder or null image
                    iconImageView.setImage(new Image("")); // Or use a local fallback image
                }
            }

            // Set the icon size (adjust to your desired size)
            iconImageView.setFitWidth(64);  // Set the desired width
            iconImageView.setFitHeight(64); // Set the desired height

            // Text fields for entry details
            nameField.setText(selectedEntry.getName());
            urlField.setText(selectedEntry.getUrl());
            noteField.setText(selectedEntry.getNote());
            ignoreDomainCheckBox.setSelected(selectedEntry.isIgnoreDomainValidation());

            // Button to select a new icon
            Button selectIconButton = new Button("Select Icon");

            selectIconButton.setOnAction(e ->
                    gridPaneController.showIconChooserForNewEntry(iconImageView)

            );

            VBox rightPane = (VBox) ((SplitPane) primaryStage.getScene().getRoot()).getItems().get(1);

            // Clear and add all elements to the right pane
            rightPane.getChildren().clear();
            rightPane.getChildren().addAll(
                    new Label("ID: " + selectedEntry.getId()),
                    new Label("Name:"), nameField,
                    new Label("URL:"), urlField,
                    new Label("Notes:"), noteField,
                    new Label("Current Icon:"),
                    iconImageView,
                    selectIconButton,
                    ignoreDomainCheckBox,
                    new HBox(10, saveButton, launchButton, deleteButton)
            );

            // Enable buttons and checkbox
            launchButton.setDisable(false);
            saveButton.setDisable(false);
            saveButton.setOnAction(e -> controller.saveSelectedEntry(iconImageView));
            deleteButton.setDisable(false);
            ignoreDomainCheckBox.setDisable(false);
        }
    }

    /**
     * Show when errors happen to the user.
     * @param errorMessage - the error to show the user
     */
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

    /* GETTERS BELOW */
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