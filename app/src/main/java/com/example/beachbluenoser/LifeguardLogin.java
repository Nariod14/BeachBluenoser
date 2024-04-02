package com.example.beachbluenoser;

import android.accounts.AccountManagerFuture;
import android.util.Log;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LifeguardLogin extends AppCompatActivity {

    EditText emailAddress;
    EditText passwordField;
    Button userLogin;
    ImageButton rtnHome;
    private FirebaseAuth beachBluenoserAuth;
    String emailAuth;
    String passAuth;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_lifeguard);

        userLogin = findViewById(R.id.loginBtn);
        rtnHome = findViewById(R.id.returnHomeButton);
        mp = MediaPlayer.create(this, R.raw.click);

        beachBluenoserAuth = FirebaseAuth.getInstance();
        /*if (beachBluenoserAuth.getCurrentUser() != null) {
            finish();
            return;
        }*/

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                authenticateUser();

            }
        });


        rtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(LifeguardLogin.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void authenticateUser() {
        emailAddress = findViewById(R.id.emailLogin);
        passwordField = findViewById(R.id.passwordLogin);

        emailAuth = emailAddress.getText().toString().trim();
        //Token in the name of "password"
        passAuth = passwordField.getText().toString().trim();

        if (emailAuth.isEmpty() || passAuth.isEmpty()) {
            Toast.makeText(LifeguardLogin.this, "Please enter a valid email address and password", Toast.LENGTH_LONG).show();
        }
        else {

            checkIfEmailExists(emailAuth);
        }
    }
    private void checkIfEmailExists(String email) {
        FirebaseFirestore.getInstance().collection("Lifeguard")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Email exists in the database
                            signInUser(email, passAuth);
                        } else {
                            // Email does not exist in the database
                            showSnackbar("The entered email does not exist. Either enter the correct email address or create a new account by navigating to the registration page");
                        }
                    } else {
                        // Error occurred while checking email existence
                        Toast.makeText(LifeguardLogin.this, "Error checking email existence", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setTextMaxLines(48)

                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });



        // CODE TO ADJUST TEXT SIXE AND SNACKBAR SIZE
        TextView snackbarTextView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarTextView.setMaxLines(20);
        snackbarTextView.setPadding(60, 40, 37, 36); // Adjust padding as needed

        snackbar.show();
    }

    private void signInUser(String email, String password) {

        beachBluenoserAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        showMainActivity();
                    } else {
                        Log.e("LifeguardLogin", "Authentication Failed!", task.getException());
                        Toast.makeText(LifeguardLogin.this, "Authentication Failed!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showMainActivity() {
        Intent intent = new Intent(LifeguardLogin.this,MainActivity.class);
        startActivity(intent);
        Toast.makeText(LifeguardLogin.this, "Authentication was successful", Toast.LENGTH_LONG).show();
    }
}
