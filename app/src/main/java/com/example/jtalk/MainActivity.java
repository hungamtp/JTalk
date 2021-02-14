package com.example.jtalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jtalk.adapter.FriendListAdapter;
import com.example.jtalk.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView friendListView;
    ArrayList<User> friendList;
    FriendListAdapter friendListAdapter;
    DatabaseReference databaseReference;
    EditText nameSearch;
    Button btnSearch;
    Dialog searchFriendDialog;
    String username;
    ImageView avatar;
    boolean isFound = false;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");


        // fecth data to recycle view
        databaseReference.child("Users").child(username).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String friendName = snapshot.getValue(String.class);
                databaseReference.child("Users").child(friendName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User myFriend = snapshot.getValue(User.class);
                        friendList.add(myFriend);
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
                            // add add friend
                            // databaseReference.child("Users").child(username).child("friends").push().setValue(nameSearch.getText().toString());
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
        avatar.setImageResource(R.drawable.ic_launcher_background);
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
                Intent newIntent = new Intent();
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
        friendListView = findViewById(R.id.friendList);
        avatar = findViewById(R.id.avatar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        friendList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(friendList);
        friendListView.setLayoutManager(new LinearLayoutManager(this));
        friendListView.setAdapter(friendListAdapter);
        searchFriendDialog = new Dialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();


    }

    private void addFriend(String currentUsername, String friendUsername) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(currentUsername).child("friends").push().setValue(friendUsername);
        databaseReference.child("Users").child(friendUsername).child("friends").push().setValue(username);
    }

}





