package com.example.jtalk.model;



public class User {
    public String email  ,  username  , password  , avatar;
    public boolean online;

    public User(){

    }

    public User(String email, String username, String password, String avatar, boolean online) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.online = online;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
