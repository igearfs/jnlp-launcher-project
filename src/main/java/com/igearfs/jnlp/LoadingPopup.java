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

        loadingStage = new Stage(StageStyle.TRANSPARENT);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setAlwaysOnTop(true);
        loadingStage.setScene(scene);
    }

    public void show() {
        Platform.runLater(() -> {
            if (!loadingStage.isShowing()) {
                loadingStage.show();
            }
        });
    }

    public void hide() {
        Platform.runLater(() -> {
            if (loadingStage.isShowing()) {
                loadingStage.hide();
            }
        });
    }
}
