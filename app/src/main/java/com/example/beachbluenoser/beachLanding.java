package com.example.beachbluenoser;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class beachLanding extends AppCompatActivity {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseAuth auth = FirebaseAuth.getInstance();;
    public String beachName;
    public String landingBeachCapacityText;
    public String landingBeachSandyOrRockyValue;
    public String landingBeachWheelchairAccessibleText;
    public String landingBeachImageSource;
    public String landingBeachVisualWaterConditionsText;
    public String landingBeachParkingText;
    public String landingBeachFloatingWheelchairText;

    public ImageView landingBeachImageView;
    public TextView landingBeachCapacityView;
    public TextView landingBeachSandyOrRockyView;
    public TextView landingBeachWheelchairAccessibleView;
    public TextView landingBeachFloatingWheelchairView;
    public TextView landingBeachParkingView;
    public TextView landingBeachNameView;
    public TextView landingBeachVisualWaterConditionsView;
    public String currentDate;
    public String userID;
    public String userType = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beach_landing);

        Bundle bundle = getIntent().getExtras();

        Button btn = findViewById(R.id.checkInSurvey);
        ImageButton backBtn = findViewById(R.id.backButton);

        if (bundle != null) {
            if (bundle.getString("beachName") != null) {
                beachName = bundle.getString("beachName");
            }
            if (bundle.getString("userType") != null) {
                userType = bundle.getString("userType");
            }
            if (auth.getCurrentUser() != null) {
                userID = auth.getCurrentUser().getUid();
                DocumentReference userRef = db.collection("BBUsers").document(userID);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                userType = document.getData().get("userType").toString();
                                Log.d("USERTYPE ", userType);
                            }
                        }
                    }
                });
            } else {
                btn.setVisibility(View.GONE);
            }
        }

        getPreliminaryDataFromDB();

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDate = formattedDate;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(beachLanding.this, MainActivity.class);
                startActivity(backIntent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (userType.equals("Manager")) {
                    intent = new Intent(beachLanding.this, ManagementDataSurvey.class);
                } else if (userType.equals("Lifeguard")) {
                    intent = new Intent(beachLanding.this, LifeguardDataSurvey.class);
                } else {
                    intent = new Intent(beachLanding.this, UserDataSurvey.class);
                }
                intent.putExtra("beachName", beachName);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        getPreliminaryDataFromDB();
        super.onResume();
    }

    private void getPreliminaryDataFromDB() {
        DocumentReference landingBeachRef = db.collection("beach").document(beachName);
        landingBeachRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Object DataImage = document.getData().get("image");
                        String DataImageValue;
                        if (DataImage == null) {
                            DataImageValue = "imageNotFound";
                        } else {
                            DataImageValue = document.getData().get("image").toString();
                        }
                        landingBeachImageSource = DataImageValue;
                    }
                    if (!(document.getData().get("beachCapacityTextForTheDay") == null)) {
                        landingBeachCapacityText = document.getData().get("beachCapacityTextForTheDay").toString();
                    } else {
                        landingBeachCapacityText = "Beach Capacity: No data today!";
                    }
                    if (!(document.getData().get("beachVisualWaveConditionsTextForTheDay") == null)) {
                        landingBeachVisualWaterConditionsText = document.getData().get("beachVisualWaveConditionsTextForTheDay").toString();
                    } else {
                        landingBeachVisualWaterConditionsText = "Water Conditions: No data today!";
                    }
                    if (!(document.getData().get("beachParkingConForDay") == null)) {
                        landingBeachParkingText = document.getData().get("beachParkingConForDay").toString();
                    } else {
                        landingBeachParkingText = "Parking: No data today!";
                    }
                    if (!(document.getData().get("floatingWheelchair") == null)) {
                        landingBeachFloatingWheelchairText = document.getData().get("floatingWheelchair").toString();
                        if (landingBeachFloatingWheelchairText.equals("Floating Wheelchair"))
                        { landingBeachFloatingWheelchairText="Floating Wheelchair: Yes"; }
                        else {landingBeachFloatingWheelchairText="Floating Wheelchair: No"; }
                    } else {
                        landingBeachFloatingWheelchairText = "Floating Wheelchair: Unknown";
                    }
                    if (!(document.getData().get("wheelchairAccessible") == null)) {
                        landingBeachWheelchairAccessibleText = document.getData().get("wheelchairAccessible").toString();
                        if (landingBeachWheelchairAccessibleText.equals("Wheelchair Accessible"))
                        { landingBeachWheelchairAccessibleText="Wheelchair Accessible: Yes"; }
                        else {landingBeachWheelchairAccessibleText="Wheelchair Accessible: No"; }
                    } else {
                        landingBeachWheelchairAccessibleText = "Floating Wheelchair: Unknown";
                    }
                    showDataOnUI();
                } else {
                    Log.d("Beach Landing Query", "No such document");
                }
            }
        });
    }

    private void showDataOnUI() {
        landingBeachCapacityView = findViewById(R.id.landingBeachCapacityTextView);
        landingBeachSandyOrRockyView = findViewById(R.id.landingBeachSandyOrRockyTextView);
        landingBeachWheelchairAccessibleView = findViewById(R.id.landingBeachWheelchairAccessibleTextView);
        landingBeachNameView = findViewById(R.id.landingBeachNameTextView);
        landingBeachVisualWaterConditionsView = findViewById(R.id.landingBeachVisualWaterConditionsTextView);
        landingBeachFloatingWheelchairView = findViewById(R.id.landingBeachFloatingWheelchairTextView);
        landingBeachParkingView = findViewById(R.id.landingBeachParkingTextView);

        landingBeachCapacityView.setText(landingBeachCapacityText);
        landingBeachVisualWaterConditionsView.setText(landingBeachVisualWaterConditionsText);
        landingBeachSandyOrRockyView.setText(landingBeachSandyOrRockyValue);
        landingBeachWheelchairAccessibleView.setText(landingBeachWheelchairAccessibleText);
        landingBeachFloatingWheelchairView.setText(landingBeachFloatingWheelchairText);
        landingBeachParkingView.setText(landingBeachParkingText);
        landingBeachNameView.setText(beachName);
        setBeachImage();


    }

    public void setBeachImage() {

        if (landingBeachImageSource.equals("") || landingBeachImageSource == null) {
            landingBeachImageSource = "default1.jpg";
        }
        landingBeachImageSource = landingBeachImageSource.replace('-', '_');
        int fileExtension = landingBeachImageSource.indexOf('.');

        landingBeachImageSource = landingBeachImageSource.substring(0, fileExtension);
        String uri = "@drawable/" + landingBeachImageSource;
        Log.d("SetImage", " this is the file path: " + uri);
        int fileID = 0;

        try {
            fileID = R.drawable.class.getField(landingBeachImageSource).getInt(null);
        } catch (IllegalAccessException e) {
            Log.d("getImageIDError", "Error getting image");
        } catch (NoSuchFieldException e2) {
            Log.d("getImageIDError", "no Icon found");
        }
        landingBeachImageView = findViewById(R.id.landingBeachImageView);
        landingBeachImageView.setImageResource(fileID);

    }
}
