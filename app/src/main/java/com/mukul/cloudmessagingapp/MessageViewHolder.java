package com.mukul.cloudmessagingapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;



import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * public class ViewHolder extends Recycler.ViewHolder
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView othersMessageTV, othersSenderDetailsTV, othersSendTimeTV, ownMessageTV, ownSendTimeTV;
    ImageView othersMessageImageIV, ownMessageImageIV;
    LinearLayout othersMessageLayout, ownMessageLayout;

    public MessageViewHolder(@NonNull  View itemView) {
        super(itemView);

        othersMessageTV = itemView.findViewById(R.id.othersMessageTV);
        othersSenderDetailsTV = itemView.findViewById(R.id.othersSenderDetails);
        othersMessageImageIV = itemView.findViewById(R.id.othersMessageImageIV);
        othersSendTimeTV = itemView.findViewById(R.id.othersMessageSendTime);

        ownMessageTV = itemView.findViewById(R.id.ownMessageTV);
        ownMessageImageIV = itemView.findViewById(R.id.ownMessageImageIV);
        ownSendTimeTV = itemView.findViewById(R.id.ownMessageSendTime);

        othersMessageLayout = itemView.findViewById(R.id.othersMessageLayout);
        ownMessageLayout = itemView.findViewById(R.id.ownMessageLayout);
    }

    public void bind(MessageModel message, boolean isItTheSameUser) {

        if (isItTheSameUser) {
            othersMessageLayout.setVisibility(View.GONE);
            ownMessageLayout.setVisibility(View.VISIBLE);

            if (message.message != null && !message.message.isEmpty()) {
                ownMessageImageIV.setVisibility(View.GONE);
                ownMessageTV.setVisibility(View.VISIBLE);
                ownMessageTV.setText(message.message);
            } else {
                ownMessageTV.setVisibility(View.GONE);
                ownMessageImageIV.setVisibility(View.VISIBLE);
                Glide.with(ownMessageImageIV).load(message.picURL).into(ownMessageImageIV);
            }

            ownSendTimeTV.setText(new SimpleDateFormat("MMM,dd  hh:mm aaa").format(message.sendTime));

        } else {
            ownMessageLayout.setVisibility(View.GONE);
            othersMessageLayout.setVisibility(View.VISIBLE);

            if (message.message != null && !message.message.isEmpty()) {
                othersMessageImageIV.setVisibility(View.GONE);
                othersMessageTV.setVisibility(View.VISIBLE);
                othersMessageTV.setText(message.message);
            } else {
                othersMessageTV.setVisibility(View.GONE);
                othersMessageImageIV.setVisibility(View.VISIBLE);
                Glide.with(othersMessageImageIV).load(message.picURL).into(othersMessageImageIV);
            }

            String senderDetails = "";

            if (isItTheSameUser) {
                senderDetails = "You";
            } else if (message.senderName != null && !message.senderName.isEmpty()) {
                senderDetails = message.senderName;
            } else if (message.senderPhoneNum != null && !message.senderPhoneNum.isEmpty()) {
                senderDetails = message.senderPhoneNum;
            } else if (message.senderEmail != null && !message.senderEmail.isEmpty()) {
                senderDetails = message.senderEmail;
            }

            othersSenderDetailsTV.setText(senderDetails);
            othersSendTimeTV.setText(new SimpleDateFormat("d MMM, yyyy").format(message.sendTime));

        }

    }

}
