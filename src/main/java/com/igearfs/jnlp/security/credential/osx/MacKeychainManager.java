package com.igearfs.jnlp.security.credential.osx;

import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;

public class MacKeychainManager {

    // Load macOS Security Framework
    public interface SecurityLibrary extends Library {
        SecurityLibrary INSTANCE = (SecurityLibrary) Native.load("Security", SecurityLibrary.class);

        // SecKeychainItemAdd: Add an item to the keychain
        int SecKeychainItemAdd(Pointer keychain, Pointer item, PointerByReference result);

        // SecKeychainItemCopyAttributesAndData: Read an item from the keychain
        int SecKeychainItemCopyAttributesAndData(Pointer keychain, Pointer item, PointerByReference attr, PointerByReference data);

        // SecKeychainItemDelete: Delete an item from the keychain
        int SecKeychainItemDelete(Pointer keychain, Pointer item);
    }

    /**
     * Save username and password to macOS Keychain under a specific key name.
     *
     * @param keyName  The key name under which the credentials (username/password) will be saved.
     * @param username The username to be saved.
     * @param password The password to be saved.
     * @return true if the credentials were saved successfully, false otherwise.
     */
    public static boolean saveCredentials(String keyName, String username, String password) {
        // Convert username and password to UTF-8 (Keychain expects data in UTF-8 or UTF-16)
        byte[] usernameBytes = username.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] passwordBytes = password.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // Set up the attributes for the keychain item
        Pointer accountPointer = new Memory(keyName.length() * Native.WCHAR_SIZE);
        accountPointer.setString(0, keyName);

        Pointer usernamePointer = new Memory(usernameBytes.length);
        usernamePointer.write(0, usernameBytes, 0, usernameBytes.length);

        Pointer passwordPointer = new Memory(passwordBytes.length);
        passwordPointer.write(0, passwordBytes, 0, passwordBytes.length);

        PointerByReference ref = new PointerByReference();

        // Add item to the Keychain
        int status = SecurityLibrary.INSTANCE.SecKeychainItemAdd(null, accountPointer, ref);

        if (status != 0) {
            System.err.println("Failed to add item to Keychain. Error code: " + status);
            return false;
        }
        return true;
    }

    /**
     * Load username and password from macOS Keychain using the key name.
     *
     * @param keyName The key name of the stored credentials.
     * @return A string array containing username and password if found, null if not found or on error.
     */
    public static String[] loadCredentials(String keyName) {
        Pointer accountPointer = new Memory(keyName.length() * Native.WCHAR_SIZE);
        accountPointer.setString(0, keyName);

        // Use the macOS Keychain API to retrieve the credentials
        PointerByReference dataRef = new PointerByReference();
        PointerByReference attrRef = new PointerByReference();

        int status = SecurityLibrary.INSTANCE.SecKeychainItemCopyAttributesAndData(null, accountPointer, attrRef, dataRef);

        if (status != 0) {
            System.err.println("Failed to read item from Keychain. Error code: " + status);
            return null;
        }

        // Retrieve the credential data (the raw bytes)
        Pointer credData = dataRef.getValue();
        byte[] credentialData = credData.getByteArray(0, credData.getInt(0)); // Get the raw byte array

        // Now we need to extract the username and password manually from the byte array
        int usernameLength = 0;
        while (usernameLength < credentialData.length && credentialData[usernameLength] != 0) {
            usernameLength++;  // Find the length of the username (terminated by null byte)
        }

        int passwordLength = credentialData.length - usernameLength - 1;  // Password comes after the null-terminated username

        // Extract the username and password
        String username = new String(credentialData, 0, usernameLength, java.nio.charset.StandardCharsets.UTF_8);
        String password = new String(credentialData, usernameLength + 1, passwordLength, java.nio.charset.StandardCharsets.UTF_8);

        return new String[]{username, password};
    }

    // Main method to test saving and loading credentials (username/password)
    public static void main(String[] args) {
        String keyName = "MyApp-Credentials";
        String username = "bob";
        String password = "SecurePass123";

        // Saving the username and password under the specified key name
        boolean saved = saveCredentials(keyName, username, password);
        System.out.println("Credentials saved: " + saved);

        // Loading the credentials using the key name
        String[] credentials = loadCredentials(keyName);
        if (credentials != null) {
            System.out.println("Username: " + credentials[0]);
            System.out.println("Password: " + credentials[1]);
        } else {
            System.out.println("Failed to load credentials.");
        }
    }
}
