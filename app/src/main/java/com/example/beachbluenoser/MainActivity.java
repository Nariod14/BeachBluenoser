package com.example.beachbluenoser;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity {
    final  FirebaseFirestore db = FirebaseFirestore.getInstance();
    //int emptyListTextViewOriginalHeight = -1; // to store original height of the TextView
    ArrayList<BeachItem> beachList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_beach_list);

/** DataBase Test code: add user Ada Lovelace **/
//        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);
//
//        // Add a new document with a generated ID
//        db.collection("usertable")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });

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
                            Log.w("BeachRetrievalLoopERROR", "Error getting documents.", task.getException());
                        }

                        beachList = beachItemArrayList;
                        Collections.reverse(beachList);
                       // Log.w("Beach list size check22222", "B1111");
                      //  Log.w("Beach list size check", "Beach list size "+beachList.size());
                        loadMasterBeachList();
                    }


                });



    }

    private void loadMasterBeachList() {

        /*
        ArrayList<TaskPost> activePosts = new ArrayList<>();

        for (TaskPost task: taskPosts) {
            if(!task.isPostDeleted()){
                activePosts.add(task);
            }
        }

         */
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

