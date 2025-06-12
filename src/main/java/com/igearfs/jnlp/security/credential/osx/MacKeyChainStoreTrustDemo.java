/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp.security.credential.osx;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.KeyStore;

public class MacKeyChainStoreTrustDemo {

    public static void main(String[] args) throws Exception {
        // Initialize the KeyStore with macOS Keychain
        KeyStore keyStore = KeyStore.getInstance("KeychainStore", "Apple");
        keyStore.load(null, null);

        // Save a credential (password) to the Keychain
        saveToKeychain(keyStore, "MyAppKey", "bob", "SecurePassword123");

        // Load the credential from the Keychain
        loadFromKeychain(keyStore, "MyAppKey");

        // Delete the credential from the Keychain
        deleteFromKeychain("MyAppKey");

        // List all entries in the Keychain (after delete)
        keyStore.aliases().asIterator().forEachRemaining(System.out::println);
    }

    // Method to save a password credential to the Keychain
    private static void saveToKeychain(KeyStore keyStore, String alias, String username, String password) throws Exception {
        byte[] passwordBytes = password.getBytes();
        SecretKey secretKey = new SecretKeySpec(passwordBytes, "AES");

        KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(password.toCharArray());
        keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(secretKey), passwordProtection);
        System.out.println("Saved credential for alias: " + alias);
    }

    // Method to load a password credential from the Keychain
    private static void loadFromKeychain(KeyStore keyStore, String alias) throws Exception {
        KeyStore.Entry entry = keyStore.getEntry(alias, null);
        if (entry instanceof KeyStore.SecretKeyEntry) {
            SecretKey secretKey = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
            System.out.println("Found entry for alias: " + alias);
            System.out.println("Secret key: " + new String(secretKey.getEncoded()));
        } else {
            System.out.println("No entry found for alias: " + alias);
        }
    }

    // Method to delete a credential from the Keychain (using macOS 'security' command)
    private static void deleteFromKeychain(String alias) throws IOException {
        String command = String.format("security delete-generic-password -s %s", alias);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        Process process = processBuilder.start();

        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Successfully deleted credential for alias: " + alias);
            } else {
                System.err.println("Failed to delete credential for alias: " + alias);
            }
        } catch (InterruptedException e) {
            System.err.println("Error during process execution: " + e.getMessage());
        }
    }
}
