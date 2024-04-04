package com.example.beachbluenoser;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import com.google.auth.oauth2.GoogleCredentials;
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
import com.google.firebase.messaging.FirebaseMessaging;


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

import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Arrays;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    final  FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth beachBluenoserAuth = FirebaseAuth.getInstance();
    ArrayList<BeachItem> beachList;

    // String[] beach = {"All Beaches", "Rocky", "Sandy", "Wheelchair Accessible", "Floating Wheelchair"};
    String[] capacity = {"Any Capacity", "High", "Medium", "Low","Any Parking","Many Spots","Few Spots","Little/No Spots"};

    SwitchCompat itemToggle;
    SwitchCompat favToggle;

    boolean ischeckTAG;




    String filterBeachItem = "";
    String filterCapacityItem = "";
    String filterParkingItem = "";

    public String visualWaterConditionsText;
    public String capacityText;
    public List<String> dates = new ArrayList<>();

    public String currentDate;
    public String beachName;
    ArrayAdapter<String> adapterItems;

    AutoCompleteTextView beachType; //Beach
    AutoCompleteTextView capacityVolume; //Capacity

    MediaPlayer mp;
    private RecyclerView beachMasterList;

    // making Hash Set
    Set<String> selectedItems = new HashSet<>();

    interface MyCallback {
        void callbackCall();
    }

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemToggle = findViewById(R.id.itemToggle);
        favToggle = findViewById(R.id.favToggle);


        itemToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ischeckTAG = isChecked;
                updateRecyclerView(beachList,isChecked);
            }
        });
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_textview);
        autoCompleteTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        AutoCompleteTextView autoCompleteTextView2 = (AutoCompleteTextView) findViewById(R.id.auto_complete_textview2);
        autoCompleteTextView2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS},99);
            Log.d(TAG,"Location Permission asked");
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }


                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d(TAG,"FCM Token:  " + token);
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("warnings")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        favToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    populateFavoriteBeaches();
                } else {
                    updateRecyclerView(beachList, ischeckTAG); // Change false to true if you want to use second layout
                }
            }
        });


        beachList = new ArrayList<>();


        String[] beachItems = {"Rocky", "Sandy", "Wheelchair Accessible", "Floating Wheelchair"};
        ArrayList<Integer> tagList = new ArrayList<>();
        boolean[] selected = new boolean[beachItems.length];


        ArrayAdapter<String> beachAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, beachItems);

        beachAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        beachs.setAdapter(beachAdapter);
//
        AutoCompleteTextView textView = findViewById(R.id.auto_complete_textview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                build.setTitle("Select Beaches");

                build.setCancelable(false);
                build.setMultiChoiceItems(beachItems, selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            tagList.add(i);
                            // Sort array list
                            Collections.sort(tagList);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            tagList.remove(Integer.valueOf(i));
                        }
                    }
                });
                build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < tagList.size(); j++) {
                            // concat array value
                            stringBuilder.append(beachItems[tagList.get(j)]);
                            // check condition
                            if (j != tagList.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(",");
                            }
                        }
                        // set text on textView
                        if(!stringBuilder.toString().equals("")) {
                            textView.setText(stringBuilder.toString());
                        }
                        String itemList;
                        itemList = stringBuilder.toString();
                        ArrayList<String> beachTagList = new ArrayList<>(Arrays.asList(itemList.split(",")));

                        // Loop through the array list
                        for (String element : beachTagList) {
                            // Do something with each element
                            filterBeachItem = element;
                            Log.d("ELEMENT",element);
                            getDataFromDbAndShowOnUI();
                        }

                    }
                });

                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                build.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selected.length; j++) {
                            // remove all selection
                            selected[j] = false;
                            // clear language list
                            tagList.clear();
                            // clear text view value
                            textView.setText("Beaches");
                        }
                        filterBeachItem = "";
                        getDataFromDbAndShowOnUI();
                    }
                });
                // show dialog
                build.show();
            }
        });



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
                if (capacityItem.equals("Any Capacity") || capacityItem.equals("Any Parking")) {
                    filterCapacityItem = "";
                }
                else if (capacityItem.equals("Low") || capacityItem.equals("Medium") || capacityItem.equals("High")){
                    filterCapacityItem = "Beach Capacity: "+ capacityItem + " Capacity";
                }
                else {
                    filterCapacityItem = "Parking Availability: " + capacityItem;
                }
                getDataFromDbAndShowOnUI();
            }
        }));






        final Button homeBtn = findViewById(R.id.HomeButton);
        final Button loginProfileBtn = findViewById(R.id.LoginButton);
        mp = MediaPlayer.create(this, R.raw.click);
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
                beachMasterList = findViewById(R.id.BeachMasterList);
                beachMasterList.smoothScrollToPosition(0);
            }
        });

        loginProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ;
                if (beachBluenoserAuth.getCurrentUser() != null){
                    mp.start();
                    Intent profileIntent = new Intent(MainActivity.this, userprofile.class);
                    startActivity(profileIntent);
                } else {
                    mp.start();
                    Intent loginIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(loginIntent);
                }
            }
        });


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

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.default_notification_channel_name);
        String description = getString(R.string.default_notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void populateFavoriteBeaches() {
        getUserfavbeaches(new FavBeachesCallback() {
            @Override
            public void onFavBeachesReceived(ArrayList<String> favBeaches) {
                ArrayList<BeachItem> favoriteBeachList = new ArrayList<>();
                for (BeachItem beachItem : beachList) {
                    if (favBeaches.contains(beachItem.getName())) {
                        favoriteBeachList.add(beachItem);
                    }
                }
                updateRecyclerView(favoriteBeachList, ischeckTAG); // Change false to true if you want to use second layout
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

    public interface FavBeachesCallback {
        void onFavBeachesReceived(ArrayList<String> favBeaches);
    }


    public void getUserfavbeaches(FavBeachesCallback callback) {
        String userID = beachBluenoserAuth.getCurrentUser().getUid();
        DocumentReference Ref = db.collection("BBUSERSTABLE-PROD").document(userID);
        Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        ArrayList<String> userFavBeaches = (ArrayList<String>) documentSnapshot.getData().get("favBeaches");
                        if (userFavBeaches != null) {
                            callback.onFavBeachesReceived(userFavBeaches);
                        } else {
                            callback.onFavBeachesReceived(new ArrayList<>());
                        }
                    }
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
                                String beachParking = "";
                                //Retrieve parking
                                if(!(document.getData().get("beachParkingConForDay")==null)) {
                                    beachParking = document.getData().get("beachParkingConForDay").toString();
                                }else{
                                    beachParking="Parking Availability: No data today!";
                                }
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
                                        beachVisualWaveConditionsTextForTheDay,recyclerViewWheelchairAccessValue,recyclerViewSandyOrRockyValue,recyclerViewFloatingWheelchairValue,beachParking);
                                //beachItemArrayList.add(beachItem);
                                Log.d("beachdetails:","wheelchair: "+beachItem.getwheelchairAccess() + " floating: "+beachItem.getFloatingWheelchair() +" sandy or rocky: "+beachItem.getsandyOrRocky());
                                Log.d("FilterItem:","filterItem:"+filterCapacityItem);



                                if (Objects.equals(filterBeachItem, "") || Objects.equals(beachItem.getsandyOrRocky(), filterBeachItem) || Objects.equals(beachItem.getwheelchairAccess(), filterBeachItem) || Objects.equals(beachItem.getFloatingWheelchair(), filterBeachItem)) {
                                    if (Objects.equals(filterCapacityItem, "") || Objects.equals(beachItem.getcapacity(), filterCapacityItem) || Objects.equals(beachItem.getParking(), filterCapacityItem)) {
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


       /* adapterItems = new ArrayAdapter<String>(this, R.layout.beach_list, beachItems);
        beachType.setAdapter(adapterItems);

        beachType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mp.start();
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

        });*/

        //Capacity


    }
    private void updateRecyclerView(ArrayList<BeachItem> beachList,  boolean useSecondLayout) {
        RecyclerView recyclerView = findViewById(R.id.BeachMasterList);

        RecyclerView.LayoutManager layoutManager;

        if (useSecondLayout) {
            layoutManager = new LinearLayoutManager(this);
        } else {
            layoutManager = new GridLayoutManager(this, 2);
        }

        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new MasterBeachListAdapter(beachList, useSecondLayout);
        recyclerView.setAdapter(mAdapter);
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
    //

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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new MasterBeachListAdapter(beachList);
        recyclerView.setAdapter(mAdapter);
    }
}

