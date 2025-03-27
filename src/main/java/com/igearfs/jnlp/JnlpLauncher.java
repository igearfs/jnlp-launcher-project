package com.igearfs.jnlp;

import com.igearfs.jnlp.model.LaunchEntry;
import com.igearfs.jnlp.security.TrustStoreManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            LaunchEntry entry = new LaunchEntry("My Application", args[0], "Description", "1", true); // Example entry

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
        for (Path jar : downloadedJars) {
            if (classpath.length() > 0) {
                classpath.append(File.pathSeparator);
            }
            classpath.append(jar.toString());
        }
        return classpath.toString();
    }

    private static void launchApp(String mainClass, String classpath, List<String> appArgs) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(JRE_PATH);
        command.add("-cp");
        command.add(classpath);
        command.add(mainClass);
        command.addAll(appArgs);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO();
        processBuilder.start();
    }
}
