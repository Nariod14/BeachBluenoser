package com.example.beachbluenoser;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LifeguardRegistration extends AppCompatActivity {

    EditText emailAdd, accessToken;
    Button backBtn, registerBtn;

    String email, AccToken, lgID, email12;

    private int tempp;
    FirebaseFirestore beachBluenoserDB, beachBluenoserDBB;
    private FirebaseAuth beachBluenoserAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifeguard_registration_page);

        emailAdd = findViewById(R.id.registerLifeguardEmail);
        accessToken = findViewById(R.id.editAccessToken);
        backBtn = findViewById(R.id.backButton);
        registerBtn = findViewById(R.id.registerBtn);

        beachBluenoserDB = FirebaseFirestore.getInstance();
        beachBluenoserDBB = FirebaseFirestore.getInstance();
        beachBluenoserAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LifeguardRegistration.this, Registration.class);
                startActivity(intent);

            }
        });

        //email = emailAdd.getText().toString().trim();
        //AccToken = accessToken.getText().toString().trim();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailAdd.getText().toString().trim();
                AccToken = accessToken.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emailAdd.setError("Please Enter an Email!");
                    return;
                }else if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2,3}+")){
                    emailAdd.setError("Email is Invalid.");
                    return;
                }


                if(TextUtils.isEmpty(AccToken)){
                    accessToken.setError("Please Enter an Access Token!");
                    return;
                }

                beachBluenoserDBB.collection("AccessToken").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (e !=null)
                        {

                        }

                        for (DocumentChange documentChange : documentSnapshots.getDocumentChanges())
                        {
                            int temp = 0;
                            String   isAttendance =  documentChange.getDocument().get("Token").toString();
                            //Log.d(TAG,"Test" + isAttendance );
                            Log.d(TAG,"Test " + AccToken );
                            if(isAttendance.equals(AccToken))
                            {
                                temp=1;
                                Toast.makeText(LifeguardRegistration.this, "Lifeguard Loged IN.", Toast.LENGTH_SHORT).show();
                                lgID = UUID.randomUUID().toString();
                                boolean temp1 = checkEmail(email);
                                //Log.d(TAG,"Test4 " + temp1 );
                                boolean temp2 = true;
                                DocumentReference documentReference = beachBluenoserDB.collection("Lifeguard").document(lgID);

                                Map<String, Object> user = new HashMap<>();
                                //user.put("Fullname", fullname);
                                if(Boolean.compare(temp1, temp2) == 0) {
                                    user.put("Email", email);
                                }
                                else {
                                    break;
                                }
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: user Profile is created for " + lgID);
                                        }
                                    });
                                    startActivity(new Intent(LifeguardRegistration.this, MainActivity.class));

                            }
                            if(temp==0){
                                Toast.makeText(LifeguardRegistration.this, "Invalid Token", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                });


            }
        });


    }

    private boolean checkEmail(String email) {
        //final int[] temp = {0};
        //email12 = emailAdd.getText().toString().trim();
        String email12 = email;
//        beachBluenoserDB.collection("Lifeguard").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                if (error !=null)
//                {
//
//                }
//
//                for (DocumentChange documentChange : documentSnapshot.getDocumentChanges())
//                {
//                    String isEmail =  documentChange.getDocument().get("Email").toString();
//                    Log.d(TAG,"Test3 " + email12);
//                    if(isEmail.equals(email12)) {
//                        Log.d(TAG,"Test2 " + isEmail);
//                        tempp = 1;
//                        Log.d(TAG,"Test13 " + tempp);
//                        //Toast.makeText(LifeguardRegistration.this, "Email exists.", Toast.LENGTH_SHORT).show();
//                        break;
//                    }
//                }
//            }
//        });

        beachBluenoserDB.collection("Lifeguard")
                .whereEqualTo("Email", email12)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(TAG, document.getId() + " => " + document.getData());
//                    }
                    tempp = 1;
                    Log.d(TAG,"Test22 " + tempp);
                }
                else {
                    tempp = 0;
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });

        Log.d(TAG,"Test11 " + tempp);
        if(tempp == 1){
            Log.d(TAG,"Test123 " + tempp);
            Toast.makeText(LifeguardRegistration.this, "Email exists.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }

    }


}
