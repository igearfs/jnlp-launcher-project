package com.igearfs.jnlp.util;

import com.igearfs.jnlp.model.LaunchEntry;

import java.io.*;
import java.util.*;

public class LaunchEntryManager {

    private static final String DATA_FILE = System.getProperty("user.home") + File.separator + "jnlp_entries.txt";

    public static void loadEntriesFromFile(List<LaunchEntry> entries) {
        File dataFile = new File(DATA_FILE);
        boolean newSaves = false;

        if (dataFile.exists() && dataFile.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
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

                System.out.println("Entries loaded from " + DATA_FILE);
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (LaunchEntry entry : entries) {
                writer.write(entry.getName() + "|" + entry.getUrl() + "|" + entry.getNote() + "|" +
                        entry.getId() + "|" + entry.isIgnoreDomainValidation() + "|" + entry.getIconPath());
                writer.newLine();
            }
            System.out.println("Entries saved to " + DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the entries by name (case insensitive)
        entries.sort(Comparator.comparing(LaunchEntry::getName, String::compareToIgnoreCase));
    }
}
