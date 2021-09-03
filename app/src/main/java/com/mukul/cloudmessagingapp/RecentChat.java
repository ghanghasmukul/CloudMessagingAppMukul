package com.mukul.cloudmessagingapp;

public class RecentChat {
    String chatName, chatPhoneNum , chatUID, chatMessage="";
    long chatTime = 0;

    public RecentChat() {

    }

    public RecentChat(String chatName, String chatPhoneNum, String chatUID, String chatMessage, long chatTime) {
        this.chatName = chatName;
        this.chatPhoneNum = chatPhoneNum;
        this.chatUID = chatUID;
        this.chatMessage = chatMessage;
        this.chatTime = chatTime;
    }

    public String getChatPhoneNum() {
        return chatPhoneNum;
    }

    public void setChatPhoneNum(String chatPhoneNum) {
        this.chatPhoneNum = chatPhoneNum;
    }

    public String getChatUID() {
        return chatUID;
    }

    public void setChatUID(String chatUID) {
        this.chatUID = chatUID;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public long getChatTime() {
        return chatTime;
    }

    public void setChatTime(long chatTime) {
        this.chatTime = chatTime;
    }
}
