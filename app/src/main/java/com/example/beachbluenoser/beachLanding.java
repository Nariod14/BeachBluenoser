package com.example.beachbluenoser;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class beachLanding extends AppCompatActivity {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String beachName;
    public String landingBeachCapacityText;
    public String landingBeachSandyOrRockyValue;
    public String landingBeachWheelChairRampValue;
    public String landingBeachImageSource;
    public String landingBeachVisualWaterConditionsText;
    
    public ImageView landingBeachImageView;
    public TextView landingBeachCapacityView;
    public TextView landingBeachSandyOrRockyView;
    public TextView landingBeachWheelChairRampView;
    public TextView landingBeachNameView;
    public TextView landingBeachVisualWaterConditionsView;
    public String currentDate;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beach_landing);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString("beachName")!=null) {
                beachName = bundle.getString("beachName");
            }
        }

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDate = formattedDate;

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

    @Override
    protected void onResume() {
        super.onResume();
        getPreliminaryDataFromDB();
    }


    private void getPreliminaryDataFromDB(){
        DocumentReference landingBeachRef = db.collection("beach").document(beachName);

        landingBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object DataImage  = document.getData().get("image");
                        String DataImageValue;
                        if(DataImage == null){
                            DataImageValue = "imageNotFound";
                        }else {
                            DataImageValue = document.getData().get("image").toString();
                        }
                        landingBeachImageSource = DataImageValue;
                        }if(!(document.getData().get("beachCapacityTextForTheDay")==null)) {
                            landingBeachCapacityText = document.getData().get("beachCapacityTextForTheDay").toString();
                        }else{
                            landingBeachCapacityText="Beach Capacity: No data today!";
                        }
                        if(!(document.getData().get("beachVisualWaveConditionsTextForTheDay")==null)) {
                            landingBeachVisualWaterConditionsText = document.getData().get("beachVisualWaveConditionsTextForTheDay").toString();
                        }else{
                            landingBeachVisualWaterConditionsText ="Water Conditions: No data today!";
                        }

                        showDataOnUI();

                    } else {
                        Log.d("Beach Landing Query", "No such document");
                    }
            }
        });
    }

    private void showDataOnUI(){
        landingBeachCapacityView = findViewById(R.id.landingBeachCapacityTextView);
        landingBeachSandyOrRockyView = findViewById(R.id.landingBeachSandyOrRockyTextView);
        landingBeachWheelChairRampView = findViewById(R.id.landingBeachWheelChairRampTextView);
        landingBeachNameView = findViewById(R.id.landingBeachNameTextView);
        landingBeachVisualWaterConditionsView = findViewById(R.id.landingBeachVisualWaterConditionsTextView);

        landingBeachCapacityView.setText(landingBeachCapacityText);
        landingBeachVisualWaterConditionsView.setText(landingBeachVisualWaterConditionsText);
        landingBeachSandyOrRockyView.setText(landingBeachSandyOrRockyValue);
        landingBeachWheelChairRampView.setText(landingBeachWheelChairRampValue);
        landingBeachNameView.setText(beachName);
        setBeachImage();

    }

    public void setBeachImage(){

        if(landingBeachImageSource.equals("")|| landingBeachImageSource == null){
            landingBeachImageSource ="default1.jpg";
        }
        landingBeachImageSource = landingBeachImageSource.replace('-','_');
        int fileExtension = landingBeachImageSource.indexOf('.');

        landingBeachImageSource = landingBeachImageSource.substring(0,fileExtension);
        String uri = "@drawable/"+landingBeachImageSource;
        Log.d("SetImage"," this is the file path: "+uri);
        int fileID =0;

        try{
            fileID = R.drawable.class.getField(landingBeachImageSource).getInt(null);
        }catch(IllegalAccessException e){
            Log.d("getImageIDError","Error getting image");
        }catch(NoSuchFieldException e2){
            Log.d("getImageIDError","no Icon found");
        }
        landingBeachImageView = findViewById(R.id.landingBeachImageView);
        landingBeachImageView.setImageResource(fileID);

    }

}
