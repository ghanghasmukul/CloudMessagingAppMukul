package com.mukul.cloudmessagingapp;

import java.util.Map;

public class AllMessages {

        Map<String, MessageModel> mMessageModelMap;

        public AllMessages() {}

        public AllMessages(Map<String, MessageModel> messageModelMap) {
            mMessageModelMap = messageModelMap;
        }

        public Map<String, MessageModel> getMessageModelMap() {
            return mMessageModelMap;
        }

        public void setMessageModelMap(Map<String, MessageModel> messageModelMap) {
            mMessageModelMap = messageModelMap;
        }
    }

