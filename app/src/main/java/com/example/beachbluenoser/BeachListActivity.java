package com.example.beachbluenoser;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class BeachListActivity extends AppCompatActivity {
    final  FirebaseFirestore db = FirebaseFirestore.getInstance();
    int emptyListTextViewOriginalHeight = -1; // to store original height of the TextView
    ArrayList<BeachItem> beachList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_beach_list);


    }

    @Override
    protected void onResume() {
        super.onResume();

        getDataFromDbAndShowOnUI();
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
                                String DataName =  document.getData().get("name").toString();
                               // String DataDescription = document.getData().get("description").toString();
                                String DataImage = document.getData().get("image").toString();
                                Long DataRating = (Long) document.getData().get("rating");
                                String landingBeachCapacityValue="";
                                String landingBeachWheelChairRampValue="";
                                String landingBeachSandyOrRockyValue="";

                                if(document.exists()){
                                    if(document.getData().get("capacity")!=null){
                                        landingBeachCapacityValue = document.getData().get("capacity").toString();
                                    }else{
                                        landingBeachCapacityValue = "";
                                    }
                                    if(document.getData().get("wheelchairRamp")!=null){
                                        landingBeachWheelChairRampValue = document.getData().get("wheelchairRamp").toString();
                                    }else{
                                        landingBeachWheelChairRampValue = "";
                                    }
                                    if(document.getData().get("sandyOrRocky")!=null){
                                        landingBeachSandyOrRockyValue = document.getData().get("sandyOrRocky").toString();
                                    }else{
                                        landingBeachSandyOrRockyValue = "";
                                    }
                                }

                                BeachItem beachItem = new BeachItem(DataName,DataImage,landingBeachWheelChairRampValue,landingBeachCapacityValue,landingBeachSandyOrRockyValue);

                                beachItemArrayList.add(beachItem);
                            }
                        } else {
                            Log.w("BeachRetrievalLoopERROR", "Error getting documents.", task.getException());
                        }

                        beachList = beachItemArrayList;
                        Collections.reverse(beachList);
                        Log.w("Beach list size check", "Beach list size "+beachList.size());
                        loadMasterBeachList();
                    }


                });



    }

    private void loadMasterBeachList() {


        Log.w("Beach list size check22222", "Beach list size "+beachList.size());

        createRecyclerView(beachList);
    }

    /**
     * creates the Recycler view for all my task posts
     * @param beachList list of all my posts
     */
    public void createRecyclerView(ArrayList<BeachItem> beachList) {

        //a message shows/expands in the recycleView if it's empty and collapses otherwise
        /*
        TextView emptyListTextView = (TextView)findViewById(R.id.emptyStatusMyPosts);
        if(emptyListTextViewOriginalHeight == -1){
            emptyListTextViewOriginalHeight = emptyListTextView.getHeight();
        }
*/
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.BeachMasterList);

        // using a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        RecyclerView.Adapter mAdapter = new MasterBeachListAdapter(beachList);
        recyclerView.setAdapter(mAdapter);



    }


}