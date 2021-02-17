package com.example.jtalk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.jtalk.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    FirebaseStorage storage;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    TextView username;
    TextView email;
    TextView changPassword;
    TextView done;
    TextView cancel;
    ImageView avatar;
    Button signout ;
    String usernameStr;
    View v;
    boolean isAvatarChanged = false;
    final int REQUEST_CODE = 1;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        v = getView();
        initView();
        Intent intent = getActivity().getIntent();
        usernameStr = intent.getStringExtra("username");
        loadProfile();

    }
    void initView() {
        username = v.findViewById(R.id.username);
        avatar = v.findViewById(R.id.avatar);
        avatar.setOnClickListener(this::onClick);
        email = v.findViewById(R.id.email);
        done = v.findViewById(R.id.done);
        done.setOnClickListener(this::onClick);
        cancel = v.findViewById(R.id.cancel);
        cancel.setOnClickListener(this::onClick);
        signout = v.findViewById(R.id.sign_out);
        signout.setOnClickListener(this::onClick);
        changPassword = v.findViewById(R.id.change_password);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance("gs://timer-34f5a.appspot.com");

    }
    void loadProfile() {
        // check avatar exist
        databaseReference.child("Users").child(usernameStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userProfile = snapshot.getValue(User.class);
                    username.setText(userProfile.username);
                    email.setText(userProfile.email);
                    if(!userProfile.avatar.equals("")) {
                        Glide.with(getActivity().getBaseContext()).load(userProfile.avatar).into(avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.done:
                done();
                break;
            case R.id.cancel:
                cancel();
                break;
            case R.id.avatar:
                getAvatar();
                break;
            case R.id.sign_out:
                signOut();
                break;
        }

    }
    void signOut(){


        databaseReference.child("Users").child(usernameStr).child("online").setValue(false);
        Intent intent = new Intent();
        intent.setClass(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();

    }

    private void getAvatar() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void cancel() {

        getActivity().finish();
    }

    private void done() {
        databaseReference.child("Users").child(usernameStr).child("email").setValue(email.getText().toString());
        if (isAvatarChanged) {
            databaseReference.child("Users").child(usernameStr).child("image").setValue(true);
            avatar.setDrawingCacheEnabled(true);
            avatar.buildDrawingCache();
            storageReference = FirebaseStorage.getInstance().getReference().child("avatar/" + usernameStr + ".jpg");
            Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageReference.putBytes(data);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        databaseReference.child("Users").child(usernameStr).child("avatar").setValue(downloadUri.toString());
                    }
                }
            });
        }
        getActivity().finish();
    }
}
