package com.igearfs;

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

    private static final String CACHE_DIR = "jnlp_cache";
    private static final String JRE_PATH = "jre/bin/java"; // Relative path to bundled JRE
    private static final String TRUST_STORE_PASSWORD = "changeit";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar jnlp-launcher.jar <jnlp-url>");
            System.exit(1);
        }

        String jnlpUrl = args[0];

        try {
            // Trust the server certificate by using the default JRE truststore
            TrustStoreManager.trustUrl(jnlpUrl);  // Automatically uses the default truststore from JRE

            // Now SSL verification will trust the JNLP URL server's certificate
            loadJnlpAndLaunch(jnlpUrl);

        } catch (Exception e) {
            System.err.println("Error during JNLP launch process: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void loadJnlpAndLaunch(String jnlpUrl) throws Exception {
        Document jnlpDoc = loadJnlp(jnlpUrl);
        String mainClass = extractMainClass(jnlpDoc);
        List<String> jarUrls = extractJarUrls(jnlpUrl, jnlpDoc);
        List<String> appArgs = extractAppArgs(jnlpDoc);

        if (mainClass == null || mainClass.isEmpty()) {
            throw new RuntimeException("Main class not found in JNLP");
        }

        List<Path> downloadedJars = new ArrayList<>();
        for (String jarUrl : jarUrls) {
            Path jarPath = downloadJar(jarUrl);
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

    private static Path downloadJar(String jarUrl) throws IOException {
        // Create a URL object from the JAR URL string
        URL url = new URL(jarUrl);

        // Open a connection to the URL
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Java JNLP Launcher");
        conn.connect();

        // Create a temporary file in the cache directory to save the JAR
        Path tempJar = Files.createTempFile(Paths.get(CACHE_DIR), "downloaded-", ".jar");

        // Download the file
        try (InputStream in = conn.getInputStream();
             OutputStream out = Files.newOutputStream(tempJar)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }

        // Return the path of the downloaded JAR file
        System.out.println("Downloaded JAR to: " + tempJar.toString());
        return tempJar;
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
