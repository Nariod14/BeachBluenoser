package com.example.beachbluenoser;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Map;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final  FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth beachBluenoserAuth = FirebaseAuth.getInstance();
    ArrayList<BeachItem> beachList;

    String[] beach = {"All Beaches", "Rocky", "Sandy", "Wheelchair Accessible", "Floating Wheelchair"};
    String[] capacity = {"Any Capacity", "High", "Medium", "Low"};
    String filterBeachItem = "";
    String filterCapacityItem = "";

    public String visualWaterConditionsText;
    public String capacityText;
    public List<String> dates = new ArrayList<>();

    public String currentDate;
    public String beachName;
    ArrayAdapter<String> adapterItems;

    AutoCompleteTextView beachType; //Beach
    AutoCompleteTextView capacityVolume; //Capacity

    interface MyCallback {
        void callbackCall();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button homeBtn = findViewById(R.id.HomeButton);
        final Button loginProfileBtn = findViewById(R.id.LoginButton);
        //beachBluenoserAuth.signOut();
        if (beachBluenoserAuth.getCurrentUser() != null){
            loginProfileBtn.setText("Profile");
        }
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDate = formattedDate;

        checkDate();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });

        loginProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beachBluenoserAuth.getCurrentUser() != null){
                    Intent profileIntent = new Intent(MainActivity.this, userprofile.class);
                    startActivity(profileIntent);
                } else {
                    Intent loginIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(loginIntent);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDbAndShowOnUI();
    }

    private void checkDate() {

        db.collection("survey").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                List<String> list = new ArrayList<>();

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    if(list.contains(currentDate)){
                        Log.d("ResetDataforToday","yes contains");

                    }else{
                        Log.d("ResetDataforToday","no does not. ");
                        resetDataForToday();
                    }
                    Log.d("printDocs", list.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void resetDataForToday(){
        Log.d("StartReset","yes");

        Map<String, Object> resetText = new HashMap<>();

        resetText.put("beachCapacityTextForTheDay", "Beach Capacity: No data today!");
        resetText.put("beachVisualWaveConditionsTextForTheDay", "Visual Water Conditions: No data today!");

        db.collection("beach").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("resetting","name: "+document.getId());
                        document.getReference().update(resetText);
                    }
                } else {
                    Log.w(TAG, "Error resetingData", task.getException());
                    Log.w("BeachRetrievalLoopERROR", "Error getting documents.", task.getException());
                }
            }
        });
    }

        private void getDataFromDbAndShowOnUI() {
        // to toggle between the "deleted posts" and active posts button
        // resetToggle();
        final ArrayList<BeachItem> beachItemArrayList = new ArrayList<>();

        db.collection("beach")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String DataName =  document.getData().get("name").toString();
                                beachName = DataName;
                                String beachCapacityTextForTheDay ="";
                                String beachVisualWaveConditionsTextForTheDay = "";
                                if(!(document.getData().get("beachCapacityTextForTheDay")==null)) {
                                     beachCapacityTextForTheDay = document.getData().get("beachCapacityTextForTheDay").toString();
                                }else{
                                    beachCapacityTextForTheDay="Beach Capacity: No data today!";
                                }
                                if(!(document.getData().get("beachVisualWaveConditionsTextForTheDay")==null)) {
                                     beachVisualWaveConditionsTextForTheDay = document.getData().get("beachVisualWaveConditionsTextForTheDay").toString();
                                }else{
                                    beachVisualWaveConditionsTextForTheDay ="Water Conditions: No data today!";
                                }
                                Object DataImage  = document.getData().get("image");
                                String DataImageValue;
                                if(DataImage == null){
                                    DataImageValue = "imageNotFound";
                                }else {
                                    DataImageValue = document.getData().get("image").toString();
                                }
                                String recyclerViewWheelchairAccessValue="";
                                String recyclerViewSandyOrRockyValue="";
                                String recyclerViewFloatingWheelchairValue="";
                                if(document.exists()){
                                    if(document.get("sandyOrRocky")!=null){
                                        recyclerViewSandyOrRockyValue = document.get("sandyOrRocky").toString();
                                    }else{
                                        recyclerViewSandyOrRockyValue = "";
                                    }
                                    if(document.get("wheelchairAccessible")!=null){
                                        recyclerViewWheelchairAccessValue = document.get("wheelchairAccessible").toString();
                                    }else{
                                        recyclerViewWheelchairAccessValue = "";
                                    }
                                    if(document.get("floatingWheelchair")!=null){
                                        recyclerViewFloatingWheelchairValue = document.get("floatingWheelchair").toString();
                                    }else{
                                        recyclerViewFloatingWheelchairValue = "";
                                    }
                                }

                                retrieveAdditionalDataFromDB();

                                Log.d("PrintingHere","BeachName: "+DataName + " capacity: "+beachCapacityTextForTheDay +  " visualWaterConditions: " +beachVisualWaveConditionsTextForTheDay);
                                BeachItem beachItem = new BeachItem(DataName,DataImageValue,beachCapacityTextForTheDay,
                                        beachVisualWaveConditionsTextForTheDay,recyclerViewWheelchairAccessValue,recyclerViewSandyOrRockyValue,recyclerViewFloatingWheelchairValue);
                                //beachItemArrayList.add(beachItem);
                                Log.d("beachdetails:","wheelchair: "+beachItem.getwheelchairAccess() + " floating: "+beachItem.getFloatingWheelchair() +" sandy or rocky: "+beachItem.getsandyOrRocky());
                                Log.d("FilterItem:","filterItem:"+filterCapacityItem);


                                if (Objects.equals(filterBeachItem, "") || Objects.equals(beachItem.getsandyOrRocky(), filterBeachItem) || Objects.equals(beachItem.getwheelchairAccess(), filterBeachItem) || Objects.equals(beachItem.getFloatingWheelchair(), filterBeachItem)) {
                                    if (Objects.equals(filterCapacityItem, "") || Objects.equals(beachItem.getcapacity(), filterCapacityItem)) {
                                        beachItemArrayList.add(beachItem);
                                    }
                                }

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Log.w("BeachRetrievalLoopERROR", "Error getting documents.", task.getException());
                        }

                        beachList = beachItemArrayList;
                        Collections.reverse(beachList);

                        loadMasterBeachList();
                    }
                });

        //filters
        beachType = findViewById(R.id.auto_complete_textview);
        adapterItems = new ArrayAdapter<String>(this, R.layout.beach_list, beach);
        beachType.setAdapter(adapterItems);

        beachType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String beachItem = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, beachItem + " Option", Toast.LENGTH_SHORT).show();
                beachList.clear();
                if (beachItem.equals("All Beaches")){
                    filterBeachItem = "";
                } else {
                    filterBeachItem = beachItem;
                }
                getDataFromDbAndShowOnUI();
            }

        });

        //Capacity
        capacityVolume = findViewById(R.id.auto_complete_textview2);

        ArrayAdapter<String> adapterItems2; //For Capacity
        adapterItems2 = new ArrayAdapter<String>(this, R.layout.capacity_list, capacity);
        capacityVolume.setAdapter(adapterItems2);

        // Capacity
        capacityVolume.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //dropdown item
                String capacityItem = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, capacityItem, Toast.LENGTH_SHORT).show();
                beachList.clear();
                if (capacityItem.equals("Any Capacity")) {
                    filterCapacityItem = "";
                }
                else {
                    filterCapacityItem = "Beach Capacity: "+ capacityItem + " Capacity";
                }
                getDataFromDbAndShowOnUI();
            }
        }));
    }

    private void retrieveAdditionalDataFromDB(){
        DocumentReference landingBeachRef = db.collection("survey").document(currentDate).collection(beachName).document(currentDate);
        landingBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(!(document.getData().get("beachCapacityTextForTheDay")==null))
                            capacityText  =document.getData().get("beachCapacityTextForTheDay").toString();
                        if(!(document.getData().get("beachVisualWaveConditionsTextForTheDay")==null))
                            visualWaterConditionsText  = document.getData().get("beachVisualWaveConditionsTextForTheDay").toString();




                       // showDataOnUI();
                    } else {
                        Log.d("Beach Landing Query", "No such document");
                       // showDataOnUI();
                    }
                } else {
                    Log.d("Beach Landing Query", "get failed with ", task.getException());
                }
            }
        });
    }

    private void loadMasterBeachList() {
        createRecyclerView(beachList);
    }

    /**
     * creates the Recycler view for all my task posts
     * @param beachList list of all my posts
     */
    public void createRecyclerView(ArrayList<BeachItem> beachList) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.BeachMasterList);

        // using a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new MasterBeachListAdapter(beachList);
        recyclerView.setAdapter(mAdapter);
    }
}
