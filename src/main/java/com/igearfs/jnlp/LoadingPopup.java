/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * This will eventually pop up when loading so someone isn't wondering if the program is launching.
 */
public class LoadingPopup {

    private final Stage loadingStage;

    public LoadingPopup() {
        ProgressIndicator spinner = new ProgressIndicator();
        StackPane pane = new StackPane(spinner);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-padding: 20;");
        Scene scene = new Scene(pane, 100, 100);

        loadingStage = new Stage(StageStyle.UTILITY);
        loadingStage.initModality(Modality.APPLICATION_MODAL);

        loadingStage.setAlwaysOnTop(true);
        loadingStage.setScene(scene);
    }

    public void show() {
        Platform.runLater(() -> {
            if (!loadingStage.isShowing()) {
                System.out.println("Showing loading popup");
                loadingStage.show();
                // Add a delay after showing the popup (e.g., 3 seconds)
                PauseTransition delay = new PauseTransition(Duration.seconds(3));  // Adjust seconds as needed
                delay.play();  // Start the delay
            }
        });
    }

    public void hide() {
        Platform.runLater(() -> {
            if (loadingStage.isShowing()) {
                System.out.println("Hide loading popup");
                loadingStage.hide();
            }
        });
    }
}
