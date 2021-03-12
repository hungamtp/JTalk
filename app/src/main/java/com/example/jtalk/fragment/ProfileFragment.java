package com.example.jtalk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.jtalk.LoginActivity;
import com.example.jtalk.MainActivity;
import com.example.jtalk.R;
import com.example.jtalk.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    boolean CHANGE_PASSWORD_FORM_IS_SHOWN = false;

    FirebaseStorage storage;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    TextView friend;
    TextView username;
    TextView email;
    TextView changPassword;
    EditText currentPassword;
    EditText newPassword;
    TextView btSave;
    TextView done;
    ImageView back;
    ImageView avatar;
    Button signOut;
    ProgressBar save_progress;

    String usernameStr;
    View v;
    View actionbar;
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
        loadProfile();
        getFriendCount();

    }

    void initView() {
        // get action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_profile_activity);
        actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();

        // init view in actionbar
        done = actionbar.findViewById(R.id.done);

        back = actionbar.findViewById(R.id.back);

        // init view
        friend = v.findViewById(R.id.number_friend);
        changPassword = v.findViewById(R.id.change_password);
        username = v.findViewById(R.id.username);
        avatar = v.findViewById(R.id.avatar);
        email = v.findViewById(R.id.email);
        signOut = v.findViewById(R.id.sign_out);
        currentPassword = v.findViewById(R.id.current_password);
        newPassword = v.findViewById(R.id.new_password);
        btSave = v.findViewById(R.id.bt_save);
        save_progress = v.findViewById(R.id.save_progress);

        // set on click
        avatar.setOnClickListener(this::onClick);
        signOut.setOnClickListener(this::onClick);
        changPassword.setOnClickListener(this::onClick);
        btSave.setOnClickListener(this::onClick);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.profileToMain);
            }
        });


        // set up firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance("gs://timer-34f5a.appspot.com");


    }

    void getFriendCount() {
        final AtomicInteger increase = new AtomicInteger();
        Query query = databaseReference.child("Users").child(usernameStr).child("friends");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int count = increase.incrementAndGet();
                friend.setText("Friend: " + count + "");
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


    void loadProfile() {
        // check avatar exist
        databaseReference.child("Users").child(usernameStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userProfile = snapshot.getValue(User.class);
                    username.setText(userProfile.username);
                    email.setText(userProfile.email);
                    if (!userProfile.avatar.equals("")) {
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
        switch (v.getId()) {
            case R.id.avatar:
                getAvatar();
                break;
            case R.id.sign_out:
                signOut();
                break;
            case R.id.change_password:
                moveDown();
                break;
            case R.id.bt_save:
                changPassword();
                break;

        }

    }

    private void moveDown() {
        Animation hide;
        hide = new AnimationUtils().loadAnimation(v.getContext(), R.anim.hide);
        Animation show;
        show = new AnimationUtils().loadAnimation(v.getContext(), R.anim.show);
        currentPassword.setVisibility(View.VISIBLE);
        newPassword.setVisibility(View.VISIBLE);
        btSave.setVisibility(View.VISIBLE);

        changPassword.setVisibility(View.INVISIBLE);

    }

    private void changPassword() {
        save_progress.setVisibility(View.VISIBLE);
        btSave.setVisibility(View.INVISIBLE);


        Query query = databaseReference.child("Users").orderByChild("password").equalTo(currentPassword.getText().toString().trim());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    databaseReference.child("Users").child(usernameStr).child("password").setValue(newPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(v.getContext(), "password changed successfully", Toast.LENGTH_SHORT).show();
                            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.hide);

                            currentPassword.setVisibility(View.INVISIBLE);
                            newPassword.setVisibility(View.INVISIBLE);
                            save_progress.setVisibility(View.INVISIBLE);
                            changPassword.setVisibility(View.VISIBLE);
                        }
                    });

                } else {
                    currentPassword.setError("wrong password");
                    btSave.setVisibility(View.VISIBLE);
                    save_progress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    void signOut() {


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


    private void updateProfile() {
        if (isAvatarChanged) {
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
        Navigation.findNavController(getView()).navigate(R.id.profileToMain);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            avatar.setImageBitmap(bitmap);
            isAvatarChanged = true;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            ProfileFragmentArgs args = ProfileFragmentArgs.fromBundle(getArguments());
            usernameStr = args.getUsername();
        }

    }
}
