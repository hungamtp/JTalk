package com.example.jtalk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import com.bumptech.glide.Glide;

import com.example.jtalk.R;
import com.example.jtalk.adapter.MessageAdapter;
import com.example.jtalk.model.Message;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    final static  int REQUEST_CODE =1;
    private static final String TAG = "ChatFragment";

    View view;
    String sender;
    String receiver;
    ArrayList<Message> messagesList;
    ListView messageListView;
    MessageAdapter messageAdapter;
    ImageView btnSend;
    EditText send_message;
    ImageView back;
    ImageView avatar;
    ImageView image_icon;
    CircleImageView heart_icon;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseStorage storage;
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
                Message sendedMessage = new Message(sender, receiver, send_message.getText().toString(), true);
                databaseReference.child("Users").child(sender).child("Messages").child(receiver).push().setValue(sendedMessage);

                Message receiverMessage = new Message(sender, receiver, send_message.getText().toString(), false);
                databaseReference.child("Users").child(receiver).child("Messages").child(sender).push().setValue(receiverMessage);

                messageAdapter.notifyDataSetChanged();


                send_message.clearFocus();
                send_message.setText("");

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.chatToMain);
            }
        });

        image_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openImageGallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(openImageGallery , REQUEST_CODE);
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
        send_message = view.findViewById(R.id.send_message);
        image_icon = view.findViewById(R.id.image_icon);
        heart_icon = view.findViewById(R.id.heart_icon);

        friendName.setText(receiver);

        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesList);
        messageListView.setAdapter(messageAdapter);


        // firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        storage = FirebaseStorage.getInstance("gs://timer-34f5a.appspot.com");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){

            Calendar calendar = Calendar.getInstance();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG , 100 , baos);
            byte[] image = baos.toByteArray();
            UploadTask uploadTask = storageReference.child("messages/" + calendar.getTimeInMillis()+""+ calendar.getTime().toString().replaceAll(" ", "") + ".jpg").putBytes(image);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        Log.d(TAG, "onComplete: task fail ");
                        throw task.getException();
                    }
                    else{
                        Log.d(TAG, "onComplete: task success "+storageReference.getDownloadUrl());
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        databaseReference.child("Users").child(sender).child("Messages").child(receiver).push().setValue(new Message(sender , receiver ,downloadUri.toString() , true ));
                        databaseReference.child("Users").child(receiver).child("Messages").child(sender).push().setValue(new Message(sender , receiver ,downloadUri.toString() , false ));
                    }
                    else{
                        Log.d(TAG, "get url fail");
                    }
                }
            });

        }
    }
}