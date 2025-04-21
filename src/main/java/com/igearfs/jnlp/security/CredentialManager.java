package com.igearfs.jnlp.security;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.StorageProvider.SecureOption;
import com.microsoft.credentialstorage.model.StoredCredential;

import java.util.Arrays;

/**
 * Manages secure storage and retrieval of credentials using native OS mechanisms.
 */
public class CredentialManager
{
    private static final String KEY_PREFIX = "syncsyndicate:";
    private static final SecretStore<StoredCredential> store =
            StorageProvider.getCredentialStorage(true, SecureOption.REQUIRED);

    /**
     * Stores a credential securely using the platform's native credential manager.
     *
     * @param key      A unique identifier for the stored credential.
     * @param username The username to store.
     * @param password The password to store (as a String, will be converted to char[]).
     */
    public static void storeCredential(String key, String username, String password)
    {
        if (key == null || username == null || password == null)
        {
            throw new IllegalArgumentException("Key, username, and password must not be null.");
        }
        key = KEY_PREFIX + key;

        char[] passwordChars = password.toCharArray();
        StoredCredential credential = new StoredCredential(username, passwordChars);
        try
        {
            store.add(key, credential);
        }
        finally
        {
            credential.clear();
            Arrays.fill(passwordChars, '\0');
        }
    }

    /**
     * Retrieves the stored password (as a String) for the given key.
     *
     * @param key The identifier of the credential.
     * @return The stored password, or null if not found.
     */
    public static String getPassword(String key)
    {
        key = KEY_PREFIX + key;
        StoredCredential credential = store.get(key);
        if (credential != null)
        {
            try
            {
                return new String(credential.getPassword());
            }
            finally
            {
                credential.clear();
            }
        }
        return null;
    }

    /**
     * Retrieves both username and password as a StoredCredential.
     *
     * @param key The identifier of the credential.
     * @return The StoredCredential object, or null if not found.
     */
    public static StoredCredential getCredential(String key)
    {
        key = KEY_PREFIX + key;
        return store.get(key);
    }

    /**
     * Deletes a stored credential.
     *
     * @param key The identifier of the credential to delete.
     */
    public static void deleteCredential(String key)
    {
        key = KEY_PREFIX + key;
        store.delete(key);
    }

    /**
     * Checks whether a credential exists for the given key.
     *
     * @param key The identifier to check.
     * @return true if the credential exists, false otherwise.
     */
    public static boolean hasCredential(String key)
    {
        key = KEY_PREFIX + key;
        return store.get(key) != null;
    }
}
