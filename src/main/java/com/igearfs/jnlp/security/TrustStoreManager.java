/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

package com.igearfs.jnlp.security;

import com.igearfs.jnlp.model.LaunchEntry; // Import LaunchEntry class
import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class TrustStoreManager {

    // Dynamically get the JRE path using the system property
    private static final String LOCAL_JRE_PATH = System.getProperty("java.home");  // Dynamically set the local path to the system JRE
    private static final String TRUSTSTORE_PATH = LOCAL_JRE_PATH + "/lib/security/cacerts";
    private static final String TRUSTSTORE_PASSWORD = "changeit"; // Default password for the truststore

    // Method to download the server's SSL certificate without SSL validation
    public static X509Certificate downloadCertificate(String jnlpUrl) throws Exception {
        URL url = new URL(jnlpUrl);
        System.out.println("URL made");

        // Use a custom TrustManager that doesn't perform any certificate verification
        TrustManager[] trustAllCertificates = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;  // Trust any certificate
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // Open connection
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.connect();
        System.out.println("Connection established");

        // Get the server certificates
        Certificate[] certificates = connection.getServerCertificates();
        System.out.println("Certificates received: " + certificates);

        // Get the first certificate in the chain (the server's certificate)
        if (certificates.length > 0) {
            return (X509Certificate) certificates[0];
        } else {
            throw new Exception("No certificates found in the server's response.");
        }
    }

    // Method to save the certificate to a file
    public static void saveCertificateToFile(X509Certificate cert, String certFilePath) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(certFilePath)) {
            fos.write(cert.getEncoded());
            System.out.println("Certificate saved to " + certFilePath);
        }
    }

    // Method to run keytool to add the certificate to the JRE truststore (cacerts)
    public static void addCertificateToTruststoreWithKeyTool(String certFilePath) throws Exception {
        // Run the keytool command to import the certificate
        ProcessBuilder processBuilder = new ProcessBuilder(
                "keytool",
                "-importcert",
                "-file", certFilePath,
                "-keystore", TRUSTSTORE_PATH,
                "-storepass", TRUSTSTORE_PASSWORD,
                "-noprompt",
                "-alias", "custom-cert"
        );

        processBuilder.inheritIO();  // Allow the process to print output to the console
        Process process = processBuilder.start();
        process.waitFor();
        System.out.println("Certificate successfully imported using keytool.");
    }

    // Method to set the default SSL context using the updated truststore
    public static void setDefaultSSLContext() throws Exception {
        KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream truststoreInputStream = new FileInputStream(TRUSTSTORE_PATH)) {
            truststore.load(truststoreInputStream, TRUSTSTORE_PASSWORD.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(truststore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());

        SSLContext.setDefault(sslContext);
        System.out.println("SSL context set to use updated truststore.");
    }

    // Dynamic method to validate domain based on the ignoreDomainValidation flag
    public static void setCustomHostnameVerifier(LaunchEntry entry)
    {
        // Only disable domain validation if the user chose to ignore it
        if (entry.isIgnoreDomainValidation()) {
            System.out.println("Domain validation disabled for: " + entry.getUrl());
            // Disable domain validation by accepting any hostname
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } else {
            System.out.println("Domain validation enabled for: " + entry.getUrl());
            // Validate that the hostname matches the URL's host
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> {
                try {
                    return hostname.equals(new URL(entry.getUrl()).getHost());
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Invalid URL format: " + entry.getUrl(), e);
                }
            });
        }
    }

    // Method to trust a URL (downloads the certificate, adds it to the default truststore)
    public static void trustUrl(LaunchEntry entry) {
        try {
            // Step 1: Set hostname verification based on the LaunchEntry ignoreDomainValidation field
            setCustomHostnameVerifier(entry);

            // Step 2: Download the server certificate from the given URL without SSL validation
            X509Certificate cert = downloadCertificate(entry.getUrl());
            System.out.println("Cert Downloaded");

            // Step 3: Save the downloaded certificate to a file
            String certFilePath = "server-cert.cer";
            saveCertificateToFile(cert, certFilePath);

            // Step 4: Add the certificate to the JRE truststore using keytool
            addCertificateToTruststoreWithKeyTool(certFilePath);

            // Step 5: Update the default SSL context to use the updated truststore
            setDefaultSSLContext();
            System.out.println("Server certificate trusted successfully and SSL context updated.");

            // Step 6: Make a secure connection to the server again to prove it works
            URL url = new URL(entry.getUrl());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();  // Attempt to connect again using the updated truststore
            System.out.println("Successfully connected to the server: " + entry.getUrl());

        } catch (SSLHandshakeException e) {
            System.err.println("SSL Handshake Exception encountered.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to trust URL: " + entry.getUrl());
        }
    }

    // Example usage
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java TrustStoreManager <jnlp-url>");
            System.exit(1);
        }

        // Create LaunchEntry and pass it to the trustUrl method
        LaunchEntry entry = new LaunchEntry("My Application", args[0], "Description", "1", true, "/rocket"); // Example entry
        trustUrl(entry);
    }
}
