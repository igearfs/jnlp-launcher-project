/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp.util;

import org.slf4j.Logger;

import java.net.MalformedURLException;

public class LogManager {

    // Private constructor to prevent instantiation
    private LogManager() {}

    /**
     * General info log
     * @param logger the logger from the calling class
     * @param message the message to log
     */
    public static void logInfo(Logger logger, String message) {
        if (logger != null) {
            logger.info(message);
        }
    }

    /**
     * Log a warning
     * @param logger the logger from the calling class
     * @param message the message to log
     */
    public static void logWarning(Logger logger, String message) {
        if (logger != null) {
            logger.warn(message);
        }
    }

    /**
     * Log an error with full stack trace
     * @param logger the logger from the calling class
     * @param message the message to log
     * @param throwable the exception or error that occurred
     */
    public static void logError(Logger logger, String message, Throwable throwable) {
        if (logger != null) {
            // This will print the full stack trace, which is critical for tracing the origin of the error
            logger.error(message, throwable);
        }
    }

    /**
     * Log a debug message
     * @param logger the logger from the calling class
     * @param message the message to log
     */
    public static void logDebug(Logger logger, String message) {
        if (logger != null) {
            logger.debug(message);
        }
    }
    
}
