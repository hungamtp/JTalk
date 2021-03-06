package com.example.jtalk.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.jtalk.R;
import com.example.jtalk.adapter.MessageAdapter;
import com.example.jtalk.model.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatFragment extends Fragment {
    View view;
    String sender;
    String receiver;
    ArrayList<Message> messagesList;
    ListView messageListView;
    MessageAdapter messageAdapter;
    ImageView btnSend;
    EditText message;
    ImageView back;
    ImageView avatar;
    DatabaseReference databaseReference;
    TextView friendName;
    View actionbar;

    public ChatFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // resize when keyboard appear
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |  WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        view = getView();

        initView();
        loadAvatar();
        loadMessage();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message sendedMessage = new Message(sender, receiver, message.getText().toString(), true);
                databaseReference.child("Users").child(sender).child("Messages").child(receiver).push().setValue(sendedMessage);

                Message receiverMessage = new Message(sender, receiver, message.getText().toString(), false);
                databaseReference.child("Users").child(receiver).child("Messages").child(sender).push().setValue(receiverMessage);

                message.clearFocus();
                message.setText("");
                messageAdapter.notifyDataSetChanged();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.chatToMain);
            }
        });

    }

    void initView() {
        //get action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_chat_fragment);
        actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        actionbar.bringToFront();
        actionbar.setKeepScreenOn(false);


        // action bar view
        avatar = actionbar.findViewById(R.id.avatar);
        back = actionbar.findViewById(R.id.back);
        friendName = actionbar.findViewById(R.id.friendName);

        // init view
        messageListView = view.findViewById(R.id.messageListView);
        btnSend = view.findViewById(R.id.btnSend);
        message = view.findViewById(R.id.message);

        friendName.setText(receiver);

        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesList);
        messageListView.setAdapter(messageAdapter);


        // firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    void loadAvatar() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(receiver).child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String avatarLink = snapshot.getValue(String.class);
                    if (!avatarLink.equals("")) {
                        Glide.with(getContext()).load(avatarLink).into(avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void loadMessage() {
        // fetch Message from firebase
        databaseReference.child("Users").child(sender).child("Messages").child(receiver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message newMessage = snapshot.getValue(Message.class);
                messagesList.add(newMessage);
                messageAdapter.notifyDataSetChanged();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            ChatFragmentArgs args = ChatFragmentArgs.fromBundle(getArguments());
            sender = args.getSender();
            receiver = args.getReceiver();
        }

    }
}