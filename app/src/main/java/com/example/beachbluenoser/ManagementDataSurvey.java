package com.example.beachbluenoser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class ManagementDataSurvey extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String beachTypeValue;
    public String wheelChairAccessibleValue;
    public String floatingWheelchairValue;
    public String beachName;
    public TextView name;
    ImageButton backArrowkey;;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_survey);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString("beachName")!=null) {
                beachName = bundle.getString("beachName");
            }
        }
        backArrowkey = findViewById(R.id.backArrow);
        backArrowkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagementDataSurvey.this,MainActivity.class);
                startActivity(intent);
            }
        });

        name = findViewById(R.id.surveyTitle);
        name.setText(beachName);

        Spinner beachTypeSpinner = findViewById(R.id.beachTypeSpinner);
        ArrayAdapter<CharSequence> adapterBeachTypeSpinner = ArrayAdapter.createFromResource(this,R.array.beachTypeValues, android.R.layout.simple_spinner_item);
        adapterBeachTypeSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        beachTypeSpinner.setAdapter(adapterBeachTypeSpinner);
        beachTypeSpinner.setOnItemSelectedListener(this);

        Spinner wheelchairAccessibleSpinner = findViewById(R.id.wheelchairAccessibleSpinner);
        ArrayAdapter<CharSequence> adapterAccessibleSpinner = ArrayAdapter.createFromResource(this,R.array.wheelChairAccessibleValue, android.R.layout.simple_spinner_item);
        adapterAccessibleSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wheelchairAccessibleSpinner.setAdapter(adapterAccessibleSpinner);
        wheelchairAccessibleSpinner.setOnItemSelectedListener(this);

        Spinner floatingWheelchairSpinner = findViewById(R.id.floatingWheelchairSpinner);
        ArrayAdapter<CharSequence> adapterFloatingWheelSpinner = ArrayAdapter.createFromResource(this,R.array.floatingWheelChairValue, android.R.layout.simple_spinner_item);
        adapterFloatingWheelSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floatingWheelchairSpinner.setAdapter(adapterFloatingWheelSpinner);
        floatingWheelchairSpinner.setOnItemSelectedListener(this);

        Button btn = findViewById(R.id.lifeGuardSurveyButton);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("HELLOTEST","JELLOTEST");

                beachTypeValue = beachTypeSpinner.getSelectedItem().toString();
                wheelChairAccessibleValue = wheelchairAccessibleSpinner.getSelectedItem().toString();
                floatingWheelchairValue = floatingWheelchairSpinner.getSelectedItem().toString();
                Log.d("Values",beachTypeValue+" "+wheelChairAccessibleValue+" "+floatingWheelchairValue);
                writeDataToDB();
                Intent intent = new Intent(ManagementDataSurvey.this,beachLanding.class);
                intent.putExtra("beachName",beachName);

                startActivity(intent);

            }
        });
    }

    public void writeDataToDB(){
        if (wheelChairAccessibleValue.equals("Yes")){ wheelChairAccessibleValue = "Wheelchair Accessible"; }
        if (floatingWheelchairValue.equals("Yes")){ floatingWheelchairValue = "Floating Wheelchair"; }

        Map<String, Object> survey2 = new HashMap<>();

        survey2.put("sandyOrRocky", beachTypeValue);
        survey2.put("wheelchairAccessible", wheelChairAccessibleValue);
        survey2.put("floatingWheelchair", floatingWheelchairValue);

        db.collection("beach").document(beachName)
                .set(survey2,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        beachTypeValue = selectedValue;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}
