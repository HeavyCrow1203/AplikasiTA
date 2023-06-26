package com.example.aplikasita.database;

public class userApps {
    String username, email;

    public userApps(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public userApps() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
