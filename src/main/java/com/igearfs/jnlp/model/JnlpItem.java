/*
 * Copyright (c) 2025. All rights reserved.
 * This software is protected under the intellectual property laws of the United States and international copyright treaties.
 * Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
 * By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
 * Any use of the software outside the bounds of this agreement is subject to legal action.
 *
 */

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
