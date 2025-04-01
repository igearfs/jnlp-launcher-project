/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

package com.igearfs.jnlp;

import com.igearfs.jnlp.model.LaunchEntry;
import com.igearfs.jnlp.security.TrustStoreManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JnlpLauncher {

    private static final String CACHE_DIR = "jnlp_cache";  // Cache directory
    private static final String JRE_PATH = System.getProperty("java.home") + "/bin/java"; // Dynamically set JRE path

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar jnlp-launcher.jar <jnlp-url>");
            System.exit(1);
        }

        String jnlpUrl = args[0];

        try {

            // Trust the server certificate by using the default JRE truststore
            // Create LaunchEntry and pass it to the trustUrl method
            LaunchEntry entry = new LaunchEntry("My Application", args[0], "Description", "1", true, "/rocket"); // Example entry

            // Now SSL verification will trust the JNLP URL server's certificate
            loadJnlpAndLaunch(entry);

        } catch (Exception e) {
            System.err.println("Error during JNLP launch process: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static void loadJnlpAndLaunch(LaunchEntry entry) throws Exception {
        try {
            // Trust the server certificate by using the default JRE truststore
            TrustStoreManager.trustUrl(entry);  // Automatically uses the default truststore from JRE

        } catch (Exception e) {
            System.err.println("Error during JNLP launch process: " + e.getMessage());
            e.printStackTrace();
        }
        String jnlpUrl = entry.getUrl();
        Document jnlpDoc = loadJnlp(jnlpUrl);
        String mainClass = extractMainClass(jnlpDoc);
        List<String> jarUrls = extractJarUrls(jnlpUrl, jnlpDoc);
        List<String> appArgs = extractAppArgs(jnlpDoc);

        if (mainClass == null || mainClass.isEmpty()) {
            throw new RuntimeException("Main class not found in JNLP");
        }

        List<Path> downloadedJars = new ArrayList<>();
        for (String jarUrl : jarUrls) {
            Path jarPath = downloadJar(jarUrl, jnlpUrl); // Pass jnlpUrl for domain-based cache
            downloadedJars.add(jarPath);
        }

        String classpath = buildClasspath(downloadedJars);
        launchApp(mainClass, classpath, appArgs);
    }

    private static Document loadJnlp(String jnlpUrl) throws Exception {
        System.out.println("Loading JNLP from: " + jnlpUrl);
        URL url = new URL(jnlpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        }
    }

    private static String extractMainClass(Document jnlpDoc) {
        NodeList mainClassNodes = jnlpDoc.getElementsByTagName("application-desc");
        if (mainClassNodes.getLength() > 0) {
            Element mainClassElement = (Element) mainClassNodes.item(0);
            return mainClassElement.getAttribute("main-class");
        }
        return null;
    }

    private static List<String> extractJarUrls(String jnlpUrl, Document jnlpDoc) {
        List<String> jarUrls = new ArrayList<>();
        NodeList jarNodes = jnlpDoc.getElementsByTagName("jar");
        for (int i = 0; i < jarNodes.getLength(); i++) {
            Element jarElement = (Element) jarNodes.item(i);
            String jarUrl = jarElement.getAttribute("href");
            if (!jarUrl.startsWith("http")) {
                jarUrl = jnlpUrl.substring(0, jnlpUrl.lastIndexOf("/") + 1) + jarUrl;
            }
            jarUrls.add(jarUrl);
        }
        return jarUrls;
    }

    private static List<String> extractAppArgs(Document jnlpDoc) {
        List<String> appArgs = new ArrayList<>();
        NodeList argNodes = jnlpDoc.getElementsByTagName("argument");
        for (int i = 0; i < argNodes.getLength(); i++) {
            appArgs.add(argNodes.item(i).getTextContent());
        }
        return appArgs;
    }

    private static Path downloadJar(String jarUrl, String jnlpUrl) throws IOException {
        // Generate a cache folder based on the domain
        String domain = getDomainFromUrl(jnlpUrl);
        Path domainCacheDir = Paths.get(CACHE_DIR, domain);
        if (!Files.exists(domainCacheDir)) {
            Files.createDirectories(domainCacheDir);
        }

        // Create the full file path for the JAR in the cache
        Path jarPath = domainCacheDir.resolve(getFileNameFromUrl(jarUrl));

        // If the JAR file already exists in the cache, skip the download
        if (Files.exists(jarPath)) {
            System.out.println("JAR already exists in cache: " + jarPath.toString());
            return jarPath;
        }

        // Download the JAR file if it doesn't exist
        System.out.println("Downloading JAR from: " + jarUrl);
        URL url = new URL(jarUrl);

        // Open a connection to the URL
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Java JNLP Launcher");
        conn.connect();

        // Download the file to the cache directory
        try (InputStream in = conn.getInputStream();
             OutputStream out = Files.newOutputStream(jarPath)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        System.out.println("Downloaded JAR to: " + jarPath.toString());
        return jarPath;
    }

    private static String getDomainFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getHost();  // Extract the domain (host) from the URL
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL: " + urlString, e);
        }
    }

    private static String getFileNameFromUrl(String jarUrl) {
        // Extract the file name from the URL (e.g., "file.jar" from "http://example.com/file.jar")
        return jarUrl.substring(jarUrl.lastIndexOf('/') + 1);
    }

    private static String buildClasspath(List<Path> downloadedJars) {
        StringBuilder classpath = new StringBuilder();

        // Include the path to the JavaFX SDK lib directory
        String javafxLibPath = "javafx-sdk-17.0.14/lib";  // Path to your javafx-sdk lib folder
        classpath.append(javafxLibPath).append(File.pathSeparator);

        // Add all downloaded JARs to the classpath
        for (Path jar : downloadedJars) {
            if (classpath.length() > 0) {
                classpath.append(File.pathSeparator); // Add separator between paths
            }
            classpath.append(jar.toString());
        }

        // Add the JARs in the subfolders of the 4.5.2 directory
        File libFolder = new File("4.5.2");  // Directory with the 4.5.2 JARs
        addJarFilesFromFolder(libFolder, classpath);

        return classpath.toString();
    }

    private static void addJarFilesFromFolder(File folder, StringBuilder classpath) {
        if (folder.exists() && folder.isDirectory()) {
            // List all files in the directory and its subdirectories
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recurse into subdirectories
                    addJarFilesFromFolder(file, classpath);
                } else if (file.getName().endsWith(".jar")) {
                    // Add the JAR file to the classpath
                    classpath.append(File.pathSeparator).append(file.getAbsolutePath());
                }
            }
        }
    }

    private static void launchApp(String mainClass, String classpath, List<String> appArgs) throws IOException {
        String javafxPath = "javafx-sdk-17.0.14/lib";  // Ensure absolute path

        // Get the default JRE path from java.home
        String javaHome = System.getProperty("java.home");
        String jrePath = javaHome + File.separator + "bin" + File.separator + "java";

        // Check OS and set the correct classpath separator
        String classpathSeparator = System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":";

        List<String> command = new ArrayList<>();
        command.add(jrePath);
        command.add("--module-path");
        command.add(javafxPath);
        command.add("--add-modules");
        command.add("javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.web,javafx.media");

        // --add-opens (necessary to avoid IllegalAccessError)
        String[] opens = {
                "java.base/java.lang", "javafx.base/com.sun.javafx.logging", "javafx.base/com.sun.javafx",
                "javafx.graphics/com.sun.javafx", "javafx.controls/com.sun.javafx",
                "javafx.graphics/com.sun.javafx.application", "javafx.graphics/com.sun.javafx.embed",
                "javafx.graphics/com.sun.javafx.sg.prism", "javafx.graphics/com.sun.prism",
                "javafx.graphics/com.sun.glass.ui", "javafx.graphics/com.sun.javafx.geom.transform",
                "javafx.graphics/com.sun.javafx.tk", "javafx.graphics/com.sun.glass.utils",
                "javafx.controls/com.sun.javafx.scene.control", "javafx.graphics/com.sun.javafx.scene.input",
                "javafx.graphics/com.sun.javafx.stage", "javafx.graphics/com.sun.javafx.geom",
                "javafx.graphics/com.sun.javafx.cursor", "javafx.graphics/com.sun.prism.paint",
                "javafx.graphics/com.sun.javafx.ui", "javafx.graphics/com.sun.javafx.font",
                "java.desktop/sun.awt", "javafx.graphics/com.sun.javafx.util",
                "javafx.graphics/com.sun.javafx.scene", "java.base/java.util"
        };

        for (String open : opens) {
            command.add("--add-opens");
            command.add(open + "=ALL-UNNAMED");
        }

        // Optional: Enable software rendering for JavaFX in case of hardware issues
        command.add("-Dprism.order=sw");

        // Specify JavaFX native libraries path
        command.add("-Djava.library.path=" + javafxPath);

        // Classpath for normal JARs and JavaFX JARs
        command.add("-cp");
        command.add(classpath + classpathSeparator + javafxPath + "/*");

        // Main class
        command.add(mainClass);

        // Add application arguments
        command.addAll(appArgs);

        // Print the generated command for debugging
        System.out.println("Running command: " + String.join(" ", command));

        // Execute using ProcessBuilder (safer than Runtime.exec)
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO(); // Redirects output to the console
        Process process = processBuilder.start();

        // Capture output and errors
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
