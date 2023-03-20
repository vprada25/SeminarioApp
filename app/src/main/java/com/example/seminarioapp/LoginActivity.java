package com.example.seminarioapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {


    EditText mTextUser;
    EditText mTextPassword;
    EditText mTextEmail;
    Button mButtonRegister;

    //firebase
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    //firebase

    ProgressDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextUser = findViewById(R.id.textUsers);
        mTextEmail = findViewById(R.id.textEmail);
        mTextPassword = findViewById(R.id.textPassword);
        mButtonRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        Date mDate = new Date();
        SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String mDateFormatted = mFormat.format(mDate);



        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mTextUser.getText().toString();
                String email = mTextEmail.getText().toString();
                String password = mTextPassword.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mTextEmail.setError("Invalid Email");
                    mTextEmail.setFocusable(true);
                }else if(password.length()<6){
                    mTextPassword.setError("Password length at least 6 characters");
                    mTextEmail.setFocusable(true);
                }else{
                RegisterPlayer(email, password);
                }

            }

        });
        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("Registering User...");
        mDialog.setCancelable(false);
    }
    private void RegisterPlayer(String email, String password) {
        mDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            FirebaseUser mUser = mAuth.getCurrentUser();
                            int count = 0;
                            assert mUser != null;
                            String mUserId = mUser.getUid();
                            String mUserName = mTextUser.getText().toString();
                            String mEmail = mTextEmail.getText().toString();
                            String mPassword = mTextPassword.getText().toString();

                            HashMap<Object, Object> player = new HashMap<>();
                            player.put("uid", mUserId);
                            player.put("name", mUserName);
                            player.put("email", mEmail);
                            player.put("password", mPassword);

                            mDatabase.getReference("Users").child(mUserId).setValue(player);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Toast.makeText(LoginActivity.this, "Registered...", Toast.LENGTH_SHORT).show();
                            finish();

                        }else{
                            mDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    });
    }

}

