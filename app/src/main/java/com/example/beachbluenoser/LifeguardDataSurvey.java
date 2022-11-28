package com.example.beachbluenoser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class LifeguardDataSurvey extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String visualWaterConditionsValue;
    public String beachName;
    public String currentDate;
    public String beachCapacityValue;
    public TextView name;
    public long currentVisualWaterConditionsValue;
    public long currentBeachCapacityValue;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lifeguard_data);

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDate = formattedDate;
       // Log.d("TIME222","CUR TIME:"+formattedDate+";");

        Log.d("here222","2222");
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString("beachName")!=null) {
                beachName = bundle.getString("beachName");

                Log.d("beach Main Page NAme 22222", " Name : " + beachName);
            }
        }


    //    Log.d("Here222","beachName22: "+)
        name = findViewById(R.id.surveyTitle);
        name.setText(beachName);

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





        // spinnerSetup();
        Button btn = (Button)findViewById(R.id.lifeGuardSurveyButton);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("HELLOTEST","JELLOTEST");

                 visualWaterConditionsValue = visualWaveConditionSpinner.getSelectedItem().toString();
                 beachCapacityValue = beachCapacitySpinner.getSelectedItem().toString();
                Log.d("Values",visualWaterConditionsValue+" "+beachCapacityValue);
                getCurrentValues();

                Intent intent = new Intent(LifeguardDataSurvey.this,beachLanding.class);
                intent.putExtra("beachName",beachName);

                startActivity(intent);

            }
        });



    }
    public void getCurrentValues(){
        DocumentReference landingBeachRef = db.collection("survey").document(beachName);

        landingBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        Log.d("HELLOTEST2222","JELLOTEST222");
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Log.d("TIME222","CUR TIME:"+formattedDate+";");


        Map<String, Object> survey = new HashMap<>();
        Log.d("currentVals",currentBeachCapacityValue + " d: "+currentVisualWaterConditionsValue);
        currentBeachCapacityValue++;
        currentVisualWaterConditionsValue++;
        Log.d("currentValsPost",currentBeachCapacityValue + " 2: "+currentVisualWaterConditionsValue);

        survey.put(visualWaterConditionsValue, currentVisualWaterConditionsValue);
        survey.put(beachCapacityValue, currentBeachCapacityValue);
        survey.put("date", formattedDate);

        db.collection("survey").document(beachName)
                .set(survey,SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("LifeGuardSurveyWrite", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LifeGuardSurveyWrite", "Error writing document", e);
                    }
                });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long length) {
        String selectedValue = parent.getItemAtPosition(position).toString();
        visualWaterConditionsValue = selectedValue;
        //Toast.makeText(parent.getContext(),selectedValue,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
