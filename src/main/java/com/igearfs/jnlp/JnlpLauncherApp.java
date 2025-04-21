/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp;

import com.igearfs.jnlp.controller.LaunchEntryController;
import com.igearfs.jnlp.model.LaunchEntry;
import com.igearfs.jnlp.security.CredentialManager;
import com.igearfs.jnlp.util.ColorGridCell;
import com.igearfs.jnlp.util.IconLoader;
import com.igearfs.jnlp.util.LaunchEntryManager;
import com.microsoft.credentialstorage.model.StoredCredential;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.igearfs.jnlp.JnlpLauncher.clearCacheForEntry;
import static com.igearfs.jnlp.util.LaunchEntryManager.saveEntriesToFile;

/**
 * Main FX Setup to launch the application.
 */
public class JnlpLauncherApp extends Application
{
    private static final Logger logger = LoggerFactory.getLogger(JnlpLauncherApp.class);
    private List<LaunchEntry> entries = new ArrayList<>();
    private ListView<HBox> listViewJnlp;
    private TextField nameField, urlField, searchField, usernameField;
    private TextArea noteField;
    private PasswordField passwordField;

    private Button launchButton, saveButton, deleteButton;

    private CheckBox ignoreDomainCheckBox;

    private boolean isModified = false;
    private boolean isLoading = false;

    private Stage primaryStage;
    private String masterPassword = null; // Holds the master password in memory
    private LaunchEntryController controller;  // Reference to the controller
    private LoadingPopup lp;

    @Override
    public void start(Stage primaryStage)
    {

        initializeUI(primaryStage);

    }

    private void initializeUI(Stage primaryStage)
    {
        isLoading = true;
        lp = new LoadingPopup();
        lp.show();

        this.primaryStage = primaryStage;

        primaryStage.setTitle("JNLP Launcher");

        // Initialize UI components
        listViewJnlp = new ListView<>();
        listViewJnlp.setPrefWidth(250);
        listViewJnlp.setMaxHeight(400);

        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));

        // Adding the new fields for username and password
        usernameField = new TextField(); // Username field
        passwordField = new PasswordField(); // Password field (masked)
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
                new Label("Username:"), usernameField,  // Username label and field
                new Label("Password:"), passwordField,  // Password label and field
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
        isModified = false;
        // Set button actions
        launchButton.setOnAction(e ->
        {
            if (isModified)
            {
                boolean confirmed = showConfirmationDialog("You have unsaved changes. Launch anyway?");
                if (!confirmed)
                {
                    return;
                }
            }
            lp.show();

            PauseTransition pause = new PauseTransition(Duration.millis(1000));
            pause.setOnFinished(ex ->
            {
                // Your long running code here
                controller.launchSelectedEntry();
                lp.hide();
            });
            pause.play();

        });
        primaryStage.setOnCloseRequest(event ->
        {
            if (isModified)
            {
                boolean confirmed = showConfirmationDialog("You have unsaved changes. Exit without saving?");
                if (!confirmed)
                {
                    event.consume(); // Cancel close
                }
                else
                {
                    isModified = false;
                }
            }
        });
        deleteButton.setOnAction(e ->
        {

            controller.deleteSelectedEntry();
        });
        setupChangeListeners();
        lp.hide();
        isLoading = false;
    }

    private VBox createLeftPane()
    {
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        Button addButton = new Button("Add Entry");
        addButton.setOnAction(e ->
        {
            if (isModified)
            {
                boolean confirmed = showConfirmationDialog("You have unsaved changes. Exit without saving?");
                if (!confirmed)
                {
                    e.consume(); // Cancel close
                }
                else
                {
                    isModified = false;
                }
            }
            showAddEntryPopup();
        });

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

        listViewJnlp.setOnMouseClicked(e ->
        {
            isLoading = true;
            if (isModified)
            {
                boolean confirmed = showConfirmationDialog("You have unsaved changes. Exit without saving?");
                if (!confirmed)
                {
                    e.consume(); // Cancel close
                }
                else
                {
                    isModified = false;
                }
            }
            updateRightPane();
            isLoading = false;
        });
        leftPane.getChildren().add(listViewJnlp);
        return leftPane;
    }

    private void showAddEntryPopup()
    {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add New Entry");

        VBox popupLayout = new VBox(10);
        popupLayout.setPadding(new Insets(10));
        popupLayout.setAlignment(Pos.CENTER_LEFT);

        TextField newNameField = new TextField();
        TextField newUrlField = new TextField();
        TextArea newNoteField = new TextArea();
        TextField newUserName = new TextField();
        PasswordField newPasswordField = new PasswordField();

        // Initial image for icon preview (empty initially)
        ImageView iconImageView = new ImageView();
        iconImageView.setFitWidth(32);
        iconImageView.setFitHeight(32);

        // Button to select an icon
        Button selectIconButton = new Button("Select Icon");
        selectIconButton.setOnAction(e -> showIconChooserForNewEntry(iconImageView));

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
                new Label("Username:"), newUserName,
                new Label("Password:"), newPasswordField,
                iconImageView,
                selectIconButton,
                newIgnoreDomainCheckBox,
                buttonBox
        );

        Scene popupScene = new Scene(popupLayout, 400, 500);
        popupStage.setScene(popupScene);

        okButton.setOnAction(e ->
        {
            System.out.println("ICON SAVING FROM POPUP::" + iconImageView.getImage().getUrl());
            // Create a new LaunchEntry with the selected icon path
            LaunchEntry newEntry = new LaunchEntry(
                    newNameField.getText(),
                    newUrlField.getText(),
                    newNoteField.getText(),
                    UUID.randomUUID().toString(),
                    newIgnoreDomainCheckBox.isSelected(),
                    ColorGridCell.lastSelectedCell.getIconName(),
                    newUserName.getText(),
                    newPasswordField.getText()
            );
            entries.add(newEntry);
            saveEntriesToFile(entries);
            isModified = false;
            controller.populateListView();
            popupStage.close();
        });

        cancelButton.setOnAction(e -> popupStage.close());

        popupStage.showAndWait();
    }

    private void setupChangeListeners()
    {
        nameField.textProperty().addListener((obs, oldVal, newVal) ->
        {
            if (!isLoading)
            {
                isModified = true;
            }
        });
        urlField.textProperty().addListener((obs, oldVal, newVal) ->
        {
            if (!isLoading)
            {
                isModified = true;
            }
        });
        noteField.textProperty().addListener((obs, oldVal, newVal) ->
        {
            if (!isLoading)
            {
                isModified = true;
            }
        });
        ignoreDomainCheckBox.selectedProperty().addListener((obs, oldVal, newVal) ->
        {
            if (!isLoading)
            {
                isModified = true;
            }
        });
    }


    private int currentPage = 0; // Keep track of the current page
    private int iconsPerPage = 100; // Number of icons to show per page (adjust this as necessary)
    private List<String> iconNames = IconLoader.loadIconNames("/icons_list.txt"); // Load icon names from the config file

    private void showIconChooserForNewEntry(ImageView iconImageView)
    {
        // Create a new Stage (window) for the icon selection
        Stage iconSelectionStage = new Stage();
        iconSelectionStage.initModality(Modality.APPLICATION_MODAL);
        iconSelectionStage.setTitle("Select Icon");

        // Create a GridPane for displaying icons
        GridPane iconGrid = new GridPane();
        iconGrid.setHgap(1);
        iconGrid.setVgap(1);
        iconGrid.setPadding(new Insets(1));

        // Create Next and Previous Buttons for pagination (if needed)
        Button nextButton = new Button("Next");
        Button prevButton = new Button("Previous");

        // Create Done and Cancel Buttons
        Button doneButton = new Button("Done");
        Button cancelButton = new Button("Cancel");

        // Done button action
        doneButton.setOnAction(e ->
        {
            // Get the currently selected image from the ColorGridCell
            Image selectedImage = ColorGridCell.getSelectedImage();

            if (selectedImage != null)
            {
                // Update the ImageView with the selected icon in the main screen
                iconImageView.setImage(selectedImage);


            }

            // Close the dialog after the selection is made
            iconSelectionStage.close();
        });


        // Cancel button action
        cancelButton.setOnAction(e -> iconSelectionStage.close());

        // Pagination buttons (if you plan to add pagination)
        nextButton.setOnAction(e -> loadNextPage(iconGrid));
        prevButton.setOnAction(e -> loadPreviousPage(iconGrid));

        // Pagination buttons container
        HBox paginationButtons = new HBox(10, prevButton, nextButton, doneButton, cancelButton);
        paginationButtons.setAlignment(Pos.CENTER);

        // Create a VBox to hold everything (GridPane + Pagination + Action Buttons)
        VBox vbox = new VBox(10, iconGrid, paginationButtons);

        // Create a Scene for the custom dialog
        Scene iconSelectionScene = new Scene(vbox, 500, 750); // Increased size of window
        iconSelectionStage.setScene(iconSelectionScene);

        // Load the icons for the current page
        loadIconsForPage(iconGrid);  // First load for the first page

        // Show the dialog (custom window)
        iconSelectionStage.showAndWait();
    }


    private void loadIconsForPage(GridPane iconGrid)
    {
        iconGrid.getChildren().clear();  // Clear the previous icons
        int row = 0, col = 0;

        // Calculate the starting and ending index based on the current page
        int startIndex = currentPage * iconsPerPage;
        int endIndex = Math.min(startIndex + iconsPerPage, iconNames.size());

        for (int i = startIndex; i < endIndex; i++)
        {
            String iconName = iconNames.get(i);

            // Load the icon image from resources
            Image iconImage = new Image(getClass().getResourceAsStream("/icons/" + iconName));

            // Create a new ColorGridCell for each icon
            ColorGridCell iconCell = new ColorGridCell(iconName, iconImage);

            // Add the ColorGridCell to the GridPane
            iconGrid.add(iconCell, col, row);
            col++;

            // Move to the next row after 10 icons in the current row
            if (col == 10)
            {
                col = 0;
                row++;
            }
        }

        // Reset the last selected cell when loading a new page of icons
        ColorGridCell.lastSelectedCell = null;
    }


    // Pagination Logic: Show next page
    private void loadNextPage(GridPane iconGrid)
    {
        currentPage++;
        loadIconsForPage(iconGrid);  // Reload with next page's icons
    }

    // Pagination Logic: Show previous page
    private void loadPreviousPage(GridPane iconGrid)
    {
        if (currentPage > 0)
        {
            currentPage--;
            loadIconsForPage(iconGrid);  // Reload with previous page's icons
        }
    }

    private void updateRightPane()
    {
        ColorGridCell.lastSelectedCell = null;
        HBox selectedItem = listViewJnlp.getSelectionModel().getSelectedItem();
        if (selectedItem != null)
        {
            int selectedIndex = listViewJnlp.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            // Get the icon name (as stored in the LaunchEntry object)
            String iconName = selectedEntry.getIconPath();
            System.out.println("Loading ICON:::" + iconName);
            // Get the actual image for the icon using IconLoader (or resource path)
            ImageView iconImageView = new ImageView();
            if (iconName != null && !iconName.isEmpty())
            {
                // Load the icon using IconLoader
                iconImageView.setImage(IconLoader.loadIcon(iconName).getImage());
            }

            // If no valid icon is found, use a default icon (or empty placeholder)
            if (iconImageView.getImage() == null)
            {
                // Attempt to load the default icon
                InputStream iconStream = getClass().getResourceAsStream("/icons/rocket.png");
                if (iconStream != null)
                {
                    iconImageView.setImage(new Image(iconStream));
                }
                else
                {
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

            StoredCredential creds = CredentialManager.getCredential(selectedEntry.getId());
            if (creds != null)
            {
                usernameField.setText(creds.getUsername());
                passwordField.setText(new String(creds.getPassword()));
            }
            else
            {
                usernameField.setText("");
                passwordField.setText("");
            }

            // Button to select a new icon
            Button selectIconButton = new Button("Select Icon");

            // Store initial icon URL before any changes
            String initialIconPath = iconImageView.getImage() != null ? iconImageView.getImage().getUrl() : "";

            selectIconButton.setOnAction(e ->
            {
                showIconChooserForNewEntry(iconImageView);

                // After selecting an icon, compare the icon path to the original one
                if (!iconImageView.getImage().getUrl().equals(initialIconPath))
                {
                    isModified = true; // Mark as modified if the icon changes
                }
            });

            // New "Clear Cache" Button
            Button clearCacheButton = new Button("Clear Cache");
            clearCacheButton.setOnAction(e ->
            {
                try
                {
                    clearCacheForEntry(selectedEntry);
                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Success", "Cache for entry '" + selectedEntry.getName() + "' cleared successfully.");
                }
                catch (IOException ex)
                {
                    AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Error", "Failed to clear the cache. You will have to manually remove it.");

                }
                System.out.println("Cache cleared!");
            });

            VBox rightPane = (VBox) ((SplitPane) primaryStage.getScene().getRoot()).getItems().get(1);

            // Clear and add all elements to the right pane
            rightPane.getChildren().clear();
            rightPane.getChildren().addAll(
                    new Label("ID: " + selectedEntry.getId()),
                    new Label("Name:"), nameField,
                    new Label("URL:"), urlField,
                    new Label("Notes:"), noteField,
                    new Label("Current Icon:"),
                    new Label("Username:"), usernameField,
                    new Label("Password:"), passwordField,
                    iconImageView,
                    selectIconButton,
                    ignoreDomainCheckBox,
                    new HBox(10, clearCacheButton, saveButton, launchButton, deleteButton)
            );

            // Enable buttons and checkbox
            launchButton.setDisable(false);
            saveButton.setDisable(false);
            saveButton.setOnAction(e ->
            {
                controller.saveSelectedEntry(iconImageView);
                isModified = false;
                AlertHelper.showAlert(Alert.AlertType.CONFIRMATION, "Saved", "Entry Updated", "Save complete");
            });
            deleteButton.setDisable(false);
            ignoreDomainCheckBox.setDisable(false);
        }
    }

    private boolean showConfirmationDialog(String message)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yes, no);

        return alert.showAndWait().filter(response -> response == yes).isPresent();
    }


    public static void main(String[] args)
    {
        launch(args);
    }

    public List<LaunchEntry> getEntries()
    {
        return this.entries;
    }

    public Button getSaveButton()
    {
        return this.saveButton;
    }

    public Button getLaunchButton()
    {
        return this.launchButton;
    }

    public TextField getNameField()
    {
        return this.nameField;
    }

    public TextField getUrlField()
    {
        return this.urlField;
    }

    public TextArea getNoteField()
    {
        return this.noteField;
    }

    public Button getDeleteButton()
    {
        return this.deleteButton;
    }

    public CheckBox getIgnoreDomainCheckBox()
    {
        return this.ignoreDomainCheckBox;
    }

    public ListView<HBox> getListViewJnlp()
    {
        return this.listViewJnlp;
    }

    public TextField getSearchField()
    {
        return this.searchField;
    }

    public TextField getUserNameField()
    {
        return this.usernameField;
    }

    public PasswordField getPasswordField()
    {
        return this.passwordField;
    }

    public Stage getPrimaryStage()
    {
        return this.primaryStage;
    }

    public LoadingPopup getLoadingPopup()
    {
        return this.lp;
    }
}