package com.example.beachbluenoser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LifeguardRegistration extends AppCompatActivity {

    EditText emailAdd, accessToken;
    Button backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifeguard_registration_page);

        emailAdd = findViewById(R.id.registerLifeguardEmail);
        accessToken = findViewById(R.id.editAccessToken);
        backBtn = findViewById(R.id.backButton);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LifeguardRegistration.this, Registration.class);
                startActivity(intent);

            }
        });

}
}
