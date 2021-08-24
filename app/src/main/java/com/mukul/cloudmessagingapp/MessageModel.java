package com.mukul.cloudmessagingapp;

public class MessageModel {

    String message, senderID, senderName, picURL, senderPhoneNum, senderEmail;
    long sendTime = System.currentTimeMillis();

    public MessageModel() {

    }

    public MessageModel(String message, String senderID, String senderName, String picURL, String senderPhoneNum, String senderEmail, long sendTime) {
        this.message = message;
        this.senderID = senderID;
        this.senderName = senderName;
        this.picURL = picURL;
        this.senderPhoneNum = senderPhoneNum;
        this.senderEmail = senderEmail;
        this.sendTime = sendTime;
    }

    public String getSenderPhoneNum() {
        return senderPhoneNum;
    }

    public void setSenderPhoneNum(String senderPhoneNum) {
        this.senderPhoneNum = senderPhoneNum;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
}
