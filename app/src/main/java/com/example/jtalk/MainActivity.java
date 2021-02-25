package com.example.jtalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jtalk.adapter.ChatListAdapter;
import com.example.jtalk.adapter.FriendListAdapter;
import com.example.jtalk.model.Chat;
import com.example.jtalk.model.Message;
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
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity  {
    RecyclerView friendListView;
    ArrayList<User> friendList;
    FriendListAdapter friendListAdapter;
    List<Chat> chatList;
    ChatListAdapter chatListAdapter;
    RecyclerView chatListView;
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
        getFriendList();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                getChatList();
            }
        }, 500);



        //  find user
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    showPopup(v, friend);
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
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent();
                profileIntent.putExtra("username", username);
                profileIntent.setClass(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        nameSearch = findViewById(R.id.name);
        btnSearch = findViewById(R.id.btnSearch);
        friendListView = findViewById(R.id.friendList);
        avatar = findViewById(R.id.avatar);
        chatListView = findViewById(R.id.chatList);

        friendList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(friendList);
        DividerItemDecoration divider_friend_list = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        divider_friend_list.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_friend_list));
        friendListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        friendListView.addItemDecoration(divider_friend_list);
        friendListView.setAdapter(friendListAdapter);


        DividerItemDecoration divider_chat_list =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider_chat_list.
                setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_chat_list));
        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList);
        chatListView.setLayoutManager(new LinearLayoutManager(this));
        chatListView.addItemDecoration(divider_chat_list);
        chatListView.setAdapter(chatListAdapter);

        searchFriendDialog = new Dialog(this);
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


    void getFriendList() {
        databaseReference.child("Users").child(username).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String friendName = snapshot.getValue(String.class);
                databaseReference.child("Users").child(friendName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User friend = snapshot.getValue(User.class);
                        int position = friendListAdapter.getPositionById(friend.username);
                        // check user in list
                        if (position == -1) {
                            friendList.add(friend);
                        } else {
                            // update online offline in chat list
                            int index = chatListAdapter.getPositionById(friend.username);
                            if(index != -1) {
                                chatList.get(index).online = friend.online;
                                chatListAdapter.notifyDataSetChanged();
                            }
                            // update friend list
                            friendList.get(position).online = friend.online;
                        }

                        friendListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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

    void getChatList() {
        databaseReference.child("Users").child(username).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String friendName = snapshot.getValue(String.class);
                databaseReference.child("Users").child(friendName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Chat newChat = snapshot.getValue(Chat.class);
                        databaseReference.child("Users").child(username).child("Messages").child(newChat.username).limitToLast(1).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if(snapshot.exists()){
                                    Message lastMess = snapshot.getValue(Message.class);
                                    newChat.lastMessages = lastMess.message;
                                    int position = chatListAdapter.getPositionById(newChat.username);
                                    if(position  == -1){
                                        chatList.add(newChat);
                                    }
                                    chatListAdapter.notifyDataSetChanged();
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
    protected void onRestart() {
        super.onRestart();
        loadAvatar();

    }


}





