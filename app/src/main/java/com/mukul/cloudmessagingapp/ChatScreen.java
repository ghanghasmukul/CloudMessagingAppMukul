package com.mukul.cloudmessagingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


    public class ChatScreen extends AppCompatActivity {

        private EditText messagesET;
        private ImageView sendMessageBtn, pickPhotoIV;
        private RecyclerView messagesRV;

        private static final String ARG_OTHER_USER_ID = "ARG_OTHER_USER_ID",
                ARG_OTHER_USER_NAME = "ARG_OTHER_USER_NAME",
                ARG_OTHER_USER_PHONE_NUM = "ARG_OTHER_USER_PHONE_NUM";

        String otherUserId, otherUserName;

        ImageView backButton;
        TextView chatContactName;

        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference messagesDBRef, othersMessageDBRef;

        private StorageReference mChatPhotosStorageReference;

        private FirebaseAuth mFirebaseAuth;
        private FirebaseUser user;

        private final static int REQUEST_CODE_SIGN_IN = 111, REQUEST_CODE_PICK_IMAGE = 1;
        private boolean sendEnabled = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_screen);

            backButton = findViewById(R.id.backIV);
            chatContactName = findViewById(R.id.chatContactName);

            otherUserId = getIntent().getStringExtra(ARG_OTHER_USER_ID);
            otherUserName = getIntent().getStringExtra(ARG_OTHER_USER_NAME);

            backButton.setOnClickListener(v -> onBackPressed());

            chatContactName.setText(otherUserName);

            mFirebaseAuth = FirebaseAuth.getInstance();

            user = mFirebaseAuth.getCurrentUser();

            mFirebaseDatabase = FirebaseDatabase.getInstance();

            messagesDBRef = mFirebaseDatabase.getReference().child("messages/" + user.getUid() + "/" + otherUserId);
            othersMessageDBRef = mFirebaseDatabase.getReference().child("messages/" + otherUserId + "/" + user.getUid());

            mChatPhotosStorageReference = FirebaseStorage.getInstance().getReference().child("chat_photos");

            pickPhotoIV = findViewById(R.id.sendImage);
            messagesET = findViewById(R.id.messageET);
            sendMessageBtn = findViewById(R.id.sendMessage);
            messagesRV = findViewById(R.id.messagesRV);

            initViews();
        }

        private void initViews() {
            messagesET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().isEmpty())
                        sendEnabled = false;
                    else
                        sendEnabled = true;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            sendMessageBtn.setOnClickListener(v -> {
                if (sendEnabled) {
                    messagesDBRef.push().setValue(
                            new MessageModel(
                                    messagesET.getText().toString(),
                                    null,
                                    true,
                                    System.currentTimeMillis()
                            ));
                    othersMessageDBRef.push().setValue(
                            new MessageModel(
                                    messagesET.getText().toString(),
                                    null,
                                    false,
                                    System.currentTimeMillis()
                            )
                    );
                    sendEnabled = false;
                    messagesET.setText("");
                } else {
                    Toast.makeText(ChatScreen.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            });

            pickPhotoIV.setOnClickListener(v -> {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                startActivityForResult(
                        Intent.createChooser(intent, "Complete Action Using"),
                        REQUEST_CODE_PICK_IMAGE);
            });

            setUpRV();
        }

        private void setUpRV() {
            Query query = messagesDBRef;

            FirebaseRecyclerOptions<MessageModel> options =
                    new FirebaseRecyclerOptions.Builder<MessageModel>()
                            .setQuery(query, MessageModel.class)
                            .build();

            FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<MessageModel, MessageViewHolder>(options) {
                @Override
                public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message_rv, parent, false);

                    return new MessageViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(MessageViewHolder holder, int position, MessageModel message) {
                    holder.bind(message);
                }

                @Override
                public void onDataChanged() {
                    super.onDataChanged();
                    if (options.getSnapshots().size() > 5)
                        messagesRV.smoothScrollToPosition(options.getSnapshots().size() - 1);
                }
            };

            adapter.startListening();

            messagesRV.setAdapter(adapter);

            messagesRV.setLayoutManager(new LinearLayoutManager(ChatScreen.this));

        }

        public static Intent getStartIntent(Context context, String otherUserId, String otherUserName, String otherUserPhoneNum) {
            Intent intent = new Intent(context, ChatScreen.class);

            intent.putExtra(ARG_OTHER_USER_ID, otherUserId);
            intent.putExtra(ARG_OTHER_USER_NAME, otherUserName);
            intent.putExtra(ARG_OTHER_USER_PHONE_NUM, otherUserPhoneNum);
            return intent;
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {

                Uri selectedImageUri = data.getData();
                //gallery/photos/image1.jpeg
                //file name = image1.jpeg
                final StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

                photoRef.putFile(selectedImageUri)
                        .addOnSuccessListener(this, taskSnapshot -> {
                            photoRef.getDownloadUrl().addOnSuccessListener(downloadURL -> {
                                messagesDBRef.push().setValue(
                                        new MessageModel(
                                                null,
                                                downloadURL.toString(),
                                                true,
                                                System.currentTimeMillis())
                                );
                                othersMessageDBRef.push().setValue(
                                        new MessageModel(
                                                null,
                                                downloadURL.toString(),
                                                false,
                                                System.currentTimeMillis())
                                );

                                Toast.makeText(ChatScreen.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                            });
                        });
            }
        }

    }