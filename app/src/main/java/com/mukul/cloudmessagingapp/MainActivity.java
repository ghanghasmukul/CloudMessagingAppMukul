package com.mukul.cloudmessagingapp;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private EditText messagesET;
    private ImageView sendMessageBtn;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference messagesDBRef, usersDBRef;

    private StorageReference mChatPhotosStorageReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private final static int REQUEST_CODE_SIGN_IN = 111, REQUEST_CODE_PICK_IMAGE = 1;
    private boolean sendEnabled = false;

    ImageView pickPhotoIV;

    RecyclerView messagesRV;

    FirebaseUser user;

    String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickPhotoIV = findViewById(R.id.sendImage);

        mChatPhotosStorageReference = FirebaseStorage.getInstance().getReference().child("chat_photos");

        messagesET = findViewById(R.id.messageET);
        sendMessageBtn = findViewById(R.id.sendMessage);
        messagesRV = findViewById(R.id.messagesRV);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersDBRef = mFirebaseDatabase.getReference().child("users");
        messagesDBRef = mFirebaseDatabase.getReference().child("messages");

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user == null) {
                //Re-direct to Login
                Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_LONG).show();

                List<String> whiteListedCountries = new ArrayList<>();
                whiteListedCountries.add("IN");
                whiteListedCountries.add("US");

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setLogo(R.drawable.ic_baseline_chat_24)
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                                new AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("IN").setAllowedCountries(whiteListedCountries).build(),
                                                new AuthUI.IdpConfig.EmailBuilder().build())
                                ).build(), REQUEST_CODE_SIGN_IN
                );

            } else {
                //User Logged In
                Toast.makeText(MainActivity.this, "User Phone - " + user.getPhoneNumber(), Toast.LENGTH_LONG).show();

                this.user = user;

                initViews();

                if (user.getDisplayName() == null || user.getDisplayName().trim().isEmpty()) {
                    askForUserName();
                } else {
                    saveUserDetails(user.getDisplayName());
                    userName = user.getDisplayName();
                }
            }

        };

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void askForUserName() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        builder.setTitle("Enter your name");
        builder.setCancelable(false);

        final EditText nameET = new EditText(this);
        nameET.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setPositiveButton("SAVE", (dialog, which) -> {
            String name = nameET.getText().toString();
            userName = name;
            saveUserDetails(name);
        });

        builder.setView(nameET);

        builder.show();
    }

    private void saveUserDetails(String name) {
        User object = new User(user.getUid(), name, user.getEmail(), user.getPhoneNumber());

        usersDBRef.child(user.getUid()).setValue(object);
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
                                user.getUid(),
                                userName,
                                null,
                                user.getPhoneNumber(),
                                user.getEmail(),
                                System.currentTimeMillis())
                );
                sendEnabled = false;
                messagesET.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });

        pickPhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                startActivityForResult(
                        Intent.createChooser(intent, "Complete Action Using"),
                        REQUEST_CODE_PICK_IMAGE);
            }
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

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_rv, parent, false);

                return new MessageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(MessageViewHolder holder, int position, MessageModel message) {

                holder.bind(message, user.getUid().equals(message.senderID));

            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                messagesRV.smoothScrollToPosition(options.getSnapshots().size() - 1);
            }
        };

        adapter.startListening();

        messagesRV.setAdapter(adapter);

        messagesRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            Uri selectedImageUri = data.getData();

            final StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, taskSnapshot -> {
                        photoRef.getDownloadUrl().addOnSuccessListener(downloadURL -> {
                            messagesDBRef.push().setValue(
                                    new MessageModel(
                                            null,
                                            user.getUid(),
                                            userName,
                                            downloadURL.toString(),
                                            user.getPhoneNumber(),
                                            user.getEmail(),
                                            System.currentTimeMillis())
                            );

                            Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        });
                    });
        }
    }
}



