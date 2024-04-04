package com.example.beachbluenoser;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText emailAddress;
    EditText passwordField;
    Button userLogin;
    Button signUp;
    Button forgotPassword;
    ImageButton rtnHome;
    private FirebaseAuth beachBluenoserAuth;
    String emailAuth;
    String passAuth;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userLogin = findViewById(R.id.loginBtn);
        signUp = findViewById(R.id.signupBtn);
        forgotPassword = findViewById(R.id.forgotPasswordBtn);
        rtnHome = findViewById(R.id.returnHomeButton);

        mp = MediaPlayer.create(this, R.raw.click);

        beachBluenoserAuth = FirebaseAuth.getInstance();
        /*if (beachBluenoserAuth.getCurrentUser() != null) {
            finish();
            return;
        }*/
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(Login.this, PasswordReset.class);
                startActivity(intent);

            }
        });
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                authenticateUser();

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        rtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * Gets emails and passwords and checks to see if the email and password fields have input in them
     *
     */
    private void authenticateUser() {
        emailAddress = findViewById(R.id.emailLogin);
        passwordField = findViewById(R.id.passwordLogin);

        emailAuth = emailAddress.getText().toString().trim();
        passAuth = passwordField.getText().toString().trim();

        if (emailAuth.isEmpty() || passAuth.isEmpty()) {
            Toast.makeText(Login.this, "Please enter a valid email address and password", Toast.LENGTH_LONG).show();
        }
        else {

            checkIfEmailExists(emailAuth);
        }
    }
    /**
     * Checks to see if an email exists on the Bluenoser Firebase Database
     * @param  email the email input by the user
     */

    private void checkIfEmailExists(String email) {
        FirebaseFirestore.getInstance().collection("BBUSERSTABLE-PROD")
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
                        Toast.makeText(Login.this, "Error checking email existence", Toast.LENGTH_LONG).show();
                    }
                });
    }
    /**
     * Uses a snackbar to display the message if the user email does not exsist
     * @param  message the message that will be displayed if the email does not exsist
     */

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

    /**
     * If users email exists this method will sign In a user with their email and password
     * @param  email the email rpovided by the user
     * @param password the password provided by the user
     */

    private void signInUser(String email, String password) {
        beachBluenoserAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        showMainActivity();
                    } else {
                        Toast.makeText(Login.this, "Authentication Failed!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showMainActivity() {
        Intent intent = new Intent(Login.this,MainActivity.class);
        startActivity(intent);
        Toast.makeText(Login.this, "Authentication was successful", Toast.LENGTH_LONG).show();
    }
}
