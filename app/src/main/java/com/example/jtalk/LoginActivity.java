package com.example.jtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btRegister ;
    Button btLogin;
    EditText username;
    EditText password;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btRegister = findViewById(R.id.btRegister);
        btRegister.setOnClickListener(this);

        btLogin= findViewById(R.id.btLogin);
        btLogin.setOnClickListener(this);


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        firebaseAuth = FirebaseAuth.getInstance();
        // auto log
//        username.setText("hungamtp");
//        password.setText("hunghung");
//        btLogin.callOnClick();

        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btRegister:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this , RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btLogin:
                login();
                break;
        }
    }

    private void login() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryUsername = databaseReference.child("Users").orderByChild("username").equalTo(username.getText().toString());
        queryUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Query queryPassword = databaseReference.child("Users").orderByChild("password").equalTo(password.getText().toString());
                    queryPassword.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Intent intent = new Intent();
                                databaseReference.child("Users").child(username.getText().toString()).child("online").setValue(true);
                                intent.putExtra("username" , username.getText().toString()) ;
                                intent.setClass(LoginActivity.this , MainActivity.class);
                                startActivity(intent);

                            }else{
                                password.setError("wrong password");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else{
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
        finish();
    }
}