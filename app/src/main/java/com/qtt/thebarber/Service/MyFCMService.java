package com.qtt.thebarber.Service;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.qtt.thebarber.Common.Common;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;

public class MyFCMService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Common.updateToken(this, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //dataSend.put("update_done", "true");
        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().get("update_done") != null) {
                updateLastBooking();
                Map<String, String> dataReceived = remoteMessage.getData();
                Paper.init(this);
                Paper.book().write(Common.RATING_INFORMATION_KEY, new Gson().toJson(dataReceived));
            }


            Common.showNotification(this, new Random().nextInt(),
                    remoteMessage.getData().get(Common.TITLE_KEY),
                    remoteMessage.getData().get(Common.CONTENT_KEY),
                    null);
        }
    }

    private void updateLastBooking() {
        //get user (can get in background)
        final CollectionReference userBookingCol;
        //If app is running
        if (Common.currentUser != null) {
            ///User/+841689294631/Booking/pntPlrcepjBq6q4ITxWO
            userBookingCol = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking");
        } else { //run background
            Paper.init(this);
            String user = Paper.book().read(Common.LOGGED_KEY);
            userBookingCol = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(user)
                    .collection("Booking");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.MINUTE, 0);

        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        userBookingCol
                .whereGreaterThan("timestamp", timestamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                //update
                                DocumentReference userBookingDoc = null;

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    userBookingDoc = userBookingCol.document(documentSnapshot.getId());
                                }

                                if (userBookingDoc != null) {
                                    Map<String, Object> dataUpdate = new HashMap<>();
                                    dataUpdate.put("done", true);
                                    userBookingDoc.update(dataUpdate)
                                            .addOnFailureListener(e -> Toast.makeText(MyFCMService.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyFCMService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
