package com.igearfs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class JnlpLauncherApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JNLP Launcher");

        Label urlLabel = new Label("JNLP URL:");
        TextField urlField = new TextField();
        urlField.setPrefWidth(400);

        Button launchButton = new Button("Launch");
        Button cancelButton = new Button("Cancel");

        launchButton.setOnAction(e -> {
            String url = urlField.getText();
            if (url != null && !url.trim().isEmpty()) {
                new Thread(() -> {
                    try {
                        JnlpLauncher.main(new String[]{url});
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        });

        cancelButton.setOnAction(e -> primaryStage.close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(urlLabel, 0, 0);
        grid.add(urlField, 1, 0);
        grid.add(launchButton, 0, 1);
        grid.add(cancelButton, 1, 1);

        Scene scene = new Scene(grid, 550, 120);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
