package com.igearfs;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class TrustStoreManager {

    // Specify the local JRE path directly (Replace with the path to your local JRE)
    private static final String LOCAL_JRE_PATH = "jre";  // Set the local path to your JRE
    private static final String TRUSTSTORE_PATH = LOCAL_JRE_PATH + "/lib/security/cacerts";
    private static final String TRUSTSTORE_PASSWORD = "changeit"; // Default password for the truststore

    // Static block to disable hostname verification for localhost only
    static {
        // For localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {
                    public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                        // Trust "localhost" as a valid hostname
                        if (hostname.equals("localhost")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

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

    // Method to trust a URL (downloads the certificate, adds it to the default truststore)
    public static void trustUrl(String jnlpUrl) {
        try {
            // Step 1: Download the server certificate from the given URL without SSL validation
            X509Certificate cert = downloadCertificate(jnlpUrl);
            System.out.println("Cert Downloaded");

            // Step 2: Save the downloaded certificate to a file
            String certFilePath = "server-cert.cer";
            saveCertificateToFile(cert, certFilePath);

            // Step 3: Add the certificate to the JRE truststore using keytool
            addCertificateToTruststoreWithKeyTool(certFilePath);

            // Step 4: Update the default SSL context to use the updated truststore
            setDefaultSSLContext();
            System.out.println("Server certificate trusted successfully and SSL context updated.");

            // Step 5: Make a secure connection to the server again to prove it works
            URL url = new URL(jnlpUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier((hostname, session) -> true);  // Use this for non-prod certs

            connection.connect();  // Attempt to connect again using the updated truststore
            System.out.println("Successfully connected to the server: " + jnlpUrl);

        } catch (SSLHandshakeException e) {
            System.err.println("SSL Handshake Exception encountered.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to trust URL: " + jnlpUrl);
        }
    }

    // Example usage
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java TrustStoreManager <jnlp-url>");
            System.exit(1);
        }

        String jnlpUrl = args[0];
        trustUrl(jnlpUrl);
    }
}
