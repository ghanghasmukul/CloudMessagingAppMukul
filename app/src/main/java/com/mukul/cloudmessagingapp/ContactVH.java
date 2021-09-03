package com.mukul.cloudmessagingapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class ContactVH extends RecyclerView.ViewHolder {
    private TextView contactName, contactPhoneNum;

    public ContactVH(@NonNull @NotNull View itemView) {
        super(itemView);

        contactName = itemView.findViewById(R.id.contactName);
        contactPhoneNum = itemView.findViewById(R.id.contactPhoneNum);


    }

    void bind(User user, OnUserClick onUserClick) {

        String name = "";

        if (user.userName == null || user.userName.trim().isEmpty()) {
            name = "Anonymous";
        } else {
            name = user.userName;
        }
        contactName.setText(name);

        contactPhoneNum.setText(user.userPhoneNum);

        itemView.setOnClickListener(v -> onUserClick.onContactClick(user));

    }

}
