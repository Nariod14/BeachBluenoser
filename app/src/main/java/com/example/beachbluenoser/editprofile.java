package com.example.beachbluenoser;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

public class editprofile extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText name;
    EditText Email;
    EditText password;
    EditText Pass;
    Button Save;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);
        name = findViewById(R.id.EditName);
        Email = findViewById(R.id.EditEmail);
        password = findViewById(R.id.EditPassword);
        Pass = findViewById(R.id.FullName);
        Save = findViewById(R.id.save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Pass.getText() == null){
                    openDialog();
                }else {
//                    String NAME = name.getText().toString();
//                    String email = Email.getText().toString();
//                    String Password = password.getText().toString();
//                    User user = new User(NAME, email, Password);
//                    Upload(user);
//                    startActivity(new Intent(editprofile.this, userprofile.class));
                }
            }
        });
    }

    public void openDialog(){
        editdialog dialog = new editdialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }
    public void Upload(User user){
        db.collection("users").add(user);
    }
}