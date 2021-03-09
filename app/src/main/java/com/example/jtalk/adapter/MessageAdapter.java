package com.example.jtalk.adapter;

import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.jtalk.R;
import com.example.jtalk.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MessageAdapter extends BaseAdapter {
    ArrayList<Message> messageList;
    static final int SENDER = 1;

    public MessageAdapter(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }


    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View messageView;
        Message message = (Message) getItem(position);
        if (convertView == null) {
            if (getItemViewType(position) == SENDER) {
                if(message.isImage){
                    messageView = View.inflate(parent.getContext(), R.layout.image_message, null);
                    Glide.with(messageView.getContext()).load(message.message).into((ImageView) messageView.findViewById(R.id.image));

                }else{
                    messageView = View.inflate(parent.getContext(), R.layout.message_view_of_sender, null);
                    ((TextView) messageView.findViewById(R.id.message)).setText(String.format("%s", message.message));
                }


            } else {
                if(message.isImage){
                    messageView = View.inflate(parent.getContext(), R.layout.image_message_of_receiver, null);
                    Glide.with(messageView.getContext()).load(message.message).into((ImageView) messageView.findViewById(R.id.image));
                }else{
                    messageView = View.inflate(parent.getContext(), R.layout.message_view_of_receiver, null);
                    ((TextView) messageView.findViewById(R.id.message)).setText(String.format("%s", message.message));

                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Users").child(message.sender).child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String avatarLink = snapshot.getValue(String.class);
                        if (!avatarLink.equals("")) {
                            Glide.with(parent.getContext()).load(avatarLink).into((ImageView) messageView.findViewById(R.id.avatar));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        } else messageView = convertView;



        return messageView;
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        boolean isSender = false;
        if (messageList.get(position).isSender) {
            isSender = true;
        }
        if (isSender) type = SENDER;

        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
