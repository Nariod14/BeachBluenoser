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

    public int calmWatersCount=0;
    public int mediumWatersCount=0;
    public int roughWatersCount=0;
    public int lowCapacityCount=0;
    public int mediumCapacityCount=0;
    public int highCapacityCount=0;

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
                Log.d("beach Main Page NAme ", " Name : " + beachName);
            }
        }

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDate = formattedDate;
//



       // spinnerSetup();
       // showDataOnUI();

        Button btn = (Button)findViewById(R.id.checkInSurvey);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(beachLanding.this,LifeguardDataSurvey.class);
                intent.putExtra("beachName",beachName);

                startActivity(intent);
            }
        });

        onRestart();

    }
    @Override
    protected void onRestart() {

        super.onRestart();
        //this.onCreate(null);

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
                        //String landingBeachVisualWaterConditionsText;
                       // String landingBeachCapacityText;
                        if(DataImage == null){
                            DataImageValue = "imageNotFound";
                        }else {
                            DataImageValue = document.getData().get("image").toString();
                        }
                        landingBeachImageSource = DataImageValue;
                        if(document.get("beachCapacityTextForTheDay")!=null){
                            landingBeachCapacityText = document.get("beachCapacityTextForTheDay").toString();
                        }
                        if(document.get("beachVisualWaveConditionsTextForTheDay")!=null){
                            landingBeachVisualWaterConditionsText = document.get("beachVisualWaveConditionsTextForTheDay").toString();
                        }


                        Log.d("setCapText","capacityText: "+landingBeachCapacityText);
                        Log.d("setVWCText","waterconText: "+landingBeachVisualWaterConditionsText);

                        showDataOnUI();
                     //   getRemainingDataFromDB();

                    } else {
                        Log.d("Beach Landing Query", "No such document");

                    }
                } else {
                    Log.d("Beach Landing Query", "get failed with ", task.getException());
                }
            }
        });

    }
    private void getRemainingDataFromDB(){
        DocumentReference landingBeachRef = db.collection("survey").document(currentDate).collection(beachName).document(currentDate);
        landingBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                         if(!(document.getData().get("Calm waters")==null))
                         calmWatersCount  = Integer.parseInt(document.getData().get("Calm waters").toString());
                         if(!(document.getData().get("Medium waters")==null))
                         mediumWatersCount  = Integer.parseInt(document.getData().get("Medium waters").toString());
                         if(!(document.getData().get("Rough waters")==null))
                         roughWatersCount  = Integer.parseInt(document.getData().get("Rough waters").toString());
                         if(!(document.getData().get("Low Capacity")==null))
                         lowCapacityCount  = Integer.parseInt(document.getData().get("Low Capacity").toString());
                         if(!(document.getData().get("Medium Capacity")==null))
                         mediumCapacityCount   = Integer.parseInt(document.getData().get("Medium Capacity").toString());
                         if(!(document.getData().get("High Capacity")==null))
                         highCapacityCount  = Integer.parseInt(document.getData().get("High Capacity").toString());
//
                         showDataOnUI();
                    } else {
                        Log.d("Beach Landing Query", "No such document");
                        showDataOnUI();
                    }
                } else {
                    Log.d("Beach Landing Query", "get failed with ", task.getException());
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

        Log.d("what22","calm: "+calmWatersCount + " medium: "+ mediumWatersCount+ " r: "+roughWatersCount);
        Log.d("what22","calm: "+lowCapacityCount + " medium: "+ mediumCapacityCount+ " r: "+highCapacityCount);



        Log.d("setCapText222","capacityText: "+landingBeachCapacityText);
        Log.d("setVWCText222","waterconText: "+landingBeachVisualWaterConditionsText);

        //FIX: could have just made it a string value instead of this then print at end
//
//        if(calmWatersCount > mediumWatersCount && calmWatersCount > roughWatersCount){
//            landingBeachVisualWaterConditionsText = "Visual water conditions: Calm Waters";
//
//        }
//        else if(mediumWatersCount >= calmWatersCount && mediumWatersCount >= roughWatersCount){
//            landingBeachVisualWaterConditionsText = "Visual water conditions: Medium Waters";
//        }
//        else if(roughWatersCount >= calmWatersCount && roughWatersCount >= mediumWatersCount){
//            landingBeachVisualWaterConditionsText = "Visual water conditions: Rough Waters";
//        }
//
//
//        landingBeachVisualWaterConditionsView.setText(landingBeachVisualWaterConditionsText);
//
//
//        if(lowCapacityCount > mediumCapacityCount && lowCapacityCount > highCapacityCount){
//            landingBeachCapacityText = "Beach Capacity: Low Capacity";
//        }
//        else if(mediumCapacityCount >= lowCapacityCount && mediumCapacityCount >= highCapacityCount){
//            landingBeachCapacityText = "Beach Capacity: Medium Capacity";
//        }
//        else if(highCapacityCount >= lowCapacityCount && highCapacityCount >= mediumCapacityCount){
//            landingBeachCapacityText = "Beach Capacity: High Capacity";
//        }
//
//
//        if(lowCapacityCount ==0 && mediumCapacityCount ==0 && highCapacityCount==0){
//            landingBeachCapacityText = "Beach Capacity: No data today!";
//        }
//        if(calmWatersCount ==0 && mediumWatersCount ==0 && roughWatersCount==0){
//            landingBeachVisualWaterConditionsText = "Visual Water Conditions: No data today!";
//
//        }
        landingBeachCapacityView.setText(landingBeachCapacityText);
        landingBeachVisualWaterConditionsView.setText(landingBeachVisualWaterConditionsText);
        landingBeachSandyOrRockyView.setText(landingBeachSandyOrRockyValue);
        landingBeachWheelChairRampView.setText(landingBeachWheelChairRampValue);
        landingBeachNameView.setText(beachName);
        setBeachImage();



    }

    public void setBeachImage(){
        //Log.d("setBeachImage3333","Val 1: "+beachImageFileName);



        //Log.d("IMAGENAME: ","name : "+ beaches.get(pos).getImageSource());
        // beachImage.setImageResource(R.drawable.theetcher);
        //     beachImage.setImageURI("path/");
        // beachImage.setBackground(R.drawable.beachmeadows_beach);
        if(landingBeachImageSource.equals("")|| landingBeachImageSource == null){
            landingBeachImageSource ="default1.jpg";
        }
        landingBeachImageSource = landingBeachImageSource.replace('-','_');
        int fileExtension = landingBeachImageSource.indexOf('.');

        Log.d("SetImage"," file before parse "+landingBeachImageSource);
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
        //((ImageView)) findViewById(R.id.BeachImage).setImageResource(fileID));
        landingBeachImageView.setImageResource(fileID);



        //int imageResouce = mainView.getResources().getIdentifier(uri,null,mainView.getActivty().getPackageName());
        // Drawable res = mainView.getResources().getDrawable(imageResouce);

    }

}
