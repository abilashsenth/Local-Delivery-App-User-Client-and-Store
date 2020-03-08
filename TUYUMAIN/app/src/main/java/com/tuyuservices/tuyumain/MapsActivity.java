package com.tuyuservices.tuyumain;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String UID;

    private String TAG = "TAGG";
    List<Partner> mList;


    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        UID = getIntent().getStringExtra("UID");
        Log.e("UID", UID);
        mList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        checkDBChange();




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    Partner partner = new Partner();


    private void checkDBChange() {
        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.child("LOCATION").child(UID).getChildren()) {
                    double latitude = 0, longitude=0;
                    String name = (String) ds.getKey();
                    partner.setUID(UID);

                    if(name.equals("LATITUDE")){
                        latitude = (double) ds.getValue();
                        partner.setLat(latitude);

                    }else if(name.equals("LONGITUDE")){
                        longitude = (double) ds.getValue();
                        partner.setLongitude(longitude);

                    }

                }

                mList.add(partner);
                Log.e("num", UID+"  "+partner.getLat()+"   "+partner.getLongitude());
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
