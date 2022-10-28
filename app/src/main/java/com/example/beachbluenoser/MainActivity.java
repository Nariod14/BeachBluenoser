package com.example.beachbluenoser;

import static android.content.ContentValues.TAG;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import javax.xml.parsers.FactoryConfigurationError;

public class MainActivity extends AppCompatActivity {
    final  FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<BeachItem> beachList;

    String[] item = {"All", "Rocky", "Sandy", "Wheelchair Ramps", "Floating Wheelchairs"};
    String[] capacity = {"High", "Medium", "Low"};

    ArrayAdapter<String> adapterItems;

    AutoCompleteTextView autoCompleteTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button homeBtn = findViewById(R.id.HomeButton);
        final Button loginBtn = findViewById(R.id.LoginButton);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: update code so that it takes user back to homepage all the time
                setContentView(R.layout.activity_main);
                getDataFromDbAndShowOnUI();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: find a way to change button to to view account

                //TODO: change view to login page when clicked

            }
        });
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
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String DataName =  document.getData().get("name").toString();
                             //   String DataDescription = document.getData().get("description").toString();
                                Object DataImage  = document.getData().get("image");
                                String DataImageValue;
                                if(DataImage == null){
                                    DataImageValue = "imageNotFound";
                                }else {
                                    DataImageValue = document.getData().get("image").toString();
                                }
                                Long DataRating = (Long) document.getData().get("rating");
                                if(DataRating==null){
                                    DataRating = Long.valueOf(0);
                                }
                                BeachItem beachItem = new BeachItem(DataName,DataImageValue,DataRating.intValue());

                                beachItemArrayList.add(beachItem);
                               // Log.w("Beach list size check777777", "Beacharrrraayyyy list size "+beachItemArrayList.size());
                               // Log.d("BeachRetrivalLoop", "retrieved beach with name: "+DataName +" img: "+DataImage);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Log.w("BeachRetrievalLoopERROR", "Error getting documents.", task.getException());
                        }

                        beachList = beachItemArrayList;
                        Collections.reverse(beachList);
                       // Log.w("Beach list size check22222", "B1111");
                      //  Log.w("Beach list size check", "Beach list size "+beachList.size());
                        loadMasterBeachList();
                    }
                });

        autoCompleteTextView = findViewById(R.id.auto_complete_textview);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String item = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Item " + item, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void loadMasterBeachList() {
        Log.w("Beach list size check22222", "B4444");
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

        /*
        if(beachList != null && beachList.size() > 0){
            //hide message that says the list is empty
            emptyListTextView.setHeight(0);
        }else {
            emptyListTextView.setHeight(emptyListTextViewOriginalHeight);
        }

         */

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

     */
}

