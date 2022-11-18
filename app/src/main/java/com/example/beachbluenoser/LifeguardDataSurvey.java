package com.example.beachbluenoser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LifeguardDataSurvey extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String visualWaterConditionsValue;
    public String beachName;
    public String currentDate;
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



                writeDataToDB();
               // startActivity(new Intent(beachLanding.this,LifeguardDataSurvey.class));
            }
        });

    }

    public void writeDataToDB(){
        Log.d("HELLOTEST2222","JELLOTEST222");
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Log.d("TIME222","CUR TIME:"+formattedDate+";");





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
