package com.example.beachbluenoser;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class AddFavBeach {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseFirestore beachBluenoserDB;
    private ArrayList<String> favoriteBeachNames;

    public AddFavBeach(FirebaseUser firebaseUser) {
        this.currentUser = firebaseUser;
        this.beachBluenoserDB = FirebaseFirestore.getInstance();
    }

    public void addFavoriteBeach(String beachName) {
        String userID = currentUser.getUid();
        DocumentReference documentReference = beachBluenoserDB.collection("BBUSERSTABLE-PROD").document(userID);

        // Use FieldValue.arrayUnion() to add the beach to the existing list of favorite beaches
        documentReference.update("favBeaches", FieldValue.arrayUnion(beachName))
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Log.d("AddFavBeach", "Beach added to favorites: " + beachName);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("AddFavBeach", "Error adding beach to favorites", e);
                });
    }

    public void removeFavoriteBeach(String beachName) {
        String userID = currentUser.getUid();
        DocumentReference documentReference = beachBluenoserDB.collection("BBUSERSTABLE-PROD").document(userID);

        // Use FieldValue.arrayRemove() to remove the beach from the list of favorite beaches
        documentReference.update("favBeaches", FieldValue.arrayRemove(beachName))
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Log.d("RemoveFavBeach", "Beach removed from favorites: " + beachName);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("RemoveFavBeach", "Error removing beach from favorites", e);
                });
    }

    public void fetchFavoriteBeachNames() {
        String userID = currentUser.getUid();
        DocumentReference documentReference = beachBluenoserDB.collection("BBUSERSTABLE-PROD").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists() && document.contains("favBeaches")) {
                        // Get the favBeaches array from the document
                        List<String> favBeaches = (List<String>) document.get("favBeaches");
                        favoriteBeachNames.clear();
                        favoriteBeachNames.addAll(favBeaches);
                    }
                } else {
                    // Handle failure
                    Log.e("AddFavBeach", "Error getting favorite beaches", task.getException());
                }
            }
        });
    }

    public ArrayList<String> getFavoriteBeachNames() {
        return favoriteBeachNames;
    }
}
