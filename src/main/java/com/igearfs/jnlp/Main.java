/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp;

/**
 *
 * Class for setup and launching the JNLP Launcher.
 *
 */
public class Main {
    // Static block to set up logging and system properties before main() runs
    static {
        String os = System.getProperty("os.name").toLowerCase();
        String logPath;
        String dataDir;

        // Set log directory path based on OS
        if (os.contains("win")) {
            String appData = System.getProperty("user.home"); // Points to AppData\Roaming
            logPath = appData + "/AppData/Roaming/SyncSyndicate/logs";
        } else {
            String userHome = System.getProperty("user.home");
            logPath = userHome + "/.config/SyncSyndicate/logs"; // Or ~/.SyncSyndicate/data
        }

        // Set data directory path based on OS
        if (os.contains("win")) {
            String appData = System.getProperty("user.home"); // Points to AppData\Roaming
            dataDir = appData + "/AppData/Roaming/SyncSyndicate/data";
        } else {
            String userHome = System.getProperty("user.home");
            dataDir = userHome + "/.config/SyncSyndicate/data"; // Or ~/.SyncSyndicate/data
        }

        // Set system properties before logging setup
        System.setProperty("os.logdir", logPath);
        System.setProperty("os.dataDir", dataDir);

    }
    public static void main(String[] args) {
        JnlpLauncherApp.main(args);
    }
}