package com.example.beachbluenoser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class userprofile extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button edit;
    TextView Email, username, FullName;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        edit = findViewById(R.id.Edit);
        Email = findViewById(R.id.Email);
        FullName = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userprofile.this, editprofile.class);
                startActivity(intent);
            }
        });
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("users");
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = String.valueOf(dataSnapshot.child("Email").getValue());
                Email.setText(email);
                String fullname = String.valueOf(dataSnapshot.child("Fullname").getValue());
                FullName.setText(fullname);
                String user = String.valueOf(dataSnapshot.child("Username").getValue());
                username.setText(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}