package com.igearfs.jnlp.model;

import java.util.Objects;

public class JnlpItem {
    private String name;
    private String url;
    private String iconPath;

    // Default constructor
    public JnlpItem() {
    }

    // Parameterized constructor
    public JnlpItem(String name, String url, String iconPath) {
        this.name = name;
        this.url = url;
        this.iconPath = iconPath;
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

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public String toString() {
        return "JnlpItem{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", iconPath='" + iconPath + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, iconPath);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        JnlpItem jnlpItem = (JnlpItem) obj;
        return Objects.equals(name, jnlpItem.name) &&
                Objects.equals(url, jnlpItem.url) &&
                Objects.equals(iconPath, jnlpItem.iconPath);
    }
}
