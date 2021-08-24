package com.mukul.cloudmessagingapp;

public class User {

    String uid, userName, userEmail, userPhoneNum;

    public User() {

    }

    public User(String uid, String userName, String userEmail, String userPhoneNum) {
        this.uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }
}
