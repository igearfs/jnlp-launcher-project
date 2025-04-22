/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JarVerifier
{

    // Run the jarsigner verify command as an external process
    public static boolean verifyJarSignature(Path jarPath)
    {
        // Construct the command
        String jarsignerCommand = "jarsigner";
        String verifyOption = "-verify";
        String verboseOption = "-verbose";  // Optional: Shows more details of the verification
        String jarFile = jarPath.toString();  // Path to the jar file

        // Build the process
        ProcessBuilder processBuilder = new ProcessBuilder(jarsignerCommand, verifyOption, verboseOption, jarFile);
        processBuilder.redirectErrorStream(true);  // Combine stdout and stderr for easier handling

        try
        {
            // Start the process
            Process process = processBuilder.start();

            // Capture the output from the process (testing purposes)
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);  // Print the output from jarsigner
//                }
//            }

            // Wait for the process to complete and check the exit value
            int exitCode = process.waitFor();
            if (exitCode == 0)
            {
                System.out.println("JAR verification successful.");
                return true;  // Verification successful
            }
            else
            {
                System.out.println("JAR verification failed.");
                return false;  // Verification failed
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            return false;  // If an error occurs while running the process
        }
    }

    // Method to verify all JARs in a given directory
    public static void verifyAllJarsInDirectory(Path directory)
    {
        if (!Files.isDirectory(directory))
        {
            System.err.println("Invalid directory: " + directory);
            return;
        }

        try
        {
            Files.list(directory)
                    .filter(path -> path.toString().toLowerCase().endsWith(".jar"))
                    .forEach(jarPath ->
                    {
                        System.out.println("Verifying: " + jarPath.getFileName());
                        boolean isValid = verifyJarSignature(jarPath);
                        System.out.println(jarPath.getFileName() + " is " + (isValid ? "VALID ✅" : "INVALID ❌"));
                    });
        }
        catch (IOException e)
        {
            System.err.println("Failed to list directory: " + directory);
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Path jarDirectory = Paths.get(System.getProperty("user.home") +
                File.separator +
                "AppData" + File.separator +
                "Roaming" + File.separator +
                "SyncSyndicate" + File.separator +
                "jnlp_cache" + File.separator +
                "MC 4.5.2 d" + File.separator +
                "localhost");
        verifyAllJarsInDirectory(jarDirectory);
    }
}