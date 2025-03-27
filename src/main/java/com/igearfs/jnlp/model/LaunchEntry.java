package com.igearfs.jnlp.model;

// Simple LaunchEntry class to hold name, URL, note, and ID
public class LaunchEntry {
    private String id;
    private String name;
    private String url;
    private String note;

    public LaunchEntry(String name, String url, String note, String id) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.note = note;
    }

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
}
