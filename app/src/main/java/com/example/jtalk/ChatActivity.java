package com.example.jtalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jtalk.adapter.MessageAdapter;
import com.example.jtalk.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {
    String sender ;
    String receiver;
    ArrayList<Message> messagesList ;
    ListView messageListView ;
    MessageAdapter messageAdapter;
    Button btnSend;
    EditText message;
    ImageView back;
    DatabaseReference databaseReference;
    TextView friendName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);
        initView();

        // get message from firebase
        databaseReference.child("Users").child(sender).child("Messages").addChildEventListener(new ChildEventListener() {
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message sendedMessage = new Message(sender , receiver , message.getText().toString() , true);
                Message receiverMessage = new Message(sender , receiver , message.getText().toString() , false);
                databaseReference.child("Users").child(sender).child("Messages").push().setValue(sendedMessage);
                databaseReference.child("Users").child(receiver).child("Messages").push().setValue(receiverMessage);
                message.clearFocus();
                message.setText("");
                messageAdapter.notifyDataSetChanged();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void initView(){
        Intent intent = getIntent();
        sender = intent.getStringExtra("sender");
        receiver = intent.getStringExtra("receiver");
        messagesList = new ArrayList<>();
        back = findViewById(R.id.back);
        messageAdapter = new MessageAdapter(messagesList);
        messageListView = findViewById(R.id.messageListView);

        messageListView.setAdapter(messageAdapter);
        btnSend = findViewById(R.id.btnSend);
        message = findViewById(R.id.message);
        friendName = findViewById(R.id.friendName);
        friendName.setText(receiver);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

}