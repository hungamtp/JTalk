package com.example.jtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.jtalk.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextView btRegister;
    TextView forgot;
    Button btLogin;
    EditText username;
    EditText password;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        // auto log
        username.setText("hungamtp");
        password.setText("hunghung");


    }

    void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        btRegister = findViewById(R.id.btRegister);
        btLogin = findViewById(R.id.btLogin);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgot = findViewById(R.id.forgot);
        progressBar = findViewById(R.id.progress);


        forgot.setOnClickListener(this::onClick);
        btRegister.setOnClickListener(this::onClick);
        btLogin.setOnClickListener(this::onClick);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btRegister:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btLogin:

                progressBar.setVisibility(View.VISIBLE);
               // Toast.makeText(LoginActivity.this ,  "clicked" , Toast.LENGTH_LONG).show();
                login();
                break;
            case R.id.forgot:
                forgotPassword();
                break;
        }
    }

    private void forgotPassword() {

        databaseReference.child("Users").child(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User currentUser = snapshot.getValue(User.class);
                    firebaseAuth.sendPasswordResetEmail(currentUser.email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                Toast.makeText(LoginActivity.this , "get the email" , Toast.LENGTH_LONG).show();
                                Intent newIntent = new Intent();
                                newIntent.setClass(LoginActivity.this , ResetPasswordActivity.class);
                                startActivity(newIntent);
                            }else{
                                Toast.makeText(LoginActivity.this , "no email" , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void login() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryUsername = databaseReference.child("Users").orderByChild("username").equalTo(username.getText().toString());
        queryUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query queryPassword = databaseReference.child("Users").orderByChild("password").equalTo(password.getText().toString());
                    queryPassword.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Intent intent = new Intent();
                                databaseReference.child("Users").child(username.getText().toString()).child("online").setValue(true);
                                intent.putExtra("username", username.getText().toString());
                                intent.setClass(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            } else {
                                password.setError("wrong password");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    username.setError("Account not found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    @Override
    protected void onPause() {
        super.onPause();
        progressBar.setVisibility(View.INVISIBLE);
        finish();
    }
}