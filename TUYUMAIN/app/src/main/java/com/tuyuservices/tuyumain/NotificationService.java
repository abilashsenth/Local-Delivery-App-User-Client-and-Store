package com.tuyuservices.tuyumain;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

import java.util.ArrayList;

public class NotificationService extends Service {
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRefNotif;
    String OID;
    int notificationID =0;
    private String TAG = "PartnerServiceTag";
    private String shopID;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        shopID = intent.getStringExtra("shopID");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        OID = "null";
        mRefNotif = mFirebaseDatabase.getReference().child("NEW");
        Log.e("shopNotificationService", "ENABLED");


        try {
            getOrderNotifData(shopID);
        } catch (SecurityException ignore) {
            Log.e("AppLocationService", "SecurityException - " + ignore.toString(), ignore);
        }
        //timerTask();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("shopServiceNotification", "DESTROYED");
        stopSelf();
    }



    ArrayList<Product> productArrayListNotif;
    ArrayList<Orders> mOrderListNotif, referenceListNotif;
    ValueEventListener childValueEventListenerNotif;
    Orders order;
    public void getOrderNotifData(final String shopIDData) {
        // Read from the database
        productArrayListNotif = new ArrayList<Product>();
        mOrderListNotif = new ArrayList<>();
        final int[] count = {0};
        referenceListNotif = new ArrayList<>();
        childValueEventListenerNotif = mRefNotif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("NOTIFICATIONSERViCE", String.valueOf(dataSnapshot.child(shopID).getValue()));
                Log.e("NOTIFICATIONSERViCE1", OID);
                if(!String.valueOf(dataSnapshot.child(shopID).getValue()).equals(OID)){
                    OID = String.valueOf(dataSnapshot.child(shopIDData).getValue());
                    notificationCall(OID, "SAMPLETIMEPREF");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void notificationCall(String oid, String timePreference) {

        NotificationManager mNotificationManager;
        Log.e("NOTIFICATIONSERVX", OID);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this.getApplicationContext(), NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("TimePreference"+timePreference);
        bigText.setBigContentTitle(oid);
        bigText.setSummaryText("New Order");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Water360 SHOP");
        mBuilder.setContentText("New order for your shop");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION ));
        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(notificationID, mBuilder.build());
        notificationID++;
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/



}
