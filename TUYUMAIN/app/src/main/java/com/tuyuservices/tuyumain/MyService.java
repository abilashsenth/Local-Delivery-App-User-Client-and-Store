package com.tuyuservices.tuyumain;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyService extends Service {

    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private String TAG = "PartnerServiceTag";
    private String partnerID;
    private FusedLocationProviderClient fusedLocationClient;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private String shopID;


    @Override
    public void onCreate() {
        super.onCreate();
        client = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        partnerID = intent.getStringExtra("partnerID");
        shopID = intent.getStringExtra("shopID");
        Log.e("partnerID", partnerID);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        try {
            client.requestLocationUpdates(locationRequest, locationCallback, null);

        } catch (SecurityException ignore) {
            Log.e("AppLocationService", "SecurityException - " + ignore.toString(), ignore);
        }
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

        //timerTask();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SERVICEE", "DESTROYED");
        stopSelf();
        client.removeLocationUpdates(locationCallback );

    }





    private void writeLocationToDatabase(double latitude, double longitude) {

        //constantly writes latitude and longitude to the database every five seconds while logged in
       // Log.e("TAG", "DATABASE OVERWRITE");
       //Log.e("TAG", "DATABASE OVERWRITE"+ latitude);
       //Log.e("TAG", "DATABASE OVERWRITE" + longitude);


        mRef.child("PARTNER").child(shopID).child(partnerID).child("LATITUDE").setValue(latitude);
        mRef.child("PARTNER").child(shopID).child(partnerID).child("LONGITUDE").setValue(longitude);
    }

    private final LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() != 0) {
                Location location = locationList.get(0);
             //   Log.e("AppLocationService", "Latitude  - " +location.getLatitude()+", longitude  - " +location.getLongitude() );
                writeLocationToDatabase(location.getLatitude(), location.getLongitude());
            }
        }
    };



    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/



}
