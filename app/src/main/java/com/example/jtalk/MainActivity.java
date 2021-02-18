package com.example.jtalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jtalk.adapter.ChatListAdapter;
import com.example.jtalk.model.Chat;
import com.example.jtalk.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView chatListView;
    ArrayList<Chat> chatList;
    List<User> friendList;
    ChatListAdapter chatListAdapter;
    DatabaseReference databaseReference;
    EditText nameSearch;
    Button btnSearch;
    Dialog searchFriendDialog;
    String username;
    ImageView avatar;
    StorageReference storageReference;
    Intent newIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        loadAvatar();
        // fecth chat data to recycle view
        databaseReference.child("Users").child(username).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String friendName = snapshot.getValue(String.class);
                databaseReference.child("Users").child(username).child("Messages").child(friendName).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                     if(snapshot.exists())
                        Chat newChat = snapshot.getValue(Chat.class);
                        chatList.add(newChat);

                        databaseReference.child("Users").child(username).child("Messages").child(newChat.username).limitToLast(1).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                String lastMessage = snapshot.getValue(String.class);
                                newChat.lastMessage = lastMessage;
                                chatListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                databaseReference.child("Users").child(friendName).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Chat chat = snapshot.getValue(Chat.class);
//                        if (chatListAdapter.checkUser(chat.username)) {
//                            chatList.get(chatListAdapter.getPositionById(chat.username)).online = chat.online;
//                        } else {
//                            chatList.add(chat);
//                        }
//                        chatListAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void showPopup(View v, User friend) {
        ImageView avatar;
        TextView friendName;
        TextView btnClose;
        Button btnAddFriend;
        Button btnText;
        if (!isFriend(nameSearch.getText().toString())) {
            searchFriendDialog.setContentView(R.layout.popup_search_friend);
            btnAddFriend = searchFriendDialog.findViewById(R.id.btnAddfriend);
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchFriendDialog.dismiss();
                    addFriend(username, nameSearch.getText().toString());
                }
            });
        } else searchFriendDialog.setContentView(R.layout.popup_search_friend_been_friend);

        friendName = searchFriendDialog.findViewById(R.id.name);
        avatar = searchFriendDialog.findViewById(R.id.avatar);
        btnText = searchFriendDialog.findViewById(R.id.btnText);
        btnClose = searchFriendDialog.findViewById(R.id.btnClose);
        Glide.with(this).load(friend.avatar).into(avatar);
        friendName.setText(friend.username);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriendDialog.dismiss();
            }
        });

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newIntent = new Intent();
                newIntent.setClass(MainActivity.this, ChatActivity.class);
                newIntent.putExtra("sender", username);
                newIntent.putExtra("receiver", nameSearch.getText().toString());
                searchFriendDialog.dismiss();
                startActivity(newIntent);
            }
        });


        searchFriendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        searchFriendDialog.show();


    }

    boolean isFriend(String friendName) {
        boolean isFriend = false;
        for (User user : friendList) {
            if (user.username.equals(friendName)) isFriend = true;
        }
        return isFriend;
    }

    void initView() {
        nameSearch = findViewById(R.id.name);
        btnSearch = findViewById(R.id.btnSearch);
        chatListView = findViewById(R.id.friendList);
        avatar = findViewById(R.id.avatar);
        avatar.setOnClickListener(this::onClick);
        btnSearch.setOnClickListener(this::onClick);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatList = new ArrayList<>();
        friendList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList);
        chatListView.setLayoutManager(new LinearLayoutManager(this));
        chatListView.addItemDecoration(new DividerItemDecoration(this, 0));
        chatListView.setAdapter(chatListAdapter);
        searchFriendDialog = new Dialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();


    }

    private void addFriend(String currentUsername, String friendUsername) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(currentUsername).child("friends").push().setValue(friendUsername);
        databaseReference.child("Users").child(friendUsername).child("friends").push().setValue(currentUsername);
    }


    void loadAvatar() {
        // check avatar exist
        databaseReference.child("Users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userProfile = snapshot.getValue(User.class);
                    if (!userProfile.avatar.equals("")) {
                        Glide.with(MainActivity.this).load(userProfile.avatar).into(avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        loadAvatar();
    }

    private void getFriendList() {
        databaseReference.child(username).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User friend = snapshot.getValue(User.class);
                friendList.add(friend);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                profile();
                break;
            case R.id.btnSearch:
                searchFriend();
                break;

        }
    }

    private void searchFriend() {
        Query query = databaseReference.child("Users").orderByChild("username").equalTo(nameSearch.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    nameSearch.setError("No user found");
                } else {
                    databaseReference.child("Users").child(nameSearch.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User friend = snapshot.getValue(User.class);
                            showPopup(btnSearch.getRootView(), friend);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void profile() {
        Intent profileIntent = new Intent();
        profileIntent.putExtra("username", username);
        profileIntent.setClass(MainActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
    }
}





