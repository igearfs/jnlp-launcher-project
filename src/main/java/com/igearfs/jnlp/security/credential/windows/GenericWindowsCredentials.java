/*
 * Copyright (c) 2025 In-Game Event, A Red Flag Syndicate LLC.
 * All rights reserved.
 *
 */

package com.igearfs.jnlp.security.credential.windows;

public class GenericWindowsCredentials {
    private String key;
    private String username;
    private String password;

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "address:[" + getKey() + "], username:[" + getUsername() + "], password:[" + getPassword() + "]";
    }


}
