package com.example.jtalk.model;

public class Chatter extends User{
    String lastMessages;

    public Chatter() {
    }

    public Chatter(String email, String username, String password, String avatar, boolean online, String lastMessages) {
        super(email, username, password, avatar, online);
        this.lastMessages = lastMessages;
    }

    public String getLastMessages() {

        return lastMessages;
    }

    public void setLastMessages(String lastMessages) {
        this.lastMessages = lastMessages;
    }
}
