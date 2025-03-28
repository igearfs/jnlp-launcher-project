package com.igearfs.jnlp.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IconLoader {

    /**
     * Loads icon names from the icons_list.txt file in the resources folder.
     *
     * @param configFilePath Path to the icons_list.txt file.
     * @return List of icon filenames.
     */
    public static List<String> loadIconNames(String configFilePath) {
        List<String> iconNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(IconLoader.class.getResourceAsStream(configFilePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                iconNames.add(line.trim());  // Add the icon name (remove any surrounding spaces)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iconNames;
    }

    /**
     * Loads icons as ImageViews from the resources/icons folder.
     *
     * @return List of ImageViews with icons.
     */
    public static List<ImageView> loadIcons() {
        List<ImageView> iconViews = new ArrayList<>();
        // Load the icon names from the icons_list.txt file in the resources folder
        List<String> iconNames = loadIconNames("/icons_list.txt");

        // Loop through each icon name, load the corresponding image, and create an ImageView
        for (String iconName : iconNames) {
            // Load the image from the resources/icons folder using the icon name
            Image iconImage = new Image(IconLoader.class.getResourceAsStream("/icons/" + iconName));
            if (iconImage != null) {
                // Create an ImageView for each icon
                ImageView imageView = new ImageView(iconImage);
                imageView.setFitWidth(20); // Set width (you can adjust this)
                imageView.setFitHeight(20); // Set height (you can adjust this)
                iconViews.add(imageView);
            }
        }
        return iconViews;
    }

    public static ImageView loadIcon(String iconName) {
        ImageView imageView = null;

        Image iconImage = new Image(IconLoader.class.getResourceAsStream("/icons/" + iconName));
        if (iconImage != null) {
            // Create an ImageView for each icon
            imageView = new ImageView(iconImage);
            imageView.setFitWidth(20); // Set width (you can adjust this)
            imageView.setFitHeight(20); // Set height (you can adjust this)
        }

        return imageView;
    }
}
