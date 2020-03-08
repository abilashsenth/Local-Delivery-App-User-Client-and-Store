package com.tuyuservices.tuyu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFinalActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String number, name, address, date, time, timepreference, services;
    int totalprice;
    TextView statusText;
    private static final String TAG = "Fbase" ;
    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private double latitude;
    private double longitude;
    String UID;
    private ValueEventListener valueEventListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_final);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        number = intent.getStringExtra("number");
        Log.e("NUMBERFINAL", number);
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        timepreference = intent.getStringExtra("timepreference");
        services = intent.getStringExtra("services");
        totalprice= intent.getIntExtra("totalprice",0);
        statusText = (TextView) findViewById(R.id.statustext);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        writeNewService(number, name, address, date, time, timepreference, totalprice, services);
        checkCoarseAddress();
        checkDbChange();


        statusText.setText("Waiting To Recieve Order");
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    ValueEventListener valueEventListener ;


    private void checkDbChange() {


        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e("CHECK", "CHECK1");

                for (DataSnapshot ds : dataSnapshot.child("STATUS").getChildren()) {
                    String key = (String) ds.getKey();
                    String value = (String) ds.getValue();
                    if (key.equals(number)) {
                        switch (value){
                            case "ASSIGNED": //the order is assigned to a partner
                                statusText.setText("The order has been assigned to a partner and will be on your way soon");
                                break;
                            case "ACCEPTED":
                                statusText.setText("ORDER ACCEPTED");
                                getLiveLocationAndId();
                                break;
                            case "FINISHED":
                                statusText.setText("ORDER FINIShED");
                                openRatingView();
                                mRef.child("STATUS").child(number).setValue("DONE");

                                break;
                            default:
                                statusText.setText("WAiting to be assigned");

                                break;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




    }

    private void getLiveLocationAndId() {

        // Read from the database
        valueEventListener2 = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e("CHECK", "CHECK2");

                for (DataSnapshot ds : dataSnapshot.child("ACCEPTED").getChildren()) {
                    String key = (String) ds.getKey();
                    String value = (String) ds.getValue();
                    if (key.equals(number)) {
                        UID = value;
                        updateLocation(UID);
                    }
                }
                mRef.removeEventListener(valueEventListener2);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    private void openRatingView() {
        //TODOOPEN RATING VIEW
        Intent intent = new Intent(MapsFinalActivity.this, RatingsActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("UID", UID );
        startActivity(intent);
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
                List<Address> addresses = mGeocoder.getFromLocation(mLocation.getLatitude(),
                        mLocation.getLongitude(), 1);
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                String address1 = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                //now onto updating onto the textviews
                Log.e("Address", address1);
                Log.e("Address", city);

                Log.e("LATLONG", " " +latitude+longitude);
                moveMap();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    private void moveMap() {
        // Add a marker in Sydney and move the camera
        Log.e("LATLONG", " " +latitude+longitude);
        LatLng userHome = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(userHome).title("Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userHome));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userHome,20));

    }


        Partner partner = new Partner();
    private void updateLocation(final String UIDx) {
        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.child("LOCATION").child(UIDx).getChildren()) {
                    double latitude = 0, longitude=0;
                    String name = (String) ds.getKey();
                    partner.setUID(UIDx);

                    if(name.equals("LATITUDE")){
                        latitude = (double) ds.getValue();
                        partner.setLat(latitude);

                    }else if(name.equals("LONGITUDE")){
                        longitude = (double) ds.getValue();
                        partner.setLongitude(longitude);

                    }

                }

                Log.e("num", UIDx+"  "+partner.getLat()+"   "+partner.getLongitude());
                // Add a marker in Sydney and move the camera
                LatLng partnerLoc = new LatLng(partner.getLat(), partner.getLongitude());
                mMap.addMarker(new MarkerOptions().position(partnerLoc).title("Marker in partner"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(partnerLoc));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(partnerLoc,15));





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
