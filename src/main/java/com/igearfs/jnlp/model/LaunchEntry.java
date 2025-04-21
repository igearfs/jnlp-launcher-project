/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp.model;

import com.igearfs.jnlp.security.CredentialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the Right Hand Pane data and data saved off to the file.
 * Maybe in the future we use a database but for now a simple file will do.
 */
// Simple LaunchEntry class to hold name, URL, note, ID, ignoreDomainValidation, and iconPath
public class LaunchEntry
{
    private static final Logger logger = LoggerFactory.getLogger(LaunchEntry.class);
    private String id;
    private String name;
    private String url;
    private String note;
    private boolean ignoreDomainValidation; // New field
    private String iconPath; // New field for the icon path
    private String userName;
    private String password;


    // Constructor with ignoreDomainValidation and iconPath
    public LaunchEntry(String name, String url, String note, String id, boolean ignoreDomainValidation, String iconPath,
                       String userName, String password)
    {
        this.id = id;
        this.name = name;
        this.url = url;
        this.note = note;
        this.ignoreDomainValidation = ignoreDomainValidation; // Store checkbox value
        this.iconPath = iconPath; // Store icon path
        if (userName != null && !userName.isEmpty() && password != null && !password.isEmpty())
        {
            CredentialManager.storeCredential(id, userName, password);
        }
    }

    // Constructor for older data (defaults ignoreDomainValidation to true and iconPath to "/rocket.png")
    public LaunchEntry(String name, String url, String note, String id)
    {
        this(name, url, note, id, true, "/icons/rocket.png", null, null);
    }

    // Getter and Setter methods
    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public boolean isIgnoreDomainValidation()
    {
        return ignoreDomainValidation;
    }

    public void setIgnoreDomainValidation(boolean ignoreDomainValidation)
    {
        this.ignoreDomainValidation = ignoreDomainValidation;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getIconPath()
    {
        return iconPath;
    }

    public void setIconPath(String iconPath)
    {
        this.iconPath = iconPath;
    }

    @Override
    public String toString()
    {
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
