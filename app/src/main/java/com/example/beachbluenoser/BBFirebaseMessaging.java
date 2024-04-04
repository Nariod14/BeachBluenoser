package com.example.beachbluenoser;

import android.os.Looper;
import android.util.Log;
import android.os.Handler;
import android.widget.Toast;

import static android.content.ContentValues.TAG;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class BBFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());


        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }

            sendNotification(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String from, String body){
        new Handler(Looper.getMainLooper()).post(new Runnable(){

            @Override
            public void run(){
                try {
                    Log.d(TAG, "Message from: " + from + ", body: " + body);
                } catch (Exception e) {
                    Log.e(TAG, "Error logging message: " + e.getMessage());
                }
            }

        });
    }


}