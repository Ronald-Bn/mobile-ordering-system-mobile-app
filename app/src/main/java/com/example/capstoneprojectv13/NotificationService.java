package com.example.capstoneprojectv13;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.capstoneprojectv13.model.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {


    public final String CHANNEL_ID = "Personal Notification";
    public static final int NOTIFICATION_ID = 101;

    private String key;
    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;
    FirebaseAuth mAuth;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        startTimer();

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

        createNotificationChannel();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stoptimertask();
        super.onDestroy();


    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            FirebaseUser user = mAuth.getInstance().getCurrentUser();
                            DatabaseReference fromPath = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference()
                                    .child("Notifications")
                                    .child(user.getUid())
                                    .child("pending");


                            fromPath.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild("notify")){
                                        NotificationDisplay();
                                        Map<String, Object> updateData = new HashMap<>();
                                        updateData.put("notify", true);
                                        snapshot.getRef().updateChildren(updateData);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d(TAG, error.getMessage());
                                }
                            });
                        }catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
            }
        };
    }

    private void changePathNotification(final DatabaseReference fromPath, final DatabaseReference toPath){
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Log.d(TAG, firebaseError.getMessage());
                        } else {
                            fromPath.removeValue();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    public void NotificationDisplay() {
        Intent landingIntent = new Intent(this, PaymentActivity.class);
        landingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        landingIntent.putExtra("cartId", "5992407292");
        landingIntent.putExtra("ordersId","-MpIZCejhIsCqxdT792k");
        PendingIntent landingPendingIntent = PendingIntent.getActivity(this, 0,
                landingIntent, PendingIntent.FLAG_ONE_SHOT);

        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("Orders")
                .child(user.getUid());


        String text = "Your Order" + user.getUid() + " " + "has been approved";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("Order Approved");
        builder.setContentText("Your Order " + user.getUid() + "\n has been approved");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(text));
        builder.setAutoCancel(true);

        builder.setContentIntent(landingPendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    private void createNotificationChannel () {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notification";
            String description = "Include all personal Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
