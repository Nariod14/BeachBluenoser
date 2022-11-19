package com.example.beachbluenoser;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.LatLng;

import java.util.Collections;

public class beachLanding extends AppCompatActivity {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String beachName;
    public String landingBeachCapacityValue;
    public String landingBeachSandyOrRockyValue;
    public String landingBeachWheelChairRampValue;
    public String landingBeachImageSource;
    public ImageView landingBeachImageView;
    public TextView landingBeachCapacityView;
    public TextView landingBeachSandyOrRockyView;
    public TextView landingBeachWheelChairRampView;
    public TextView landingBeachNameView;
    public TextView placeholder;
    public Double beachLat,beachLong;
    public String beachLocation;
    public Button mapsBtn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beach_landing);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString("beachName")!=null) {
                beachName = bundle.getString("beachName");

                Log.d("beach Main Page Name ", " Name : " + beachName);
            }
        }

       // spinnerSetup();
        getDataFromDB();
        mapsBtn = findViewById(R.id.mapsBtn);
        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:"+beachLocation));
                Intent chooser = Intent.createChooser(intent,"Launch Maps");
                startActivity(chooser);
            }
        });

    }

    private void getDataFromDB(){
        DocumentReference landingBeachRef = db.collection("beach").document(beachName);

        landingBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object DataImage  = document.getData().get("image");
                        String DataImageValue;
                        if(DataImage == null){
                            DataImageValue = "imageNotFound";
                        }else {
                            DataImageValue = document.getData().get("image").toString();
                        }
                        landingBeachImageSource = DataImageValue;

                        if(document.exists()){
                            if(document.get("capacity")!=null){
                                landingBeachCapacityValue = document.get("capacity").toString();
                            }
                            if(document.get("wheelchairRamp")!=null){
                                landingBeachWheelChairRampValue = document.get("wheelchairRamp").toString();
                            }
                            if(document.get("sandyOrRocky")!=null){
                                landingBeachSandyOrRockyValue = document.get("sandyOrRocky").toString();
                            }
                            if(document.get("location")!=null){
                                GeoPoint geoPoint = document.getGeoPoint("location");
                                beachLat = geoPoint.getLatitude();
                                beachLong = geoPoint.getLongitude();
                                Log.d("Beach Location", "location"+ beachLat +", " + beachLong);
                                beachLocation = beachLat + "" + beachLong;
                            }
                        }


                        showDataOnUI();

                    } else {
                        Log.d("Beach Landing Query", "No such document");


                    }
                } else {
                    Log.d("Beach Landing Query", "get failed with ", task.getException());
                }
            }
        });




    }
    private void showDataOnUI(){
        landingBeachCapacityView = findViewById(R.id.landingBeachCapacityTextView);
        landingBeachSandyOrRockyView = findViewById(R.id.landingBeachSandyOrRockyTextView);
        landingBeachWheelChairRampView = findViewById(R.id.landingBeachWheelChairRampTextView);
        landingBeachNameView = findViewById(R.id.landingBeachNameTextView);

        landingBeachCapacityView.setText(landingBeachCapacityValue);
        landingBeachSandyOrRockyView.setText(landingBeachSandyOrRockyValue);
        landingBeachWheelChairRampView.setText(landingBeachWheelChairRampValue);
        landingBeachNameView.setText(beachName);
        setBeachImage();


    }

    public void setBeachImage(){
        //Log.d("setBeachImage3333","Val 1: "+beachImageFileName);



        //Log.d("IMAGENAME: ","name : "+ beaches.get(pos).getImageSource());
        // beachImage.setImageResource(R.drawable.theetcher);
        //     beachImage.setImageURI("path/");
        // beachImage.setBackground(R.drawable.beachmeadows_beach);
        if(landingBeachImageSource.equals("")|| landingBeachImageSource == null){
            landingBeachImageSource ="default1.jpg";
        }
        landingBeachImageSource = landingBeachImageSource.replace('-','_');
        int fileExtension = landingBeachImageSource.indexOf('.');

        Log.d("SetImage"," file before parse "+landingBeachImageSource);
        landingBeachImageSource = landingBeachImageSource.substring(0,fileExtension);
        String uri = "@drawable/"+landingBeachImageSource;
        Log.d("SetImage"," this is the file path: "+uri);
        int fileID =0;

        try{
            fileID = R.drawable.class.getField(landingBeachImageSource).getInt(null);
        }catch(IllegalAccessException e){
            Log.d("getImageIDError","Error getting image");
        }catch(NoSuchFieldException e2){
            Log.d("getImageIDError","no Icon found");
        }
        landingBeachImageView = findViewById(R.id.landingBeachImageView);
        //((ImageView)) findViewById(R.id.BeachImage).setImageResource(fileID));
        landingBeachImageView.setImageResource(fileID);



        //int imageResouce = mainView.getResources().getIdentifier(uri,null,mainView.getActivty().getPackageName());
        // Drawable res = mainView.getResources().getDrawable(imageResouce);

    }

}
