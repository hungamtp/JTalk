package com.example.jtalk.model;



public class User {
    public String email  ,  username  , password ;
    public boolean online;
    public boolean avatar;

    public User(){

    }

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String email, String username, String password, boolean online, boolean avatar) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.online = online;
        this.avatar = avatar;
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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean hasAvatar() {
        return avatar;
    }

    public void setAvatar(boolean avatar) {
        this.avatar = avatar;
    }
}
