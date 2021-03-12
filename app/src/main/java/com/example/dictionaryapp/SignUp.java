package com.example.dictionaryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public  class SignUp extends AppCompatActivity implements OnClickListener {
    ProgressBar progressBar ;
    EditText editTextEmail , editTextPassword ;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
     editTextEmail =  (EditText) findViewById(R.id.editTextEmail) ;
     editTextPassword = (EditText) findViewById(R.id.editTextPassword) ;
     progressBar = (ProgressBar) findViewById(R.id.progressbar) ;


     mAuth = FirebaseAuth.getInstance();

     findViewById(R.id.buttonSignUp).setOnClickListener(this);
     findViewById(R.id.buttonNeedSignUp).setOnClickListener(this);
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter valid email ");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length()<6){
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password) .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
              if (task.isSuccessful()){
                  finish();
                  startActivity(new Intent(SignUp.this,MainActivity2.class));
                  Toast.makeText(getApplicationContext(), "User Registration Successful", Toast.LENGTH_SHORT).show();
                  Intent  intent = new Intent(SignUp.this, MainActivity2.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  startActivity(intent);
              }else {
                  Toast.makeText(getApplicationContext(), "Some error occured",Toast.LENGTH_SHORT).show();
                  if (task.getException()  instanceof FirebaseAuthUserCollisionException){
                      Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                  }else {
                      Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                  }
              }
            }
        });


    }

            @Override
            public void onClick (View v) {
                switch (v.getId()){
                    case R.id.buttonSignUp :
                        registerUser();
                        break;

                    case R.id.buttonAlreadySignUp:
                        finish();
                        startActivity(new Intent(this,MainActivity.class));
                        break;
                }

            }

    }