package com.igearfs.jnlp.model;

public class JnlpItem {
    private String name;
    private String url;
    private String iconPath;

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
}
