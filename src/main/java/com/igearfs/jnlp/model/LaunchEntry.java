package com.igearfs.jnlp.model;

// Simple LaunchEntry class to hold name, URL, note, ID, and ignoreDomainValidation flag
public class LaunchEntry {
    private String id;
    private String name;
    private String url;
    private String note;
    private boolean ignoreDomainValidation; // New field

    // Constructor with ignoreDomainValidation
    public LaunchEntry(String name, String url, String note, String id, boolean ignoreDomainValidation) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.note = note;
        this.ignoreDomainValidation = ignoreDomainValidation; // Store checkbox value
    }

    // Constructor for older data (defaults ignoreDomainValidation to true)
    public LaunchEntry(String name, String url, String note, String id) {
        this(name, url, note, id, true);
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

    @Override
    public String toString() {
        return "LaunchEntry{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", note='" + note + '\'' +
                ", ignoreDomainValidation=" + ignoreDomainValidation +
                '}';
    }
}
