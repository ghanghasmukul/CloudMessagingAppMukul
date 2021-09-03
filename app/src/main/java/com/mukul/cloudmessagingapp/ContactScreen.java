package com.mukul.cloudmessagingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

 public class ContactScreen extends AppCompatActivity implements OnUserClick {

        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference usersDBRef;

        RecyclerView contactsRV;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contact_screen);

            contactsRV = findViewById(R.id.contactsRV);

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            usersDBRef = mFirebaseDatabase.getReference().child("users");

            setUpContactsRV();
        }

        private void setUpContactsRV() {
            Query query = usersDBRef;

            FirebaseRecyclerOptions<User> options =
                    new FirebaseRecyclerOptions.Builder<User>()
                            .setQuery(query, User.class)
                            .build();

            FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, ContactVH>(options) {
                @Override
                public ContactVH onCreateViewHolder(ViewGroup parent, int viewType) {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_contact_rv, parent, false);

                    return new ContactVH(view);
                }

                @Override
                protected void onBindViewHolder(ContactVH holder, int position, User user) {
                    holder.bind(user, ContactScreen.this);
                }
            };

            adapter.startListening();

            contactsRV.setAdapter(adapter);

            contactsRV.setLayoutManager(new LinearLayoutManager(ContactScreen.this));
        }

        @Override
        public void onContactClick(User user) {
            String name = "";

            if (user.userName == null || user.userName.trim().isEmpty()) {
                name = "Anonymous";
            } else {
                name = user.userName;
            }

            startActivity(ChatScreen.getStartIntent(this, user.uid, name, user.getUserPhoneNum()));
            finish();
        }
}