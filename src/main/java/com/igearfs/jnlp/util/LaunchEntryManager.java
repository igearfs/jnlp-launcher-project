package com.igearfs.jnlp.util;

import com.igearfs.jnlp.model.LaunchEntry;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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
//                    System.out.println("Parts size::"+parts.length);
                    if (parts.length == 3) {
                        String[] newParts = new String[4];
                        newParts[0] = parts[0];
                        newParts[1] = parts[1];
                        newParts[2] = parts[2];
                        parts = newParts;
                        // If an entry doesn't have a GUID, generate one
                        if (parts[3] == null || parts[3].isEmpty()) {
                            parts[3] = UUID.randomUUID().toString(); // Generate a new GUID for this entry
                        }
                        newSaves = true;
                    }
                    if(parts.length == 2)
                    {
                        String[] newParts = new String[4];
                        newParts[0] = parts[0];
                        newParts[1] = parts[1];
                        newParts[2] = "";
                        parts = newParts;
                        // If an entry doesn't have a GUID, generate one
                        if (parts[3] == null || parts[3].isEmpty()) {
                            parts[3] = UUID.randomUUID().toString(); // Generate a new GUID for this entry
                        }
                        newSaves = true;
                    }
                    entries.add(new LaunchEntry(parts[0], parts[1], parts[2], parts[3]));
                }
                if(newSaves)
                {
                    saveEntriesToFile(entries);
                }
                System.out.println("Entries loaded from " + DATA_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No previous entries found or file is empty.");
        }
        // Sort the entries by name (case insensitive)
        Collections.sort(entries, Comparator.comparing(LaunchEntry::getName, String::compareToIgnoreCase));


    }

    public static void saveEntriesToFile(List<LaunchEntry> entries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            // Write the entries back to the file
            for (LaunchEntry entry : entries) {
                writer.write(entry.getName() + "|" + entry.getUrl() + "|" + entry.getNote() + "|" + entry.getId()  );
                writer.newLine();
            }
            System.out.println("Entries saved to " + DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Sort the entries by name (case insensitive)
        Collections.sort(entries, Comparator.comparing(LaunchEntry::getName, String::compareToIgnoreCase));
    }

}
