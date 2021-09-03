package com.mukul.cloudmessagingapp;

public class User {

    String uid, userName, userPhoneNum;

    public User() {

    }

    public User(String uid, String userName, String userPhoneNum) {
        this.uid = uid;
        this.userName = userName;
        this.userPhoneNum = userPhoneNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }
}
