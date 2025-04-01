/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

package com.igearfs.jnlp.model;

// Simple LaunchEntry class to hold name, URL, note, ID, ignoreDomainValidation, and iconPath
public class LaunchEntry {
    private String id;
    private String name;
    private String url;
    private String note;
    private boolean ignoreDomainValidation; // New field
    private String iconPath; // New field for the icon path

    // Constructor with ignoreDomainValidation and iconPath
    public LaunchEntry(String name, String url, String note, String id, boolean ignoreDomainValidation, String iconPath) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.note = note;
        this.ignoreDomainValidation = ignoreDomainValidation; // Store checkbox value
        this.iconPath = iconPath; // Store icon path
    }

    // Constructor for older data (defaults ignoreDomainValidation to true and iconPath to "/rocket.png")
    public LaunchEntry(String name, String url, String note, String id) {
        this(name, url, note, id, true, "/icons/rocket.png");
    }

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isIgnoreDomainValidation() {
        return ignoreDomainValidation;
    }

    public void setIgnoreDomainValidation(boolean ignoreDomainValidation) {
        this.ignoreDomainValidation = ignoreDomainValidation;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public String toString() {
        return "LaunchEntry{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", note='" + note + '\'' +
                ", ignoreDomainValidation=" + ignoreDomainValidation +
                ", iconPath='" + iconPath + '\'' +
                '}';
    }
}
