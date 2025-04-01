/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

package com.igearfs.jnlp.util;

import com.igearfs.jnlp.model.LaunchEntry;

import java.io.*;
import java.util.*;

public class LaunchEntryManager {

    private static final String DATA_FILE = "jnlp_entries.txt";
    private static final String USER_DATA_DIR = System.getProperty("user.home") + "/AppData/Roaming/SyncSyndicate/data";
    public static void loadEntriesFromFile(List<LaunchEntry> entries) {
        File dataDirectory = new File(USER_DATA_DIR);
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        File dataFile = new File(USER_DATA_DIR + "/" + DATA_FILE);
        boolean newSaves = false;

        if (dataFile.exists() && dataFile.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_DIR + "/" + DATA_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");

                    boolean ignoreDomainValidation = true; // Default to true for older entries
                    String iconPath = "/icons/rocket.png"; // Default icon path

                    if (parts.length == 3) { // Old format (name, url, note)
                        parts = new String[]{parts[0], parts[1], parts[2], UUID.randomUUID().toString(), "true", iconPath};
                        newSaves = true;
                    } else if (parts.length == 2) { // Even older format (name, url)
                        parts = new String[]{parts[0], parts[1], "", UUID.randomUUID().toString(), "true", iconPath};
                        newSaves = true;
                    } else if (parts.length == 4) { // Entries missing ignoreDomainValidation
                        parts = new String[]{parts[0], parts[1], parts[2], parts[3], "true", iconPath};
                        newSaves = true;
                    } else if (parts.length == 5) { // New format with ignoreDomainValidation and missing icon
                        ignoreDomainValidation = Boolean.parseBoolean(parts[4]);
                        parts = new String[]{parts[0], parts[1], parts[2], parts[3], String.valueOf(ignoreDomainValidation), iconPath};
                        newSaves = true;
                    } else if (parts.length == 6) { // New format with all information
                        ignoreDomainValidation = Boolean.parseBoolean(parts[4]);
                        iconPath = parts[5];
                        newSaves = true;
                    }

                    // Add the entry to the list
                    entries.add(new LaunchEntry(parts[0], parts[1], parts[2], parts[3], ignoreDomainValidation, iconPath));
                }

                if (newSaves) {
                    saveEntriesToFile(entries); // Save to update old format entries
                }

                System.out.println("Entries loaded from " + USER_DATA_DIR + "/" + DATA_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No previous entries found or file is empty.");
        }

        // Sort the entries by name (case insensitive)
        entries.sort(Comparator.comparing(LaunchEntry::getName, String::compareToIgnoreCase));
    }

    public static void saveEntriesToFile(List<LaunchEntry> entries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_DIR + "/" + DATA_FILE))) {
            for (LaunchEntry entry : entries) {
                writer.write(entry.getName() + "|" + entry.getUrl() + "|" + entry.getNote() + "|" +
                        entry.getId() + "|" + entry.isIgnoreDomainValidation() + "|" + entry.getIconPath());
                writer.newLine();
            }
            System.out.println("Entries saved to " + USER_DATA_DIR + "/" + DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the entries by name (case insensitive)
        entries.sort(Comparator.comparing(LaunchEntry::getName, String::compareToIgnoreCase));
    }
}
