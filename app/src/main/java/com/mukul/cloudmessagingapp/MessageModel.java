package com.mukul.cloudmessagingapp;

public class MessageModel {

    String message, picURL;
    boolean sentByOwn;
    long sendTime = System.currentTimeMillis();

    public MessageModel() {

    }

    public MessageModel(String message, String picURL, boolean sentByOwn, long sendTime) {
        this.message = message;
        this.sendTime = sendTime;
        this.sentByOwn = sentByOwn;
        this.picURL = picURL;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isSentByOwn() {
        return sentByOwn;
    }

    public void setSentByOwn(boolean sentByOwn) {
        this.sentByOwn = sentByOwn;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }
}