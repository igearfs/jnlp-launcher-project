/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

package com.igearfs.jnlp.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ColorGridCell extends StackPane {
    private ImageView iconImageView;
    private Rectangle backgroundRectangle;
    private String iconName;
    private boolean isSelected = false;

    // Static variable to track the currently selected cell
    public static ColorGridCell lastSelectedCell = null;

    public ColorGridCell(String iconName, Image iconImage) {
        this.iconName = iconName;

        // Create the background for the cell
        backgroundRectangle = new Rectangle(50, 50, Color.WHITE);  // Adjust cell size
        backgroundRectangle.setArcWidth(10); // Rounded corners
        backgroundRectangle.setArcHeight(10);

        // Create the ImageView for the icon and set size to 40px
        iconImageView = new ImageView(iconImage);
        iconImageView.setFitWidth(40); // Icon size set to 40px
        iconImageView.setFitHeight(40); // Icon size set to 40px

        // Add the background and icon to the cell
        getChildren().addAll(backgroundRectangle, iconImageView);

        // Add mouse click handler to toggle selection
        setOnMouseClicked(event -> {
            // If another cell was selected, deselect it
            if (lastSelectedCell != null) {
                lastSelectedCell.deselect();
            }

            // Select the new cell
            select();
        });
    }

    // Select this cell
    private void select() {
        isSelected = true;
        updateCellStyle();
        lastSelectedCell = this; // Update the static reference to the currently selected cell
    }

    // Deselect this cell
    private void deselect() {
        isSelected = false;
        updateCellStyle();
    }

    // Update the style of the cell based on whether it's selected
    private void updateCellStyle() {
        if (isSelected) {
            backgroundRectangle.setFill(Color.LIGHTBLUE);  // Selected cell color
        } else {
            backgroundRectangle.setFill(Color.WHITE);  // Default background color
        }
    }

    // Getters and setters for the icon name and image
    public String getIconName() {
        return iconName;
    }

    public ImageView getIconImageView() {
        return iconImageView;
    }

    public boolean isSelected() {
        return isSelected;
    }

    // Method to reset selection
    public static void resetSelection() {
        if (lastSelectedCell != null) {
            lastSelectedCell.deselect();
            lastSelectedCell = null;
        }
    }

    // Get the selected image
    public static Image getSelectedImage() {
        return lastSelectedCell != null ? lastSelectedCell.iconImageView.getImage() : null;
    }
}
