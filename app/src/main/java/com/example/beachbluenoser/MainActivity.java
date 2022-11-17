package com.example.beachbluenoser;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;




import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String[] beach = {"All Beaches", "Rocky Beach", "Sandy Beach", "Shore Accessibility", "Floating Wheelchair"};
    String[] capacity = {"High Capacity", "Medium Capacity", "Low Capacity"};

    AutoCompleteTextView beachType; //Beach
    AutoCompleteTextView capacityVolume; //Capacity
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db.collection("beach")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        super.onCreate(savedInstanceState);

        //Beach
        setContentView(R.layout.activity_main);
        beachType = findViewById(R.id.auto_complete_textview);

        ArrayAdapter<String> adapterItems; //For Beach
        adapterItems = new ArrayAdapter<String>(this, R.layout.beach_list, beach);
        beachType.setAdapter(adapterItems);

        //Beach
        beachType.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String item = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, item + " Option", Toast.LENGTH_SHORT).show();
            }
        }));

//        //Capacity
//        setContentView(R.layout.activity_main);
//        capacityVolume = findViewById(R.id.auto_complete_textview2);
//
//        ArrayAdapter<String> adapterItems2; //For Capacity
//        adapterItems2 = new ArrayAdapter<String>(this, R.layout.capacity_list, capacity);
//        capacityVolume.setAdapter(adapterItems2);
//
//        // Capacity
//        capacityVolume.setOnItemClickListener((new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String capacity = adapterView.getItemAtPosition(position).toString();
//                Toast.makeText(MainActivity.this, capacity, Toast.LENGTH_SHORT).show();
//            }
//        }));
    }
}
