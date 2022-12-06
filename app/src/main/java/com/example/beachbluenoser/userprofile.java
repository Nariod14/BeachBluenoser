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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

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
        DocumentReference Ref = db.collection("users").document("GrNKyKNkaKMagsInywkYSyA2s5m1");
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