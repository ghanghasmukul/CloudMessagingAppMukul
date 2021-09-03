package com.mukul.cloudmessagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HomeScreen extends AppCompatActivity implements RecentChatAdapter.GoToChatScreen {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersDBRef, ownMessageDBRef;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private final static int REQUEST_CODE_SIGN_IN = 111;
    String userName = "";

    private final String TAG = HomeScreen.class.getSimpleName();

    FloatingActionButton contactsFAB;

    RecyclerView recentChatsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Forcing Light Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_home_screen);

        recentChatsRV = findViewById(R.id.recentChatsRV);

        contactsFAB = findViewById(R.id.contactsFAB);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersDBRef = mFirebaseDatabase.getReference().child("users");

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user == null) {
                //Re-direct to Login
                Toast.makeText(HomeScreen.this, "Please Login", Toast.LENGTH_LONG).show();

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
                                                new AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("IN").setAllowedCountries(whiteListedCountries).build())
                                ).build(), REQUEST_CODE_SIGN_IN
                );

            } else {

                this.user = user;

                ownMessageDBRef = mFirebaseDatabase.getReference().child("messages/" + user.getUid());

                initViews();

                DatabaseReference userAccountDB = usersDBRef.child(user.getUid());

                userAccountDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User currentUser = snapshot.getValue(User.class);
                            if (!(currentUser.getUserName() != null || !currentUser.getUserName().isEmpty())) {
                                askForUserName();
                            }
                        } else {
                            askForUserName();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(HomeScreen.this, "Database Error occured", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        };

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        contactsFAB.setOnClickListener(v -> startActivity(new Intent(HomeScreen.this, ContactScreen.class)));
    }

    private void askForUserName() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        builder.setTitle("Enter your name");
        builder.setCancelable(false);

        final EditText nameET = new EditText(this);
        nameET.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setPositiveButton("SAVE", (dialog, which) -> {
            String name = nameET.getText().toString();
            name = name.trim();

            if (name.length() < 2) {
                askForUserName();
            } else {
                userName = name;
                saveUserDetails(name);
            }
        });

        builder.setView(nameET);

        builder.show();
    }

    private void saveUserDetails(String name) {
        User object = new User(user.getUid(), name, user.getPhoneNumber());

        usersDBRef.child(user.getUid()).setValue(object);
    }

    private void initViews() {
        Log.e(TAG, ownMessageDBRef.toString());

        ownMessageDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                getRecentMessages((Map<String, Map<String, MessageModel>>) snapshot.getValue());

                /**
                 * Map Key = 81l8xzS4FCPtL8tnedJn9ugW3zR2
                 * Map Value = Map<Mi7KwwjIq5EhP-kQOOe, MessageModel>.....12 such maps
                 */
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    //List<List<String>>

    //2 size = 2 * List<String>

    private void getRecentMessages(Map<String, Map<String, MessageModel>> allMessagesMap) {

        if (allMessagesMap != null && !allMessagesMap.isEmpty() &&  !allMessagesMap.entrySet().isEmpty()) {

        List<RecentChat> listOfRecentChats = new ArrayList<>();

        for (Map.Entry<String, Map<String, MessageModel>> entry : allMessagesMap.entrySet()) {

            RecentChat recentChat = new RecentChat();
            recentChat.chatUID = entry.getKey();

            for (Map.Entry<String, MessageModel> entry1 : entry.getValue().entrySet()) {

                MessageModel messageModel = new ObjectMapper().convertValue(entry1.getValue(), MessageModel.class);

                if (messageModel.sendTime > recentChat.chatTime) {
                    recentChat.chatTime = messageModel.sendTime;

                    if (messageModel.picURL == null || messageModel.picURL.isEmpty()) {
                        recentChat.chatMessage = messageModel.message;
                    } else {
                        recentChat.chatMessage = "<Image>";
                    }
                }
            }

            listOfRecentChats.add(recentChat);
        }

        getUserDetailsAndAddToRecentChat(listOfRecentChats); }
    }

    private void getUserDetailsAndAddToRecentChat(List<RecentChat> listOfRecentChats) {
        for (int i=0; i<listOfRecentChats.size(); i++) {

            int finalI = i;
            usersDBRef
                    .child(listOfRecentChats.get(finalI).chatUID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            User currentUser = snapshot.getValue(User.class);
                            assert currentUser != null;
                            listOfRecentChats.get(finalI).chatName = currentUser.userName;
                            listOfRecentChats.get(finalI).chatPhoneNum = currentUser.userPhoneNum;

                            if (finalI == listOfRecentChats.size()-1) {
                                displayRecentChats(listOfRecentChats);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        }
    }

    private void displayRecentChats(List<RecentChat> listOfRecentChats) {
        Collections.sort(listOfRecentChats, (chat1, chat2) -> (int) (chat2.chatTime - chat1.chatTime));
        recentChatsRV.setLayoutManager(new LinearLayoutManager(this));
        recentChatsRV.setAdapter(new RecentChatAdapter(listOfRecentChats, this));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(HomeScreen.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void openChatScreen(RecentChat recentChat) {
        String name = "";

        if (recentChat.chatName == null || recentChat.chatName.trim().isEmpty()) {
            name = "Anonymous";
        } else {
            name = recentChat.chatName;
        }

        startActivity(ChatScreen.getStartIntent(this, recentChat.chatUID, name, recentChat.chatPhoneNum));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}