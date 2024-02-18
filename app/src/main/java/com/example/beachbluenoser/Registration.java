package com.example.beachbluenoser;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;


;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class Registration extends AppCompatActivity {

    EditText userName, passwordField, fullName, emailAddress;
    FirebaseFirestore beachBluenoserDB;
    private FirebaseAuth beachBluenoserAuth;
    Button lifeguardBtn;
    Button registerBtn;
    String username, email, fullname, password, userID;
    ImageButton backArrowkey;;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        userName = findViewById(R.id.registerUsernameTxt);
        passwordField = findViewById(R.id.registerPasswordTxt);
        emailAddress = findViewById(R.id.registerEmailAddressTxt);
        fullName = findViewById(R.id.registerFullNameTxt);
        registerBtn = findViewById(R.id.signUpBtn);
        lifeguardBtn = findViewById(R.id.lifeguardView);
        backArrowkey = findViewById(R.id.backArrow);

        beachBluenoserDB = FirebaseFirestore.getInstance();
        beachBluenoserAuth = FirebaseAuth.getInstance();

        mp = MediaPlayer.create(this, R.raw.click);

        backArrowkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(Registration.this,Login.class);
                startActivity(intent);
            }
        });

        //Toggle switch for lifeguard that will take you to the LifeguardRegistration.java
        lifeguardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(Registration.this, LifeguardRegistration.class);
                startActivity(intent);
            }
        });

                registerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mp.start();
                        username = userName.getText().toString();
                        fullname = fullName.getText().toString();
                        email = emailAddress.getText().toString().trim();
                        password = passwordField.getText().toString().trim();

                        if (TextUtils.isEmpty(username)) {
                            userName.setError("Please Enter a Username!");
                        } else if (username.contains("!") || username.contains("#") || username.contains("$") || username.contains("%") || username.contains("&") || username.contains("'") || username.contains("(") || username.contains(")") || username.contains("*") || username.contains("+") || username.contains(",") || username.contains("-") || username.contains(".") || username.contains("/") || username.contains(":") || username.contains(";") || username.contains("<") || username.contains("=") || username.contains(">") || username.contains("?") || username.contains("@") || username.contains("[") || username.contains("]") || username.contains("^") || username.contains("_") || username.contains("`") || username.contains("{") || username.contains("|") || username.contains("}") || username.contains("~")) {
                            userName.setError("Special symbols are not allowed!");
                        } else if (username.matches("[0-9]+")) {
                            userName.setError("Numbers are not allowed!");
                        }

                        if (TextUtils.isEmpty(email)) {
                            emailAddress.setError("Please Enter an Email!");
                            return;
                        } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2,3}+")) {
                            emailAddress.setError("Email is Invalid.");
                            return;
                        }
                        if (TextUtils.isEmpty(password)) {
                            passwordField.setError("Please Enter a Password!");
                            return;
                        } else if (!(password.length() >= 8) && (!password.matches("[a-zA-Z0-9._-]"))) {
                            passwordField.setError("Password needs to be more than 8 characters and a mix of alphabets and numbers!");
                            return;
                        }

                        char[] passwordChar = new char[password.length()];

                        for (int i = 0; i < password.length(); i++) {
                            passwordChar[i] = password.charAt(i);
                        }

                        String salty = getNextSalt();
                        String hashedPassword = hash(passwordChar, salty);

                        beachBluenoserAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Registration.this, "User Created.", Toast.LENGTH_SHORT).show();
                                userID = Objects.requireNonNull(beachBluenoserAuth.getCurrentUser()).getUid();
                                DocumentReference documentReference = beachBluenoserDB.collection("BBUSERSTABLE-PROD").document(userID);

/*
                        User user = new User(username,fullname,email,hashedPassword);
*/

                                Map<String, Object> user = new HashMap<>();
                                user.put("Fullname", fullname);
                                user.put("Email", email);
                                user.put("Username", username);
                                user.put("Password", hashedPassword);
                                user.put("userType", "User");

                                Log.d(TAG, "onSuccess: hashedpasswordResult " + hashedPassword);


                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                    }
                                });
                                startActivity(new Intent(Registration.this, MainActivity.class));
                            } else {
                                Toast.makeText(Registration.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show(); //example - It will show an error if email already exists
                            }
                        });
                    }
                });
    }

    public static String getNextSalt() {
        byte[] salt = new byte[16];
        Random RANDOM = new SecureRandom();
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);

    }

    public static String hash(char[] password, String salt) {
        Log.d(TAG,"onSuccess: SaltCheck " + salt );
        PBEKeySpec spec = new PBEKeySpec(password, Base64.getDecoder().decode(salt), 10000, 256);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return Base64.getEncoder().encodeToString(skf.generateSecret(spec).getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }
}