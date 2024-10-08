package com.example.beachbluenoser;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import java.util.*;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LifeguardRegistration extends AppCompatActivity {

    EditText emailAdd, accessToken, beachType;
    Button backBtn, registerBtn;

    String email, AccToken, lgID, beachName, beach;

    private int temp = 0;
    FirebaseFirestore beachBluenoserDB, beachBluenoserDBB;
    private FirebaseAuth beachBluenoserAuth;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifeguard_registration_page);

        emailAdd = findViewById(R.id.registerLifeguardEmail);
        //New field for beach token
        beachType = findViewById(R.id.beachToken);
        //accessToken = findViewById(R.id.editAccessToken);
        backBtn = findViewById(R.id.backButton);
        registerBtn = findViewById(R.id.registerBtn);
        mp = MediaPlayer.create(this, R.raw.click);

        beachBluenoserDB = FirebaseFirestore.getInstance();
        beachBluenoserDBB = FirebaseFirestore.getInstance();
        beachBluenoserAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(LifeguardRegistration.this, Registration.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                email = emailAdd.getText().toString().trim();
                //Beach Type
                beach = beachType.getText().toString().trim();
                // Generate a random three-character string
                StringBuilder accTokenBuilder = new StringBuilder();
                Random random = new Random();
                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                for (int i = 0; i < 6; i++) {
                    accTokenBuilder.append(characters.charAt(random.nextInt(characters.length())));
                }
                AccToken = accTokenBuilder.toString();

                if(TextUtils.isEmpty(email)){
                    emailAdd.setError("Please Enter an Email!");
                    return;
                }else if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2,3}+")){
                    emailAdd.setError("Email is Invalid.");
                    return;
                }
//                if(TextUtils.isEmpty(AccToken)){
//                    accessToken.setError("Please Enter an Access Token!");
//                    return;
//                }

                //Using AccToken directly
                temp = 1;
                lgID = UUID.randomUUID().toString();
                checkEmail(email); // Check email existence


//                beachBluenoserDBB.collection("AccessToken").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//
//                        if (e != null) {}
//
//                        for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
//
//                            String isAttendance = documentChange.getDocument().get("Token").toString();
//
//                            Log.d(TAG, "Test " + AccToken);
//                            if (isAttendance.equals(AccToken)) {
//                                temp = 1;
//                                lgID = UUID.randomUUID().toString();
//                                checkEmail(email);
//                                beachName = documentChange.getDocument().get("beachName").toString();
//                                break;
//                            }
//                            if (temp == 0) {
//                                Toast.makeText(LifeguardRegistration.this, "Invalid Token", Toast.LENGTH_SHORT).show();
//                                break;
//                            }
//                        }
//                    }
//                });
            }
        });
    }

    private boolean addEmail() {

        DocumentReference documentReference = beachBluenoserDB.collection("Lifeguard").document(lgID);

        Map<String, Object> user = new HashMap<>();

        user.put("Email", email);
        user.put("Token", AccToken);
        //Hot-fix for registration
        user.put("Beach", beach);
        user.put("userType", "Lifeguard");

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: user Profile is created for " + lgID);
            }
        });
        Intent intent = (new Intent(LifeguardRegistration.this, LifeguardLogin.class));
        intent.putExtra("beachName",beachName);
        intent.putExtra("userType","Lifeguard");
        startActivity(intent);
        Toast.makeText(LifeguardRegistration.this, "Lifeguard Account Created.", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void checkEmail(String email) {
        String email12 = email;

        beachBluenoserDB.collection("Lifeguard")
                .whereEqualTo("Email", email12)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int len = task.getResult().size();
                            if (len == 0) {
                                // add email here
                                Log.d(TAG, "add email here by checking length");
                                addEmail();
                                createUserWithEmailAndPassword(email, AccToken);
                            } else {
                                // toast here
                                Toast.makeText(LifeguardRegistration.this, "Email exists.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //Create account for Firebase authentication
    private void createUserWithEmailAndPassword(String email, String password) {
        beachBluenoserAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailAndPassword:success");
                            addEmail(); // After successfully creating the user, add email to Firestore
                        } else {
                            Log.e(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(LifeguardRegistration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
