package com.example.beachbluenoser;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class beachLanding extends AppCompatActivity {

    public String beachName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beach_landing);


       // spinnerSetup();
        showDataOnUI();
        Button btn = (Button)findViewById(R.id.checkInSurvey);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(beachLanding.this,LifeguardDataSurvey.class);
                intent.putExtra("beachName",beachName);

                startActivity(intent);
            }
        });
    }

    private void showDataOnUI(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString("beachName")!=null) {
                beachName = bundle.getString("beachName");

                Log.d("beach Main Page NAme ", " Name : " + beachName);
            }
        }

    }

}
