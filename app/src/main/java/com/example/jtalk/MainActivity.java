package com.example.jtalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.jtalk.adapter.FriendListAdapter;
import com.example.jtalk.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView friendListView;
    ArrayList<User> friendList ;
    FriendListAdapter friendListAdapter;
    DatabaseReference databaseReference;
    EditText name;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        Query query = databaseReference.child("Users").orderByChild("username").equalTo(name.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    void initView(){
        name = findViewById(R.id.name);
        btnSearch = findViewById(R.id.btnSearch);
        friendListView = findViewById(R.id.friendList);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        friendList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(friendList);
        friendListView.setLayoutManager(new LinearLayoutManager(this));
        friendListView.setAdapter(friendListAdapter);

    }
}