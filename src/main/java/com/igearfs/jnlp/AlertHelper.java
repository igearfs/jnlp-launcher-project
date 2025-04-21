/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertHelper
{
    public static void showAlert(Alert.AlertType alertType, String title, String header, String content)
    {
        // Using Platform.runLater to ensure the alert shows on the JavaFX Application Thread
        Platform.runLater(() ->
        {
            showAlertWithNoRunner(alertType, title, header, content);
        });
    }

    public static void showAlertAndCloseSpinner(LoadingPopup lp, Alert.AlertType alertType, String title, String header, String content)
    {
        // Using Platform.runLater to ensure the alert shows on the JavaFX Application Thread
        Platform.runLater(() ->
        {
            lp.hide();
            // Create the alert
            showAlertWithNoRunner(alertType, title, header, content);
        });
    }

    public static void showAlertWithNoRunner(Alert.AlertType alertType, String title, String header, String content)
    {

        // Create the alert
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Show the alert and wait for the user to close it
        alert.showAndWait();

    }

}
