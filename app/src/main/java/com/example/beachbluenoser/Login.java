package com.example.beachbluenoser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class Login extends AppCompatActivity {

    EditText emailAddress, passwordField;
    Button login, signUp;
    ImageButton rtnHome;
    private FirebaseAuth beachBluenoserAuth;
    String emailAuth, passAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        login = findViewById(R.id.loginBtn);
        signUp = findViewById(R.id.signupBtn);
        rtnHome = findViewById(R.id.returnHomeButton);

        beachBluenoserAuth = FirebaseAuth.getInstance();
      /*  if (beachBluenoserAuth.getCurrentUser() != null) {
            finish();
            return;
        }
*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        rtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });




    }

    private void authenticateUser() {
        emailAddress = findViewById(R.id.emailLogin);
        passwordField = findViewById(R.id.passwordLogin);

        emailAuth = emailAddress.getText().toString().trim();
        passAuth = passwordField.getText().toString().trim();

        if (emailAuth.isEmpty() || passAuth.isEmpty()) {
            Toast.makeText(Login.this, "Please enter a valid email address and password", Toast.LENGTH_LONG).show();

        } else {
            beachBluenoserAuth.signInWithEmailAndPassword(emailAuth, passAuth)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                showMainActivity();
                            } else {
                                Toast.makeText(Login.this, "Authentication Failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    private void showMainActivity() {
        Intent intent = new Intent(Login.this,MainActivity.class);
        startActivity(intent);
        Toast.makeText(Login.this, "Authentication was successful", Toast.LENGTH_LONG).show();
    }
}