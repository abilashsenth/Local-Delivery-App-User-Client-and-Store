package com.tuyuservices.tuyupartner;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private String TAG = "tag";
    private String partnerID;
    private FusedLocationProviderClient fusedLocationClient;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;


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





        partnerID = intent.getStringExtra("UID");
        Log.e("UID", partnerID);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();


        try {
            client.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException ignore) {
            Log.e("AppLocationService", "SecurityException - " + ignore.toString(), ignore);
        }



        //timerTask();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SERVICEE", "DESTROYED");
    }





    private void writeLocationToDatabase(double latitude, double longitude) {

        //constantly writes latitude and longitude to the database every five seconds while logged in
        Log.e("TAG", "DATABASE OVERWRITE");
        Log.e("TAG", "DATABASE OVERWRITE"+ latitude);
        Log.e("TAG", "DATABASE OVERWRITE" + longitude);


        mRef.child("LOCATION").child(partnerID).child("LATITUDE").setValue(latitude);
        mRef.child("LOCATION").child(partnerID).child("LONGITUDE").setValue(longitude);
    }

    private final LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() != 0) {
                Location location = locationList.get(0);
                Log.e("AppLocationService", "Latitude  - " +location.getLatitude()+", longitude  - " +location.getLongitude() );
                writeLocationToDatabase(location.getLatitude(), location.getLongitude());
            }
        }
    };


}
