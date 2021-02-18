package com.example.jtalk.model;

public class Chatter extends User{
   public  String lastMessage;

   public  Chatter(){

   }

    public Chatter(String email, String username, String password, String avatar, boolean online, String lastMessage) {
        super(email, username, password, avatar, online);
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
