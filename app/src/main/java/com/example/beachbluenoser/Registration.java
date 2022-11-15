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
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {

    EditText userName, passwordField, fullName, emailAddress;
    FirebaseFirestore beachBluenoserDB;
    private FirebaseAuth beachBluenoserAuth;
    Switch aSwitch;
    Button registerBtn;
    String username, email, fullname, password, userID;
    ImageButton backArrowkey;
    Boolean bool = false;

    String punctuation = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userName = findViewById(R.id.registerUsernameTxt);
        passwordField = findViewById(R.id.registerPasswordTxt);
        emailAddress = findViewById(R.id.registerEmailAddressTxt);
        fullName = findViewById(R.id.registerFullNameTxt);
        registerBtn = findViewById(R.id.signUpBtn);
        aSwitch = findViewById(R.id.switchUser);
        backArrowkey = findViewById(R.id.backArrow);

        /*BBDevDB = FirebaseDatabase.getInstance();
        beachBluenoserAuth = FirebaseAuth.getInstance();
*/

        beachBluenoserDB = FirebaseFirestore.getInstance();
        beachBluenoserAuth = FirebaseAuth.getInstance();




        backArrowkey.setOnClickListener(new View.OnClickListener() {
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

      /*  if(beachBluenoserAuth.getCurrentUser() != null){
            startActivity(new Intent(Registration.this,BeachListActivity.class)); //check later
            finish();
        }*/
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = userName.getText().toString();
                fullname = fullName.getText().toString();
                email = emailAddress.getText().toString().trim();
                password = passwordField.getText().toString().trim();


                if(TextUtils.isEmpty(username)){
                    userName.setError("Please Enter a Username!");
                }else if(username.contains("!")||username.contains("#")||username.contains("$")||username.contains("%")||username.contains("&")||username.contains("'")||username.contains("(")||username.contains(")")||username.contains("*")||username.contains("+")||username.contains(",")||username.contains("-")||username.contains(".")||username.contains("/")||username.contains(":")||username.contains(";")||username.contains("<")||username.contains("=")||username.contains(">")||username.contains("?")||username.contains("@")||username.contains("[")||username.contains("]")||username.contains("^")||username.contains("_")||username.contains("`")||username.contains("{")||username.contains("|")||username.contains("}")||username.contains("~")){
                    userName.setError("Special symbols are not allowed!");
                }else if(username.matches("[0-9]+")){
                    userName.setError("Numbers are not allowed!");
                }

                if(TextUtils.isEmpty(email)){
                    emailAddress.setError("Please Enter an Email!");
                    return;
                }else if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2,3}+")){
                    emailAddress.setError("Email is Invalid.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    passwordField.setError("Please Enter a Password!");
                    return;
                }else if((password.length() < 8) && !password.matches("[a-zA-Z0-9._-]")){
                    passwordField.setError("Password needs to be more than 8 characters and a mix of alphabets and numbers!");
                    return;
                }

                beachBluenoserAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((task)->{
                    if (task.isSuccessful()){
                        Toast.makeText(Registration.this, "User Created.", Toast.LENGTH_SHORT).show();
                        userID = beachBluenoserAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = beachBluenoserDB.collection("users").document(userID);

                        Map<String, Object> user = new HashMap<>();
                        user.put("Fullname", fullname);
                        user.put("Email", email);
                        user.put("Username", username);
                        user.put("Password", password);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"onSuccess: user Profile is created for " + userID );
                            }
                        });
                        startActivity(new Intent(Registration.this, MainActivity.class));
                    }else{
                        Toast.makeText(Registration.this, "Error! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show(); //example - It will show an error if email already exists
                    }


                });


    }

        });




    }








}