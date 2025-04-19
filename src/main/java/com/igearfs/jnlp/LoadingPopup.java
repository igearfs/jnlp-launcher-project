package com.igearfs.jnlp;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoadingPopup {

    private final Stage loadingStage;

    public LoadingPopup() {
        loadingStage = new Stage();
        loadingStage.setScene(new Scene(new StackPane(new ProgressIndicator()), 200, 200));
        loadingStage.setTitle("Loading...");
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
