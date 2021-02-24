package com.example.jtalk.database;

import com.example.jtalk.model.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageDatabase {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    void getMessage(){

    }

    void uploadMessage(Message newMessage){

    }


}
