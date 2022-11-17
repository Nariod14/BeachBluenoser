package com.example.beachbluenoser;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {

    EditText usernameField, passwordField, fullNameField, emailAddressField;
   // FirebaseDatabase BBDevDB;
    FirebaseFirestore beachBluenoserDB;
    private FirebaseAuth beachBluenoserAuth, beachBluenoserAuth2;
    Switch aSwitch;
    Button registerBtn;
    String username, email, fullName, password, userID;
    ImageButton backArrowKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameField = findViewById(R.id.registerUsernameTxt);
        passwordField = findViewById(R.id.registerPasswordTxt);
        emailAddressField = findViewById(R.id.registerEmailAddressTxt);
        fullNameField = findViewById(R.id.registerFullNameTxt);
        registerBtn = findViewById(R.id.signUpBtn);
        aSwitch = findViewById(R.id.switchUser);
        backArrowKey = findViewById(R.id.backArrow);

        /*BBDevDB = FirebaseDatabase.getInstance();
        beachBluenoserAuth = FirebaseAuth.getInstance();
*/

        beachBluenoserDB = FirebaseFirestore.getInstance();
        beachBluenoserAuth2 = FirebaseAuth.getInstance();


        username = usernameField.getText().toString();
        fullName = fullNameField.getText().toString();
        email = emailAddressField.getText().toString().trim();
        password = passwordField.getText().toString().trim();

        backArrowKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this,Login.class);
                startActivity(intent);

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b == true){
                    Intent intent = new Intent(Registration.this, LifeguardRegistration.class);
                    startActivity(intent);
                }else{
                    finish();
                }
            }
        });

        if(beachBluenoserAuth2.getCurrentUser() != null){
            startActivity(new Intent(Registration.this,SplashPage.class)); //check later
            finish();
        }
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                beachBluenoserAuth2.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(TextUtils.isEmpty(username)){
                            usernameField.setError("Email is Required.");
                            return;
                        }


                        if(TextUtils.isEmpty(email)){
                            emailAddressField.setError("Email is Required.");
                            return;
                        }

                        if(TextUtils.isEmpty(password)){
                            passwordField.setError("Password is Required.");
                            return;
                        }

                        if(password.length() < 6){
                            passwordField.setError("Password Must be >= 6 Characters");
                            return;
                        }

                        registerUser();


            }







        });


    }


    private void registerUser(){

        Toast.makeText(Registration.this, "User Created.", Toast.LENGTH_SHORT).show();
        userID = beachBluenoserAuth.getCurrentUser().getUid();

        DocumentReference documentReference = beachBluenoserDB.collection("users").document(userID);

        Map<String, Object> user = new HashMap<>();
        user.put("Fullname", fullName);
        user.put("Email", email);
        user.put("Username", username);
        user.put("Password", password);

        if (username.isEmpty() || fullName.isEmpty() ||  !isValidEmailAddress(email) ||  password.isEmpty() ) {
            Toast.makeText(Registration.this, "Please fill out all the fields correctly!", Toast.LENGTH_LONG).show();

            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.toString());
                }

            });
            startActivity(new Intent(Registration.this, SplashPage.class));
        }else {
                Toast.makeText(Registration.this, "Registration Failed :(",
                        Toast.LENGTH_LONG).show();
            }

        }


        });


    }

    protected static boolean isValidEmailAddress(String emailAddress) {
        return !emailAddress.isEmpty() && emailAddress.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2,3}+");
    }






}