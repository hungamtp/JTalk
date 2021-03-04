package com.example.jtalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jtalk.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    Button btRegister;
    EditText username, email, password, repassword;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        email = findViewById(R.id.email);
        progressBar = findViewById(R.id.progress);
        btRegister = findViewById(R.id.btRegister);
        btRegister.setOnClickListener(this::onClick);
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btRegister:
                progressBar.setVisibility(View.VISIBLE);
                register();
                break;
        }
    }

    private void register() {
        String usernamestr = username.getText().toString().trim();
        String emailstr = email.getText().toString().trim();
        String passwordstr = password.getText().toString().trim();
        String repasswordstr = repassword.getText().toString();

        if (usernamestr.isEmpty()) {
            username.setError("Username must be filled");
            username.requestFocus();
            return;
        }

//        if(checkUsername(usernamestr)){
//            username.setError("Username is exist  , try another username");
//            return;
//        }

        Query query = databaseReference.child("Users").orderByChild("username").equalTo(usernamestr);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username.setError("username exist");
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!Patterns.EMAIL_ADDRESS.matcher(emailstr).matches()) {
            email.setError("example@gmail.com");
            email.requestFocus();
            return;
        }
        if (passwordstr.isEmpty()) {
            password.setError("Password must be filled");
            password.requestFocus();
            return;
        }
        if (passwordstr.length() < 8) {
            password.setError("Password have to have more 8 char");
            return;
        }

        if (!passwordstr.equals(repasswordstr)) {
            password.setError("Password is not matched");
            return;
        }

        if (emailstr.isEmpty()) {
            email.setError("Email must be filled");
            email.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailstr, passwordstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User newUser = new User(emailstr, usernamestr, passwordstr, "", false);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(usernamestr)
                            .setValue(newUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    }
                                }

                            });
                } else {
                    email.setError("Email existed");
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException existEmail) {
                        email.setError("Email existed");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}