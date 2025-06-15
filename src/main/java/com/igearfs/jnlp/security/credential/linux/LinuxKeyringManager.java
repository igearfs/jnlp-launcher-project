/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp.security.credential.linux;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class LinuxKeyringManager {

    // Define the libsecret library using JNA
    public interface LibSecret extends Library {
        LibSecret INSTANCE = (LibSecret) Native.load("secret-1", LibSecret.class);

        // Function to store a secret
        int secret_password_store_sync(Pointer schema, Pointer attributes, String label, String password, Pointer prompt, Pointer error);

        // Function to retrieve a secret
        String secret_password_lookup_sync(Pointer schema, Pointer attributes, Pointer error);

        // Function to delete a secret
        int secret_password_clear_sync(Pointer schema, Pointer attributes, Pointer error);
    }

    // Helper class for StringMemory (helps in JNA memory allocation)
    private static class StringMemory extends Memory {
        public StringMemory(String str) {
            super((str.length() + 1) * Native.WCHAR_SIZE); // +1 for null terminator
            this.setWideString(0, str); // Set string to memory
        }
    }

    // Save credentials to the keyring
    public static boolean saveCredential(String keyName, String user, String password) {
        LibSecret libSecret = LibSecret.INSTANCE;

        // Create a pointer to the schema
        Pointer schema = createSchema("org.freedesktop.secrets.Generic");

        // Create a pointer for the attributes (key and user)
        Pointer keyAttribute = new StringMemory("key");
        Pointer userAttribute = new StringMemory("user");

        // Create a pointer to the error (can be used to handle errors)
        Pointer error = null;

        // Store password in keyring
        int result = libSecret.secret_password_store_sync(
                schema, keyAttribute, keyName, password, null, error
        );

        // Check the result to determine if storing was successful
        return result == 0; // success if result is 0
    }

    // Load credentials from the keyring
    public static String loadCredential(String keyName) {
        LibSecret libSecret = LibSecret.INSTANCE;

        // Create a pointer to the schema
        Pointer schema = createSchema("org.freedesktop.secrets.Generic");

        // Create a pointer for the attributes
        Pointer keyAttribute = new StringMemory("key");

        // Create a pointer to the error (can be used to handle errors)
        Pointer error = null;

        // Retrieve password from keyring
        return libSecret.secret_password_lookup_sync(schema, keyAttribute, error);
    }

    // Delete credentials from the keyring
    public static boolean deleteCredential(String keyName) {
        LibSecret libSecret = LibSecret.INSTANCE;

        // Create a pointer to the schema
        Pointer schema = createSchema("org.freedesktop.secrets.Generic");

        // Create a pointer for the attributes
        Pointer keyAttribute = new StringMemory("key");

        // Create a pointer to the error (can be used to handle errors)
        Pointer error = null;

        // Delete the stored secret from the keyring
        int result = libSecret.secret_password_clear_sync(schema, keyAttribute, error);
        return result == 0; // success if result is 0
    }

    // For accessing the secret schema
    public static Pointer createSchema(String schemaName) {
        return new StringMemory(schemaName);  // Allocate a pointer for schema
    }

    public static void main(String[] args) {
        String keyName = "myKey";
        String user = "testUser";
        String password = "mySecurePassword";

        // Save credential to the keyring
        System.out.println("Saving credential...");
        boolean saved = saveCredential(keyName, user, password);
        System.out.println("Saved: " + saved);

        // Load credential from the keyring
        System.out.println("Loading credential...");
        String loadedPassword = loadCredential(keyName);
        System.out.println("Loaded Password: " + loadedPassword);

        // Delete credential from the keyring
        System.out.println("Deleting credential...");
        boolean deleted = deleteCredential(keyName);
        System.out.println("Deleted: " + deleted);
    }
}
