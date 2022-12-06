package com.example.beachbluenoser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class userprofile extends AppCompatActivity {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth beachBluenoserAuth = FirebaseAuth.getInstance();
    public Button edit, signOutBtn;
    public TextView Email, username, FullName;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        //edit = findViewById(R.id.editProfileBtn);
        signOutBtn = findViewById(R.id.SignOut);
        Email = findViewById(R.id.EmailTextView);
        FullName = findViewById(R.id.fullNameTextView);
        username = findViewById(R.id.usernameTextView);

        //TODO: Not implemented edit profile page
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(userprofile.this, editprofile.class);
//                startActivity(intent);
//            }
//        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userprofile.this, MainActivity.class);
                beachBluenoserAuth.signOut();
                startActivity(intent);
            }
        });
        String userID = beachBluenoserAuth.getCurrentUser().getUid();
        DocumentReference Ref = db.collection("BBUsers").document(userID);
        Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String email = documentSnapshot.getData().get("Email").toString();
                        Email.setText(email);
                        String name = documentSnapshot.getData().get("Fullname").toString();
                        FullName.setText(name);
                        String user = documentSnapshot.getData().get("Username").toString();
                        username.setText(user);
                    }
                }
            }
        });

    }
}