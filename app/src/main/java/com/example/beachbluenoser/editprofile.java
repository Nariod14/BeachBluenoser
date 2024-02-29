package com.example.beachbluenoser;

import static com.example.beachbluenoser.Registration.getNextSalt;
import static com.example.beachbluenoser.Registration.hash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;




public class editprofile extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText name;
    EditText Email;
    EditText password;
    EditText Pass;
    Button Save;
    ImageButton backArrowkey;
    private FirebaseAuth beachBluenoserAuth;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);

        name = findViewById(R.id.EditName);
        Email = findViewById(R.id.EditEmail);
        password = findViewById(R.id.EditPassword);
        password.setTransformationMethod(new PasswordTransformationMethod());
        Pass = findViewById(R.id.FullName);
        Save = findViewById(R.id.save);
        backArrowkey = findViewById(R.id.backArrow);

        beachBluenoserAuth = FirebaseAuth.getInstance();
        backArrowkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(editprofile.this,userprofile.class);
                startActivity(intent);
            }
        });
        //Make reference to data on Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("BBUSERSTABLE-PROD").document(userId);
        //Using Snapshot to display data
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            //Get real time updates with Firestore
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (snapshot.exists()) {
                    // Retrieve user data using documentSnapshot
                    name.setText(snapshot.getString("Username"));
                    Pass.setText(snapshot.getString("Fullname"));
                    Email.setText(snapshot.getString("Email"));
                }
            }
        });

        //Modified
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Pass.getText() == null){
                    openDialog();
                }else {
                    String NAME = name.getText().toString();
                    String fullName = Pass.getText().toString();
                    String email = Email.getText().toString();
                    String Password = password.getText().toString();

                    //No blank allowed
                    if (TextUtils.isEmpty(NAME) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(Password)) {
                        // Show error message or dialog indicating fields cannot be blank
                        Toast.makeText(editprofile.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    }
                    //Invalid email address
                    else if (!email.contains("@")){
                        Toast.makeText(editprofile.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //Authenticate user
                        beachBluenoserAuth.signInWithEmailAndPassword(email, Password)
                                .addOnCompleteListener(editprofile.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //Password correct, proceed to upload
                                            User user = new User(NAME, fullName, email, Password);
                                            Upload(user);
                                            startActivity(new Intent(editprofile.this, userprofile.class));
                                            Toast.makeText(editprofile.this, "Profile Updated", Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(editprofile.this, "Incorrect Password", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                    }
                }
            }
        });
    }

    public void openDialog(){
        editdialog dialog = new editdialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }
    public void Upload(User user){
        //db.collection("users").add(user);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference Ref = db.collection("BBUSERSTABLE-PROD").document(userId);
        //Update to new data
        Ref.update(
            "Username",user.getUsername(),
                "Fullname",user.getFullName(),
                "Email",user.getEmail(),
                "Password",user.getPassword());
    }


}