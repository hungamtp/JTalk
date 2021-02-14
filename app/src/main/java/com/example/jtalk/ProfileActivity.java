package com.example.jtalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jtalk.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity  {
    FirebaseStorage storage;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    TextView username;
    TextView email;
    TextView changPassword;
    TextView done;
    TextView cancel;
    ImageView avatar;
    String usernameStr;
    boolean isAvatarChanged = false;
    final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        Intent intent = getIntent();
        usernameStr = intent.getStringExtra("username");
        loadProfile();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                }

                Intent backMainActivity = new Intent();
                backMainActivity.setClass(ProfileActivity.this, MainActivity.class);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMain = new Intent();
                backToMain.setClass(ProfileActivity.this, MainActivity.class);
                startActivity(backToMain);
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            isAvatarChanged = true;
            avatar.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void initView() {
        username = findViewById(R.id.username);
        avatar = findViewById(R.id.avatar);
        email = findViewById(R.id.email);
        done = findViewById(R.id.done);
        cancel = findViewById(R.id.cancel);
        changPassword = findViewById(R.id.change_password);
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
                    if (userProfile.avatar) {
                        // load image from firebase storage
                        storageReference = FirebaseStorage.getInstance().getReference().child("avatar/" + userProfile.username + ".jpg");
                        try {
                            final File localFile = File.createTempFile(userProfile.username, ".jpg");
                            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    ImageView imageView = findViewById(R.id.avatar);
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}