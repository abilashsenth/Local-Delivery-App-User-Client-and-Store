package com.tuyuservices.tuyu;

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


public class UserNotificationService extends Service {
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRefNotif;
    String OID;
    int notificationID =0;
     String shopID =".";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("UNS", "onstart");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        shopID = intent.getStringExtra("shopID");
        OID = intent.getStringExtra("OID");
        mRefNotif = mFirebaseDatabase.getReference();
        Log.e("shopNotificationService", "ENABLED");
        try {
            getOrderNotifData(OID);
        } catch (SecurityException ignore) {
            Log.e("AppLocationService", "SecurityException - " + ignore.toString(), ignore);
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("shopServiceNotification", "DESTROYED");
        stopSelf();
    }



    ValueEventListener valueEventListenerNotif;
    Orders order;
    public void getOrderNotifData(final String oidData) {

        valueEventListenerNotif = mRefNotif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e("NOTIF", String.valueOf(dataSnapshot.child("ORDERSPLACED").child(OID).child("STATUS").getValue()));
                String value = String.valueOf(dataSnapshot.child("ORDERSPLACED").child(OID).child("STATUS").getValue());
                String msg;
                switch (value) {
                    case "NEW":
                         msg = ("ORDER Placed");
                         notificationCall(shopID, msg);
                        break;
                    case "ASSIGNED": //the order is assigned to a partner
                         msg = ("order's assigned to partner &be on your way");
                        notificationCall(shopID, msg);

                        break;
                    case "ACCEPTED":
                        msg = ("ORDER ACCEPTED");
                        notificationCall(shopID, msg);

                        break;
                    case "FINISHED":
                        msg = ("ORDER FINIShED");
                        notificationCall(shopID, msg);

                        break;
                    default:
                        break;
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void notificationCall(String shopName, String message) {

        NotificationManager mNotificationManager;
        Log.e("NOTIFICATIONSERVX", OID);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this.getApplicationContext(), UserNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("bigtext");
        bigText.setBigContentTitle(shopName);
        bigText.setSummaryText("New Order");
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Water360");
        mBuilder.setContentText(message);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION ));
        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
        notificationID++;

    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/




}
