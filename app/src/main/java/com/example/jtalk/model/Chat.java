package com.example.jtalk.model;

public class Chat extends User{
    public String lastMessages;

    public Chat() {
    }

    public Chat(String email, String username, String password, String avatar, boolean online, String lastMessages) {
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
