/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

package com.igearfs.jnlp.controller;

import com.igearfs.jnlp.JnlpLauncher;
import com.igearfs.jnlp.JnlpLauncherApp;
import com.igearfs.jnlp.model.LaunchEntry;
import com.igearfs.jnlp.util.ColorGridCell;
import com.igearfs.jnlp.util.LaunchEntryManager;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import static com.igearfs.jnlp.util.LaunchEntryManager.saveEntriesToFile;

public class LaunchEntryController {
    private final JnlpLauncherApp app;
    private final List<LaunchEntry> entries;
    private final Button saveButton;
    private final Button launchButton;
    private final TextField nameField;
    private final TextField urlField;
    private final TextArea noteField;
    private final CheckBox ignoreDomainCheckBox;
    private final Button deleteButton;
    private final ListView<HBox> listViewJnlp;
    private final TextField searchField;

    public LaunchEntryController(JnlpLauncherApp app) {
        this.app = app;
        this.entries = app.getEntries();
        this.saveButton = app.getSaveButton();
        this.launchButton = app.getLaunchButton();
        this.nameField = app.getNameField();
        this.urlField = app.getUrlField();
        this.noteField = app.getNoteField();
        this.deleteButton = app.getDeleteButton();
        this.ignoreDomainCheckBox = app.getIgnoreDomainCheckBox();
        this.listViewJnlp = app.getListViewJnlp();
        this.searchField = app.getSearchField();

    }

    public void saveSelectedEntry(ImageView iconImageView) {
        HBox selectedItem = listViewJnlp.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = listViewJnlp.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            // Save the original ID before changes
            String selectedEntryId = selectedEntry.getId();
            selectedEntry.setName(nameField.getText());
            selectedEntry.setUrl(urlField.getText());
            selectedEntry.setNote(noteField.getText());
            selectedEntry.setIgnoreDomainValidation(ignoreDomainCheckBox.isSelected());
            if(ColorGridCell.lastSelectedCell != null) {
                selectedEntry.setIconPath(ColorGridCell.lastSelectedCell.getIconName());
            }
            LaunchEntryManager.saveEntriesToFile(entries);
            populateListView();

            // Find the updated entry by its original ID and reselect it
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getId().equals(selectedEntryId)) {
                    HBox hbox = (HBox) listViewJnlp.getItems().get(i);
                    listViewJnlp.getSelectionModel().select(hbox);
                    break;
                }
            }
        }
    }

    public void populateListView() {
        listViewJnlp.getItems().clear();
        String currentSearchText = searchField.getText();

        if (!currentSearchText.isEmpty()) {
            filterList(currentSearchText);
        } else {
            for (LaunchEntry entry : entries) {
                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER_LEFT);

                // Get the icon path from the entry
                String iconPath = "/icons/" + entry.getIconPath();

                // Create the ImageView for the icon
                ImageView imageView;
                try {
                    // If the iconPath is relative and inside the resources folder, use getResource
                    URL iconUrl = getClass().getResource(iconPath);  // Assumes iconPath is a relative path inside resources
                    if (iconUrl != null) {
                        imageView = new ImageView(new Image(iconUrl.toExternalForm()));
                    } else {
                        // If the icon is not found, use a fallback icon
                        imageView = new ImageView(new Image(getClass().getResource("/icons/rocket.png").toExternalForm()));
                    }
                } catch (Exception e) {
                    // If there's an error loading the image, fall back to default icon
                    imageView = new ImageView(new Image(getClass().getResource("/icons/rocket.png").toExternalForm()));
                }

                imageView.setFitWidth(32);
                imageView.setFitHeight(32);

                // Create the name label
                Label nameLabel = new Label(entry.getName());

                // Add the icon and label to the HBox
                hbox.getChildren().addAll(imageView, nameLabel);
                listViewJnlp.getItems().add(hbox);
            }
        }
    }

    public void filterList(String searchText) {
        listViewJnlp.getItems().clear();
        for (LaunchEntry entry : entries) {
            if (entry.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    entry.getUrl().toLowerCase().contains(searchText.toLowerCase()) ||
                    entry.getId().toLowerCase().contains(searchText.toLowerCase()) ||
                    entry.getNote().toLowerCase().contains(searchText.toLowerCase())) {

                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER_LEFT);

                // Get the icon path from the entry
                String iconPath = entry.getIconPath();

                // Create the ImageView for the icon
                ImageView imageView;
                try {
                    // If the iconPath is relative and inside the resources folder, use getResource
                    URL iconUrl = getClass().getResource(iconPath);  // Assumes iconPath is a relative path inside resources
                    if (iconUrl != null) {
                        imageView = new ImageView(new Image(iconUrl.toExternalForm()));
                    } else {
                        // If the icon is not found, use a fallback icon
                        imageView = new ImageView(new Image(getClass().getResource("/icons/rocket.png").toExternalForm()));
                    }
                } catch (Exception e) {
                    // If there's an error loading the image, fall back to default icon
                    imageView = new ImageView(new Image(getClass().getResource("/icons/rocket.png").toExternalForm()));
                }

                imageView.setFitWidth(32);
                imageView.setFitHeight(32);

                // Create the name label
                Label nameLabel = new Label(entry.getName());

                // Add the icon and label to the HBox
                hbox.getChildren().addAll(imageView, nameLabel);
                listViewJnlp.getItems().add(hbox);
            }
        }
    }



    public void launchSelectedEntry() {
        HBox selectedItem = listViewJnlp.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            int selectedIndex = listViewJnlp.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            try {
                JnlpLauncher.loadJnlpAndLaunch(selectedEntry);
            } catch (Exception e) {
                showErrorAlert(e.getMessage());
            }
        }
    }

    public void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public void deleteSelectedEntry() {
        HBox selectedItem = listViewJnlp.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            int selectedIndex = listViewJnlp.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this entry?");
            alert.setContentText(selectedEntry.getName());
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Delete entry from the list
                    entries.remove(selectedEntry);
                    saveEntriesToFile(entries);
                    resetRightPane();
                    populateListView();
                }
            });
        }
    }

    private void resetRightPane() {
        // Clear all fields and disable buttons
        nameField.clear();
        urlField.clear();
        noteField.clear();
        ignoreDomainCheckBox.setSelected(true);  // Default to true
        ignoreDomainCheckBox.setDisable(true);
        launchButton.setDisable(true);
        saveButton.setDisable(true);
        deleteButton.setDisable(true);
    }
}
