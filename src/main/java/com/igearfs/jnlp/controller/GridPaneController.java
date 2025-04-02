/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

package com.igearfs.jnlp.controller;

import com.igearfs.jnlp.util.ColorGridCell;
import com.igearfs.jnlp.util.IconLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class GridPaneController {

    private int currentPage = 0; // Keep track of the current page
    private int iconsPerPage = 100; // Number of icons to show per page (adjust this as necessary)
    private List<String> iconNames = IconLoader.loadIconNames("/icons_list.txt"); // Load icon names from the config file

    public void showIconChooserForNewEntry(ImageView iconImageView) {
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
        doneButton.setOnAction(e -> {
            // Get the currently selected image from the ColorGridCell
            Image selectedImage = ColorGridCell.getSelectedImage();

            if (selectedImage != null) {
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

    private void loadIconsForPage(GridPane iconGrid) {
        iconGrid.getChildren().clear();  // Clear the previous icons
        int row = 0, col = 0;

        // Calculate the starting and ending index based on the current page
        int startIndex = currentPage * iconsPerPage;
        int endIndex = Math.min(startIndex + iconsPerPage, iconNames.size());

        for (int i = startIndex; i < endIndex; i++) {
            String iconName = iconNames.get(i);

            // Load the icon image from resources
            Image iconImage = new Image(getClass().getResourceAsStream("/icons/" + iconName));

            // Create a new ColorGridCell for each icon
            ColorGridCell iconCell = new ColorGridCell(iconName, iconImage);

            // Add the ColorGridCell to the GridPane
            iconGrid.add(iconCell, col, row);
            col++;

            // Move to the next row after 10 icons in the current row
            if (col == 10) {
                col = 0;
                row++;
            }
        }

        // Reset the last selected cell when loading a new page of icons
        ColorGridCell.lastSelectedCell = null;
    }


    // Pagination Logic: Show next page
    private void loadNextPage(GridPane iconGrid) {
        currentPage++;
        loadIconsForPage(iconGrid);  // Reload with next page's icons
    }

    // Pagination Logic: Show previous page
    private void loadPreviousPage(GridPane iconGrid) {
        if (currentPage > 0) {
            currentPage--;
            loadIconsForPage(iconGrid);  // Reload with previous page's icons
        }
    }
}
