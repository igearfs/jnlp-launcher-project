/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp.security.credential.linux;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class KeychainManager {

    // Save the key-value pair (user, password) into the keychain
    public static void save(String key, String user, String pass) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "secret-tool", "store", "--label=" + key, "key", key, "user", user, "password", pass);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            process.waitFor();
            System.out.println("Saved: " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load the password for the given key
    public static String load(String key) {
        StringBuilder result = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder("secret-tool", "lookup", "key", key);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString().trim(); // Return the password or empty string if not found
    }

    // Delete the key from the keychain
    public static void delete(String key) {
        try {
            ProcessBuilder builder = new ProcessBuilder("secret-tool", "forget", "key", key);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            process.waitFor();
            System.out.println("Deleted: " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Save
        save("exampleKey", "myUser", "myPassword");

        // Load
        String password = load("exampleKey");
        System.out.println("Loaded password: " + password);

        // Delete
        delete("exampleKey");
    }
}
