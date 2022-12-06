package com.example.beachbluenoser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PasswordReset extends AppCompatActivity {
    Button confirmBtn;
    ImageButton backBtn;
    EditText enterEmail;
    String userID;
    String emailAddress;
    FirebaseAuth beachBluenoserAuth;
    FirebaseFirestore beachBluenoserDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        beachBluenoserDB = FirebaseFirestore.getInstance();
        beachBluenoserAuth= FirebaseAuth.getInstance();

        confirmBtn=findViewById(R.id.continueButton);
        enterEmail=findViewById(R.id.email);
        backBtn =findViewById(R.id.backArrowButton);


        confirmBtn.setOnClickListener(view -> {
            emailAddress = enterEmail.getText().toString();
            if (TextUtils.isEmpty(emailAddress)) {
                enterEmail.setError("Please Enter an Email!");
            }else if(!emailAddress.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2,3}+")){
                enterEmail.setError("Please Enter a valid Email!");
            }else{
                beachBluenoserAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordReset.this, "Password reset link has been sent to your email", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(PasswordReset.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }



        });

        backBtn.setOnClickListener(v -> startActivity(new Intent(PasswordReset.this, Login.class)));
    }

}