package com.example.dictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public EditText emailId, password;
    Button btn1, btn2;
    FirebaseAuth mFirebaseAuth;
    public static String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        btn1 = findViewById(R.id.buttonSignUp);
        btn2 = findViewById(R.id.buttonAlreadySignUp);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                 if (email.isEmpty()){
                     emailId.setError("Please Enter your Email id");
                     emailId.requestFocus();
                 }
                 else if (pwd.isEmpty()){
                     password.setError("Please Enter Your Password");
                     password.requestFocus();
                 }
                 else if (email.isEmpty() && pwd.isEmpty()){
                     Toast.makeText(MainActivity.this, "Fields Are Empty!",Toast.LENGTH_SHORT).show();
                 }
                 else if (!(email.isEmpty() && pwd.isEmpty())){
                     mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (!task.isSuccessful()){
                                 Toast.makeText(MainActivity.this,"SignUp Unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();
                             }
                             else {
                                 startActivity(new Intent(MainActivity.this,HomeActivity.class));
                             }
                         }
                     });
                 }
                 else {
                     Toast.makeText(MainActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
                 }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
