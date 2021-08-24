package com.mukul.cloudmessagingapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import androidx.annotation.NonNull;

    public class FCMServices extends FirebaseMessagingService {
        @Override
        public void onNewToken(@NonNull String s) {
            super.onNewToken(s);
            Log.d("FCMToken", s);
        }
    }

