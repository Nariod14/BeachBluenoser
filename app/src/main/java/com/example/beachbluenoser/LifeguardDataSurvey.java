package com.example.beachbluenoser;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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

public class LifeguardDataSurvey extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String visualWaterConditionsValue;
    public String beachName;
    public String currentDate;
    public String beachCapacityValue;
    public TextView name;
    public String surveyVisualWaterConditionsTextForTheDay;
    public String surveyCapacityTextForTheDay;
    public long currentVisualWaterConditionsValue;
    public long currentBeachCapacityValue;

    public int calmWatersCount=0;
    public int mediumWatersCount=0;
    public int roughWatersCount=0;
    public int lowCapacityCount=0;
    public int mediumCapacityCount=0;
    public int highCapacityCount=0;
    ImageButton backArrowkey;;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lifeguard_data);

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
        backArrowkey = findViewById(R.id.backArrow);
        backArrowkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LifeguardDataSurvey.this,userprofile.class);
                startActivity(intent);
            }
        });

        Spinner visualWaveConditionSpinner = findViewById(R.id.visualWaterConditionsSpinner);
        ArrayAdapter<CharSequence> adapterVWCSpinner = ArrayAdapter.createFromResource(this,R.array.visualWaterConditionsValues, android.R.layout.simple_spinner_item);
        adapterVWCSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visualWaveConditionSpinner.setAdapter(adapterVWCSpinner);
        visualWaveConditionSpinner.setOnItemSelectedListener(this);

        Spinner beachCapacitySpinner = findViewById(R.id.lifeguardBeachCapacitySpinner);
        ArrayAdapter<CharSequence> adapterCapacity = ArrayAdapter.createFromResource(this,R.array.beachCapacityValues, android.R.layout.simple_spinner_item);
        adapterCapacity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        beachCapacitySpinner.setAdapter(adapterCapacity);
        beachCapacitySpinner.setOnItemSelectedListener(this);

        Button btn = findViewById(R.id.lifeGuardSurveyButton);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("HELLOTEST","JELLOTEST");

                 visualWaterConditionsValue = visualWaveConditionSpinner.getSelectedItem().toString();
                 beachCapacityValue = beachCapacitySpinner.getSelectedItem().toString();
                Log.d("Values",visualWaterConditionsValue+" "+beachCapacityValue);
                getCurrentValues();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LifeguardDataSurvey.this,beachLanding.class);
                        intent.putExtra("beachName",beachName);
                        startActivity(intent);
                    }
                }, 10);
            }
        });

    }
    public void getCurrentValues(){
        DocumentReference surveyBeachRef = db.collection("survey").document(currentDate).collection(beachName).document(currentDate);

        surveyBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object VWCObject = document.getData().get(visualWaterConditionsValue);
                        if(VWCObject==null){

                            Log.d("isnull","isNull");
                            currentVisualWaterConditionsValue = 0;
                        }else{
                            currentVisualWaterConditionsValue = (long)document.getData().get(visualWaterConditionsValue);
                        }
                        Object BCObject = document.getData().get(beachCapacityValue);

                        if(BCObject==null){
                            Log.d("isnull","isNull");

                            currentBeachCapacityValue = 0;
                        }else{
                            currentBeachCapacityValue = (long)document.getData().get(beachCapacityValue);
                        }
                        Log.d("ValsCurrent","Current: "+currentBeachCapacityValue + " "+currentVisualWaterConditionsValue);

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
        Log.d("currentVals",currentBeachCapacityValue + " d: "+currentVisualWaterConditionsValue);
        currentBeachCapacityValue++;
        currentVisualWaterConditionsValue++;
        Log.d("currentValsPost",currentBeachCapacityValue + " 2: "+currentVisualWaterConditionsValue);
        Log.d("visualWaters",visualWaterConditionsValue + " capacityValue "+beachCapacityValue);
        if(visualWaterConditionsValue.equals("Calm waters"))
            calmWatersCount++;
        if(visualWaterConditionsValue.equals("Medium waters"))
            mediumWatersCount++;
        if(visualWaterConditionsValue.equals("Rough waters"))
            roughWatersCount++;
        if(beachCapacityValue.equals("Low Capacity"))
            lowCapacityCount++;
        if(beachCapacityValue.equals("Medium Capacity"))
            mediumCapacityCount++;
        if(beachCapacityValue.equals("High Capacity"))
            highCapacityCount++;

        Log.d("watersCountHere","calmW: "+calmWatersCount+" medW: "+mediumWatersCount +" rough: "+roughWatersCount);
        Log.d("capCountHere","capL: "+lowCapacityCount+" capMed: "+mediumCapacityCount +" capHigh: "+highCapacityCount);

        setCapacityAndVisualConditionText();

        survey.put(visualWaterConditionsValue, currentVisualWaterConditionsValue);
        survey.put(beachCapacityValue, currentBeachCapacityValue);

        survey2.put("beachCapacityTextForTheDay", surveyCapacityTextForTheDay);
        survey2.put("beachVisualWaveConditionsTextForTheDay", surveyVisualWaterConditionsTextForTheDay);

        survey.put("date", formattedDate);
        Map<String, Object> emptyVal = new HashMap<>();
        emptyVal.put("emptyField","EmptyVal");

        DocumentReference surveyEmptyField = db.collection("survey").document(currentDate);
        surveyEmptyField.set(emptyVal,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("LifeGuardSurveyWrite22222222", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LifeGuardSurveyWrite2222", "Error writing document", e);
                    }
                });
        DocumentReference surveyBeachRef = db.collection("survey").document(formattedDate).collection(beachName).document(formattedDate);

        surveyBeachRef.set(survey,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("LifeGuardSurveyWrite22222222", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LifeGuardSurveyWrite2222", "Error writing document", e);
                    }
                });

        db.collection("beach").document(beachName)
                .set(survey2,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("LifeGuardSurveyWrite22222222", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LifeGuardSurveyWrite2222", "Error writing document", e);
                    }
                });

    }
    public void setCapacityAndVisualConditionText(){

        if(calmWatersCount > mediumWatersCount && calmWatersCount > roughWatersCount){
            surveyVisualWaterConditionsTextForTheDay = "Visual water conditions: Calm Waters";

        }
        else if(mediumWatersCount >= calmWatersCount && mediumWatersCount >= roughWatersCount){
            surveyVisualWaterConditionsTextForTheDay = "Visual water conditions: Medium Waters";
        }
        else if(roughWatersCount >= calmWatersCount && roughWatersCount >= mediumWatersCount){
            surveyVisualWaterConditionsTextForTheDay = "Visual water conditions: Rough Waters";
        }

        if(lowCapacityCount > mediumCapacityCount && lowCapacityCount > highCapacityCount){
            surveyCapacityTextForTheDay = "Beach Capacity: Low Capacity";
        }
        else if(mediumCapacityCount >= lowCapacityCount && mediumCapacityCount >= highCapacityCount){
            surveyCapacityTextForTheDay = "Beach Capacity: Medium Capacity";
        }
        else if(highCapacityCount >= lowCapacityCount && highCapacityCount >= mediumCapacityCount){
            surveyCapacityTextForTheDay = "Beach Capacity: High Capacity";
        }

        if(lowCapacityCount ==0 && mediumCapacityCount ==0 && highCapacityCount==0){
            surveyCapacityTextForTheDay = "Beach Capacity: No data today!";
        }
        if(calmWatersCount ==0 && mediumWatersCount ==0 && roughWatersCount==0){
            surveyVisualWaterConditionsTextForTheDay = "Visual Water Conditions: No data today!";

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long length) {
        String selectedValue = parent.getItemAtPosition(position).toString();
        visualWaterConditionsValue = selectedValue;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
