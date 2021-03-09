package com.example.jtalk.model;

public class Message {
    public String receiver , sender , message;
    public boolean isSender;
    public boolean isImage;
    public boolean isRead;


    public Message(){

    }


    public Message(String sender, String receiver, String message , boolean isSender) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
        this.isSender = isSender;
    }
    public Message(String sender, String receiver, String message , boolean isSender, boolean isImage) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
        this.isSender = isSender;
        this.isImage = isImage;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
