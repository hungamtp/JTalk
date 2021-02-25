package com.example.jtalk.model;

public class Chat extends User{
    public String lastMessages;

    public Chat() {
    }

    public Chat( String username, String avatar, boolean online, String lastMessages) {
        super(username, avatar, online);
        this.lastMessages = lastMessages;
    }

    public String getLastMessages() {

        return lastMessages;
    }

    public void setLastMessages(String lastMessages) {
        this.lastMessages = lastMessages;
    }
}
