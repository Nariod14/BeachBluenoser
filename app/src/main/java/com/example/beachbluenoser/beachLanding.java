package com.example.beachbluenoser;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class beachLanding extends AppCompatActivity {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public String beachName, parsedBeachName;
    public String landingBeachCapacityText;
    public String landingBeachSandyOrRockyText;
    public String landingBeachWheelchairAccessibleText;
    public String landingBeachImageSource;
    public String landingBeachVisualWaterConditionsText;
    public String landingBeachParkingText;
    public String landingBeachFloatingWheelchairText;
    public String weatherDescriptionText;
    public String temperatureText;

    public ImageView landingBeachImageView;
    public TextView landingBeachCapacityView;
    public TextView landingBeachSandyOrRockyView;
    public TextView landingBeachWheelchairAccessibleView;
    public TextView landingBeachFloatingWheelchairView;
    public TextView landingBeachParkingView;
    public TextView landingBeachNameView;
    public TextView landingBeachVisualWaterConditionsView;
    public TextView weatherView;
    public TextView weatherDescriptionView;
    public TextView humidityView;
    public TextView temperatureView;
    public TextView windView;
    public TextView cloudView;

    public String currentDate;
    public String userID;
    public String userType = "";

    public Double beachLat,beachLong;
    public String beachLocation;
    public Button mapsBtn;

    MediaPlayer mp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beach_landing);
        Bundle bundle = getIntent().getExtras();

        Button btn = findViewById(R.id.checkInSurvey);
        ImageButton backBtn = findViewById(R.id.backButton);
        mp = MediaPlayer.create(this, R.raw.click);

        if (bundle != null) {
            if (bundle.getString("beachName") != null) {
                beachName = bundle.getString("beachName");
                Log.d("beach Main Page Name ", " Name : " + beachName);
                parsedBeachName = beachName.replace(" ", "+");
            }
            if (bundle.getString("userType") != null) {
                userType = bundle.getString("userType");
            }
            else if (auth.getCurrentUser() != null) {
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
                mp.start();
                Intent backIntent = new Intent(beachLanding.this, MainActivity.class);
                startActivity(backIntent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
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

        mapsBtn = findViewById(R.id.mapsBtn);
        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                beachLocation = beachLat + "," + beachLong;
                //https://developers.google.com/maps/documentation/urls/android-intents#location_search
                //if you want maps to launch directly into navigation switch out gmmIntentUri for below
                //Uri gmmIntentUri = Uri.parse("google.navigation:q=" + parsedBeachName + "@" + beachLocation);
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + parsedBeachName + "@" + beachLocation);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
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
                    if (!(document.getData().get("sandyOrRocky") == null)) {
                        landingBeachSandyOrRockyText = "Beach type: " + document.getData().get("sandyOrRocky").toString();
                    } else {
                        landingBeachSandyOrRockyText = "Beach type: Unknown";
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
                        landingBeachWheelchairAccessibleText = "Wheelchair Accessible: Unknown";
                    }
                    if(document.get("location")!=null){
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        beachLat = geoPoint.getLatitude();
                        beachLong = geoPoint.getLongitude();
                        Log.d("Beach Location", "location : "+ beachLat +", " + beachLong);
                        beachLocation = beachLat + "," + beachLong;
                    }
                    getWeatherDetails();
                    showDataOnUI();
                } else {
                    Log.d("Beach Landing Query", "No such document");
                }
            }
        });
    }

    /**Code referenced from
     * https://github.com/sandipapps/Weather-Update
     * https://www.youtube.com/watch?v=f2oSRBwN2HY
     * */
    public void getWeatherDetails() {
        String tempUrl="https://api.openweathermap.org/data/2.5/weather?lat="+ beachLat + "&lon=" + beachLong + "&appid=895284fb2d2c50a520ea537456963d9c";
        Log.d("Beach Location", "url : "+ tempUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("Beach Location", "Response Success!");
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp")- 273.15 ;
                    double feelsLike = jsonObjectMain.getDouble("feels_like")- 273.15 ;
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    Log.d("Beach Location", "JSON OBJs: " + description +" + "+ temp);
//                    output += "Current weather of " + beachName
//                            + "\n Temp: " + df.format(temp) + " °C"
//                            + "\n Feels Like: " + df.format(feelsLike) + " °C"
//                            + "\n Humidity: " + humidity + "%"
//                            + "\n Description: " + description
//                            + "\n Wind Speed: " + wind + "m/s (meters per second)"
//                            + "\n Cloudiness: " + clouds + "%"
//                            + "\n Pressure: " + pressure + " hPa";
                    weatherDescriptionView.setText("Description: " + description);
                    weatherView.setText("Weather: "+ Math.round(temp) + " °C");
                    temperatureView.setText("Feels Like: " + Math.round(feelsLike) + " °C ");
                    humidityView.setText("Humidity: " + humidity + "%");
                    windView.setText("Wind Speed: " + wind + "m/s");
                    cloudView.setText("Cloud cover: " + clouds + "%\n");
                    Log.d("Beach Location", "Texts: " + weatherDescriptionText +" + "+ temperatureText);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
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
        landingBeachSandyOrRockyView.setText(landingBeachSandyOrRockyText);
        landingBeachWheelchairAccessibleView.setText(landingBeachWheelchairAccessibleText);
        landingBeachFloatingWheelchairView.setText(landingBeachFloatingWheelchairText);
        landingBeachParkingView.setText(landingBeachParkingText);
        landingBeachNameView.setText(beachName);
        setBeachImage();

        weatherView = findViewById(R.id.weatherTextView);
        weatherDescriptionView = findViewById(R.id.weatherDescriptionTextView);
        humidityView = findViewById(R.id.humidityTextView);
        temperatureView = findViewById(R.id.temperatureTextView);
        windView = findViewById(R.id.windTextView);
        cloudView = findViewById(R.id.cloudsTextView);
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
