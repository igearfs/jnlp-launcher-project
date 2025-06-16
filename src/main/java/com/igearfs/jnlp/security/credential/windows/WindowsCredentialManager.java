/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp.security.credential.windows;


import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinBase.*;
import com.sun.jna.win32.StdCallLibrary;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WindowsCredentialManager {

    private static final int CRED_TYPE_GENERIC = 1;
    private static final int CRED_PERSIST_LOCAL_MACHINE = 2; // Store credential for local machine

    public static class CREDENTIAL extends Structure {
        public DWORD Flags;
        public DWORD Type;
        public WString TargetName;
        public WString Comment;
        public FILETIME LastWritten;
        public DWORD CredentialBlobSize;
        public Pointer CredentialBlob;
        public DWORD Persist;
        public DWORD AttributeCount;
        public Pointer Attributes;
        public WString TargetAlias;
        public WString UserName;

        @Override
        protected List<String> getFieldOrder() {
            return List.of(
                    "Flags", "Type", "TargetName", "Comment", "LastWritten",
                    "CredentialBlobSize", "CredentialBlob", "Persist",
                    "AttributeCount", "Attributes", "TargetAlias", "UserName");
        }
    }

    // Interface to Windows API functions (with correct Unicode versions)
    public interface Advapi32 extends Library {
        Advapi32 INSTANCE = Native.load("Advapi32", Advapi32.class);

        boolean CredWriteW(CREDENTIAL credential, int flags);   // Unicode version
        boolean CredReadW(WString targetName, int type, int flags, PointerByReference pCredential);   // Unicode version
        void CredFree(Pointer buffer);
    }

    public interface Advapi32_Credentials extends StdCallLibrary
    {
        Advapi32_Credentials INSTANCE = (Advapi32_Credentials) Native.loadLibrary("advapi32", Advapi32_Credentials.class);

        /*
        BOOL CredEnumerate(
            _In_  LPCTSTR     Filter,
            _In_  DWORD       Flags,
            _Out_ DWORD       *Count,
            _Out_ PCREDENTIAL **Credentials) */
        boolean CredEnumerateW(String filter, int flags, IntByReference count, PointerByReference pref);
    }

    // Save credentials to Windows Credential Manager
    public static boolean saveCredential(String keyName, String user, String password) {
        Advapi32 advapi32 = Advapi32.INSTANCE;

        // Convert the password to UTF-16LE byte array for Windows API compatibility
        byte[] passwordBytes = password.getBytes(java.nio.charset.StandardCharsets.UTF_16LE);

        // Create and initialize the CREDENTIAL structure
        CREDENTIAL cred = new CREDENTIAL();
        cred.Flags = new DWORD(0);  // Default flags, can be used for specific settings
        cred.Type = new DWORD(CRED_TYPE_GENERIC);
        cred.TargetName = new WString(keyName);  // The name under which to store the credential
        cred.Comment = null;  // Optional, can be set to a comment
        cred.LastWritten = new FILETIME();  // Current system time (optional)
        cred.CredentialBlobSize = new DWORD(passwordBytes.length);  // Size of password
        cred.CredentialBlob = new Memory(passwordBytes.length);  // Allocate memory for password blob
        cred.CredentialBlob.write(0, passwordBytes, 0, passwordBytes.length);  // Copy password bytes
        cred.Persist = new DWORD(CRED_PERSIST_LOCAL_MACHINE);  // Set the persistence level (store locally)
        cred.AttributeCount = new DWORD(0);  // No attributes in this example
        cred.Attributes = null;  // No attributes to store
        cred.TargetAlias = null;  // Not setting any alias here
        cred.UserName = new WString(user);  // User's username

        // Attempt to write the credential to the Windows Credential Manager
        boolean result = advapi32.CredWriteW(cred, 0);
        if (!result) {
            int err = Native.getLastError();  // Get error if the operation failed
            System.err.println("CredWrite failed. Error code: " + err);
        }
        return result;  // Return true if successful, false if failed
    }


    // Main for testing
    public static void main(String[] args) {
        String keyName = "MyApp-Credentials";
        String user = "bob";
        String password = "SecurePass123";

        System.out.println("Saving credentials...");
        boolean saved = saveCredential(keyName, user, password);
        System.out.println("Saved: " + saved);

        System.out.println("Loading credentials...");
        GenericWindowsCredentials credentials = loadCredential(keyName);
        if (credentials != null) {
            System.out.println("User: " + credentials.getUsername());
            System.out.println("Password: " + credentials.getPassword());
        } else {
            System.out.println("No credentials found for " + keyName);
        }
    }


    public static GenericWindowsCredentials loadCredential(String targetName) {

        for (GenericWindowsCredentials gwc : enumerateGenericCredentials()) {
            if (gwc.getKey().equals(targetName)) {
                return gwc;
            }
        }
        return null;
    }

    private static List<GenericWindowsCredentials> enumerateGenericCredentials() {
        List<GenericWindowsCredentials> genericCredentials = new ArrayList<>();
        IntByReference pCount = new IntByReference();
        PointerByReference pCredentials = new PointerByReference();

        boolean result = Advapi32_Credentials.INSTANCE.CredEnumerateW(null, 0, pCount, pCredentials);

        Pointer[] ps = pCredentials.getValue().getPointerArray(0,  pCount.getValue());

        for (int i=0; i<pCount.getValue(); i++) {

            Credential arrRef = new Credential(ps[i]);
            arrRef.read();
            if (CredentialType.valueOf(arrRef.Type) == CredentialType.CRED_TYPE_GENERIC) { //only generic credentials

                GenericWindowsCredentials gwc = new GenericWindowsCredentials();
                gwc.setKey(arrRef.TargetName.getWideString(0)); //address
                gwc.setUsername(getUserName(arrRef)); //username

                if (arrRef.CredentialBlobSize > 0) {
                    byte[] bytes = arrRef.CredentialBlob.getByteArray(0, arrRef.CredentialBlobSize);

                    gwc.setPassword(new String(bytes, StandardCharsets.UTF_16LE)); //password
                }

                genericCredentials.add(gwc);

            }
        }
        return genericCredentials;
    }

    private static String getUserName(Credential arrRef) {

        String result = null;
        try {
            if (arrRef.UserName != null) {
                result = arrRef.UserName.getWideString(0);
            }
        } catch (java.lang.Error e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            //System.out.println("UserName: " + result);
        }
        return result;
    }
}
