package com.tuyuservices.tuyu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FinalActivity extends AppCompatActivity {
    String number, name, address, date, time, timepreference, services;
    int totalprice;
    private static final String TAG = "Fbase" ;
    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        Intent intent = getIntent();
        number = intent.getStringExtra("number");
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        timepreference = intent.getStringExtra("timepreference");
        services = intent.getStringExtra("services");
        totalprice= intent.getIntExtra("totalprice",0);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        writeNewService(number, name, address, date, time, timepreference, totalprice, services);

        checkDbChange();


    }


    private void checkDbChange() {


        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot ds : dataSnapshot.child("electrical").getChildren()){
                    String value = (String) ds.getKey();
                    Log.d(TAG, "Value is: " + value);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }


    private void writeNewService ( String number, String name, String address, String date,
                                   String time, String timepreference, int totalprice, String services){

        databaseReference.child("ORDERSPLACED").child(number).child("NAME").setValue(name);
        databaseReference.child("ORDERSPLACED").child(number).child("ADDRESS").setValue(address);
        databaseReference.child("ORDERSPLACED").child(number).child("DATE").setValue(date);
        databaseReference.child("ORDERSPLACED").child(number).child("TIME").setValue(time);
        databaseReference.child("ORDERSPLACED").child(number).child("TIMEPREFERENCE").setValue(timepreference);
        databaseReference.child("ORDERSPLACED").child(number).child("SERVICESORDERED").setValue(services);
        databaseReference.child("ORDERSPLACED").child(number).child("TOTALAMOUNT").setValue(totalprice);
    }


    //location data can be taken
    private Location mLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private void checkCoarseAddress() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    mLocation = location;
                    updateAddress();
                }
            }
        });



    }

    private void updateAddress() {
        if(mLocation != null){

            Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = mGeocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                String address1 = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                //now onto updating onto the textviews
                Log.e("Address", address1);
                Log.e("Address", city);




            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

}
