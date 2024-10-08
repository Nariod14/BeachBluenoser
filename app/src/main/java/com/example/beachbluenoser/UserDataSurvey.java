package com.example.beachbluenoser;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
import android.widget.Toast;


public class UserDataSurvey extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String parkingValue;
    public String beachName;
    public String currentDate;
    public String beachCapacityValue;
    public TextView name;
    public String surveyParkingTextForTheDay;
    public String surveyCapacityTextForTheDay;
    public long currentParkingValue;
    public long currentBeachCapacityValue;

    public int lowParkingCount = 0;
    public int mediumParkingCount = 0;
    public int highParkingCount = 0;
    public int lowCapacityCount = 0;
    public int mediumCapacityCount = 0;
    public int highCapacityCount = 0;
    ImageButton backArrowkey;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;


    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_survey);

        requestLocationPermission();

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDate = formattedDate;

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString("beachName")!=null) {
                beachName = bundle.getString("beachName");
            }
        }

        name = findViewById(R.id.surveyTitle);
        name.setText(beachName);

        Spinner parkingConditionSpinner = findViewById(R.id.parkingConditionsSpinner);
        ArrayAdapter<CharSequence> adapterParkingSpinner = ArrayAdapter.createFromResource(this,R.array.parkingSpotValues, android.R.layout.simple_spinner_item);
        adapterParkingSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parkingConditionSpinner.setAdapter(adapterParkingSpinner);
        parkingConditionSpinner.setOnItemSelectedListener(this);

        Spinner UserbeachCapacitySpinner = findViewById(R.id.userCapacitySpinner);
        ArrayAdapter<CharSequence> adapterCapacity = ArrayAdapter.createFromResource(this,R.array.beachCapacityValues, android.R.layout.simple_spinner_item);
        adapterCapacity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        UserbeachCapacitySpinner.setAdapter(adapterCapacity);
        UserbeachCapacitySpinner.setOnItemSelectedListener(this);

        Button btn = findViewById(R.id.userSubmitButton);

        backArrowkey = findViewById(R.id.backArrow);
        backArrowkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDataSurvey.this,beachLanding.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("HELLOTEST","JELLOTEST");

                parkingValue = parkingConditionSpinner.getSelectedItem().toString();
                beachCapacityValue = UserbeachCapacitySpinner.getSelectedItem().toString();
                Log.d("Values", parkingValue +" "+beachCapacityValue);
                getCurrentValues();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(UserDataSurvey.this,beachLanding.class);
                        intent.putExtra("beachName",beachName);
                        startActivity(intent);
                    }
                }, 10);
            }
        });


    }

    private void requestLocationPermission() {
        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted
            // You can start using coarse location
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the request code matches our request
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can start using coarse location

                //DEVS FOR NEXT SEMESTER THIS IS WHERE YOUR LOCATION CODE SHOULD GO
            } else {
                // Permission denied
                Intent deniedIntent = new Intent(UserDataSurvey.this, beachLanding.class);
                startActivity(deniedIntent);
                Toast.makeText(UserDataSurvey.this, "Access to checkin page removed on two location request denials. Change in android settings.", Toast.LENGTH_LONG).show();



            }
        }
    }

    public void getCurrentValues(){
        DocumentReference surveyBeachRef = db.collection("survey").document(currentDate).collection(beachName).document(currentDate);

        surveyBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object VWCObject = document.getData().get(parkingValue);
                        if(VWCObject==null){

                            Log.d("isnull","isNull");
                            currentParkingValue = 0;
                        }else{
                            currentParkingValue = (long)document.getData().get(parkingValue);
                        }
                        Object BCObject = document.getData().get(beachCapacityValue);

                        if(BCObject==null){
                            Log.d("isnull","isNull");

                            currentBeachCapacityValue = 0;
                        }else{
                            currentBeachCapacityValue = (long)document.getData().get(beachCapacityValue);
                        }
                        Log.d("ValsCurrent","Current: "+currentBeachCapacityValue + " "+ currentParkingValue);

                        if(!(document.getData().get("Many spots")==null))
                            lowParkingCount  = Integer.parseInt(document.getData().get("Many spots").toString());
                        if(!(document.getData().get("Few spots")==null))
                            mediumParkingCount  = Integer.parseInt(document.getData().get("Few spots").toString());
                        if(!(document.getData().get("Little/No Spots")==null))
                            highParkingCount  = Integer.parseInt(document.getData().get("Little/No Spots").toString());
                        if(!(document.getData().get("Low Capacity")==null))
                            lowCapacityCount  = Integer.parseInt(document.getData().get("Low Capacity").toString());
                        if(!(document.getData().get("Medium Capacity")==null))
                            mediumCapacityCount   = Integer.parseInt(document.getData().get("Medium Capacity").toString());
                        if(!(document.getData().get("High Capacity")==null))
                            highCapacityCount  = Integer.parseInt(document.getData().get("High Capacity").toString());
                        writeDataToDB();

                    } else {
                        Log.d("getCurrentSurveyData", "No such document");
                        writeDataToDB();
                    }
                } else {
                    Log.d("getCurrentSurveyData", "get failed with ", task.getException());
                }
            }
        });
    }

    public void writeDataToDB(){
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Log.d("TIME222","CUR TIME:"+formattedDate+";");

        Map<String, Object> survey = new HashMap<>();
        Map<String, Object> survey2 = new HashMap<>();
        Log.d("currentVals",currentBeachCapacityValue + " d: "+ currentParkingValue);
        currentBeachCapacityValue++;
        currentParkingValue++;
        Log.d("currentValsPost",currentBeachCapacityValue + " 2: "+ currentParkingValue);
        Log.d("visualWaters", parkingValue + " capacityValue "+beachCapacityValue);
        if(parkingValue.equals("Many spots"))
            lowParkingCount++;
        if(parkingValue.equals("Few spots"))
            mediumParkingCount++;
        if(parkingValue.equals("Little/No Spots"))
            highParkingCount++;
        if(beachCapacityValue.equals("Low Capacity"))
            lowCapacityCount++;
        if(beachCapacityValue.equals("Medium Capacity"))
            mediumCapacityCount++;
        if(beachCapacityValue.equals("High Capacity"))
            highCapacityCount++;

        Log.d("watersCountHere","parkL: "+lowParkingCount+" parkMed: "+mediumParkingCount +" parkHi: "+ highParkingCount);
        Log.d("capCountHere","capL: "+lowCapacityCount+" capMed: "+mediumCapacityCount +" capHigh: "+highCapacityCount);

        setCapacityAndVisualConditionText();

        survey.put(parkingValue, currentParkingValue);
        survey.put(beachCapacityValue, currentBeachCapacityValue);

        survey2.put("beachCapacityTextForTheDay", surveyCapacityTextForTheDay);
        survey2.put("beachParkingConForDay", surveyParkingTextForTheDay);

        survey.put("date", formattedDate);
        Map<String, Object> emptyVal = new HashMap<>();
        emptyVal.put("emptyField","EmptyVal");

        DocumentReference surveyEmptyField = db.collection("survey").document(currentDate);
        surveyEmptyField.set(emptyVal,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UserSurveyWrite", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UserSurveyWrite", "Error writing document", e);
                    }
                });
        DocumentReference surveyBeachRef = db.collection("survey").document(formattedDate).collection(beachName).document(formattedDate);

        surveyBeachRef.set(survey,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UserSurveyWrite", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UserSurveyWrite", "Error writing document", e);
                    }
                });

        db.collection("beach").document(beachName)
                .set(survey2,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UserSurveyWrite", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UserSurveyWrite", "Error writing document", e);
                    }
                });

    }
    public void setCapacityAndVisualConditionText(){
        //Capacity
        if(lowCapacityCount > mediumCapacityCount && lowCapacityCount > highCapacityCount){
            surveyCapacityTextForTheDay = "Beach Capacity: Low Capacity";
        }
        else if(mediumCapacityCount >= lowCapacityCount && mediumCapacityCount >= highCapacityCount){
            surveyCapacityTextForTheDay = "Beach Capacity: Medium Capacity";
        }
        else if(highCapacityCount > lowCapacityCount && highCapacityCount > mediumCapacityCount){
            surveyCapacityTextForTheDay = "Beach Capacity: High Capacity";
        }
        else if(lowCapacityCount ==0 && mediumCapacityCount ==0 && highCapacityCount==0){
            surveyCapacityTextForTheDay = "Beach Capacity: No data today!";
        }
        // All counts are equal
        else {
            surveyCapacityTextForTheDay = "Beach Capacity: Medium Capacity";
        }
        //Parking
        if(lowParkingCount > mediumParkingCount && lowParkingCount > highParkingCount){
            surveyParkingTextForTheDay = "Parking Availability: Many Spots";
        }
        else if(mediumParkingCount >= lowParkingCount && mediumParkingCount >= highParkingCount){
            surveyParkingTextForTheDay = "Parking Availability: Few Spots";
        }
        else if(highParkingCount > mediumParkingCount && highParkingCount > lowParkingCount){
            surveyParkingTextForTheDay = "Parking Availability: Little/No Spots";
        }
        else if(lowParkingCount ==0 && mediumParkingCount ==0 && highParkingCount==0){
            surveyParkingTextForTheDay = "Parking Availability: No data today!";
        }
        // All counts are equal
        else {
            surveyParkingTextForTheDay = "Parking Availability: Few Spots";
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long length) {
        String selectedValue = parent.getItemAtPosition(position).toString();
        parkingValue = selectedValue;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}
