package com.igearfs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JnlpLauncherApp extends Application {

    private List<LaunchEntry> entries = new ArrayList<>();
    private ListView<HBox> listView;
    private TextField nameField;
    private TextField urlField;
    private Button launchButton;
    private Button saveButton;
    private final String DATA_FILE = System.getProperty("user.home") + File.separator + "jnlp_entries.txt";  // Save to the user's home directory

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JNLP Launcher");

        // Left Pane - Scrollable list of entries
        listView = new ListView<>();
        listView.setPrefWidth(250);
        listView.setMaxHeight(400);
        listView.setStyle("-fx-background-color: #f4f4f4;");

        // Set up the right pane (Details of selected entry)
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: #e8e8e8;");

        // Labels to display selected entry's name and URL
        nameField = new TextField();
        urlField = new TextField();

        // Launch and Save buttons
        launchButton = new Button("Launch");
        saveButton = new Button("Save");

        // Default behavior for buttons
        launchButton.setDisable(true); // Disable until an item is selected
        saveButton.setDisable(true);   // Disable until an item is selected

        // Create button container to place save and launch next to each other
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.getChildren().addAll(saveButton, launchButton);

        // Add elements to the right pane
        rightPane.getChildren().addAll(new Label("Name:"), nameField, new Label("URL:"), urlField, buttonContainer);

        // Create the main layout (SplitPane)
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.4); // 40% for the left, 60% for the right
        splitPane.getItems().addAll(createLeftPane(), rightPane);

        // Scene setup
        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Button actions
        launchButton.setOnAction(e -> launchSelectedEntry());
        saveButton.setOnAction(e -> saveSelectedEntry());

        // Load entries from the file when the application starts
        loadEntriesFromFile();
    }

    private VBox createLeftPane() {
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));

        // Add Add Entry button
        Button addButton = new Button("Add Entry");
        addButton.setOnAction(e -> addNewEntry());

        leftPane.getChildren().add(addButton);

        // Add the list view for entries
        listView.setOnMouseClicked(e -> updateRightPane());

        // Populate with existing entries from file
        populateListView();

        leftPane.getChildren().add(listView);
        return leftPane;
    }

    private void populateListView() {
        listView.getItems().clear();

        // Add entries to ListView
        for (LaunchEntry entry : entries) {
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            ImageView imageView = new ImageView(new Image("https://via.placeholder.com/32")); // Placeholder icon
            imageView.setFitWidth(32);
            imageView.setFitHeight(32);

            Label nameLabel = new Label(entry.getName());
            hbox.getChildren().addAll(imageView, nameLabel);

            listView.getItems().add(hbox);
        }
    }

    private void updateRightPane() {
        // Get the selected item from the ListView
        HBox selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Find the index of the selected item
            int selectedIndex = listView.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            // Update the right pane with details of the selected entry
            nameField.setText(selectedEntry.getName());
            urlField.setText(selectedEntry.getUrl());

            // Enable buttons
            launchButton.setDisable(false);
            saveButton.setDisable(false);
        }
    }

    private void addNewEntry() {
        // In a real app, you might open a new window to add the entry
        String newName = "New Entry";
        String newUrl = "https://localhost:8443/webstart";
        LaunchEntry newEntry = new LaunchEntry(newName, newUrl);

        entries.add(newEntry);

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        ImageView imageView = new ImageView(new Image("https://via.placeholder.com/32")); // Placeholder icon
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);

        Label nameLabel = new Label(newEntry.getName());
        hbox.getChildren().addAll(imageView, nameLabel);

        listView.getItems().add(hbox);

        // Save the new entry list to the file
        saveEntriesToFile();
    }

    private void launchSelectedEntry() {
        // Get the selected item from the ListView
        HBox selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Find the index of the selected item
            int selectedIndex = listView.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);

            // Get the URL from the selected entry
            String jnlpUrl = selectedEntry.getUrl();

            System.out.println("Launching JNLP from: " + jnlpUrl);

            try {
                // Trust the server certificate by using the default JRE truststore
                TrustStoreManager.trustUrl(jnlpUrl);  // Automatically uses the default truststore from JRE

                // Now SSL verification will trust the JNLP URL server's certificate
                JnlpLauncher.loadJnlpAndLaunch(jnlpUrl);

                // Show success alert
                showAlert(Alert.AlertType.INFORMATION, "Launch Successful", "The JNLP application has been launched successfully.");

                // Close the current window if needed (optional)
                Stage stage = (Stage) launchButton.getScene().getWindow();
                stage.close();

            } catch (Exception e) {
                System.err.println("Error during JNLP launch process: " + e.getMessage());
                e.printStackTrace();

                // Show error alert
                showAlert(Alert.AlertType.ERROR, "Launch Failed", "An error occurred during the launch: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Optional: You can customize the header if needed
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveSelectedEntry() {
        // Save the selected entry (e.g., save to file or update)
        HBox selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int selectedIndex = listView.getItems().indexOf(selectedItem);
            LaunchEntry selectedEntry = entries.get(selectedIndex);
            selectedEntry.setName(nameField.getText());
            selectedEntry.setUrl(urlField.getText());
            System.out.println("Saved JNLP entry: " + selectedEntry.getName());

            // Re-populate the ListView after the update
            populateListView();

            // Save entries after updating
            saveEntriesToFile();
        }
    }

    private void saveEntriesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (LaunchEntry entry : entries) {
                writer.write(entry.getName() + "|" + entry.getUrl());
                writer.newLine();
            }
            System.out.println("Entries saved to " + DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEntriesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    entries.add(new LaunchEntry(parts[0], parts[1]));
                }
            }
            System.out.println("Entries loaded from " + DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // After loading entries, populate the ListView
        populateListView();
    }

    // Simple LaunchEntry class to hold name and URL
    private static class LaunchEntry {
        private String name;
        private String url;

        public LaunchEntry(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
