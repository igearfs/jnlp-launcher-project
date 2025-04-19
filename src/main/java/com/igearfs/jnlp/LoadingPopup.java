/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingPopup {

    private final Stage loadingStage;

    public LoadingPopup() {
        // Create the loading popup window (Stage)
        loadingStage = new Stage(StageStyle.UTILITY);
        loadingStage.setTitle("Please Wait...");
        loadingStage.initModality(Modality.APPLICATION_MODAL); // Blocks the main window while the popup is visible
        loadingStage.setResizable(false);

        // Create a ProgressIndicator (spinner)
        ProgressIndicator progressIndicator = new ProgressIndicator();

        // Set the size of the ProgressIndicator
        progressIndicator.setPrefSize(100, 100);

        // Create a StackPane to hold the ProgressIndicator
        StackPane root = new StackPane();
        root.getChildren().add(progressIndicator);

        // Create a Scene for the loading popup and add it to the Stage
        Scene scene = new Scene(root, 200, 200);  // Window size is 200x200
        scene.setFill(null); // Transparent background for the popup window
        loadingStage.setScene(scene);

        // Hide the stage initially (spinner should be hidden on start)
        loadingStage.hide();
    }

    // Show the loading popup (the spinner will appear)
    public void show() {
        Platform.runLater(() -> {
            if (!loadingStage.isShowing()) {
                loadingStage.show();  // Show the spinner
                loadingStage.toFront();  // Bring the popup to the front if needed
            }
        });
    }

    // Hide the loading popup (the spinner will disappear)
    public void hide() {
        Platform.runLater(() -> {
            if (loadingStage.isShowing()) {
                loadingStage.hide();  // Hide the spinner
            }
        });
    }
}
