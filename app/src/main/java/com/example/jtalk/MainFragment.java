package com.example.jtalk;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    RecyclerView friendListView;
    ArrayList<User> friendList;
    FriendListAdapter friendListAdapter;
    List<Chat> chatList;
    ChatListAdapter chatListAdapter;
    RecyclerView chatListView;
    DatabaseReference databaseReference;
    Dialog searchFriendDialog;
    String username;
    TextView search_bar;
    ImageView avatar;
    StorageReference storageReference;
    View actionBarView;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        initView();

        Intent intent = getActivity().getIntent();
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

        final NavController navController = Navigation.findNavController(getView());
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentDirections.MainToProfile mainToProfile = MainFragmentDirections.mainToProfile(username);
                mainToProfile.setUsername(username);
                navController.navigate(mainToProfile);
            }
        });

        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragmentDirections.MainToSearch mainToSearch = MainFragmentDirections.mainToSearch(username);
                mainToSearch.setUsername(username);
                navController.navigate(mainToSearch);
            }
        });

    }


    void initView() {
        // action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_main_activity);
        actionBarView = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        // view in action bar
        avatar = actionBarView.findViewById(R.id.avatar);
        search_bar = actionBarView.findViewById(R.id.search_bar);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        view = getView();


        // set up friend list
        friendListView = view.findViewById(R.id.friendList);
        friendList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(friendList);
        DividerItemDecoration divider_friend_list = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        divider_friend_list.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_friend_list));
        friendListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        friendListView.addItemDecoration(divider_friend_list);
        friendListView.setAdapter(friendListAdapter);

        //set up chat list
        DividerItemDecoration divider_chat_list =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider_chat_list.
                setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_chat_list));
        chatListView = view.findViewById(R.id.chatList);
        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList);
        chatListView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatListView.addItemDecoration(divider_chat_list);
        chatListView.setAdapter(chatListAdapter);

        searchFriendDialog = new Dialog(getContext());
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
                        Glide.with(getContext()).load(userProfile.avatar).into(avatar);
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
                            if (index != -1) {
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
                                if (snapshot.exists()) {
                                    Message lastMess = snapshot.getValue(Message.class);
                                    newChat.lastMessages = lastMess.message;
                                    int position = chatListAdapter.getPositionById(newChat.username);
                                    if (position == -1) {
                                        chatList.add(newChat);
                                    }else{
                                        chatListAdapter.newMessage(position);
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


}