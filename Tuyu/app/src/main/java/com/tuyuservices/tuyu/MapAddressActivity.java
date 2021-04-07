package com.tuyuservices.tuyu;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapAddressActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SharedPreferences sharedPreferences;
    String number;
    private static final int PERMISSION_FINE_LOCATION = 22;
    private double latitude, longitude;
    String address;
    TextView addressText;
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_address);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        number = LoadNum();
        addressText = (TextView) findViewById(R.id.AddressText);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        checkLocationPermission();

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

            final Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
            try {
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                List<Address> addresses = mGeocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                String address1 = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                addressText.setText(address1 + " "+city);
                address = address1 + " " +city;




                // Add a marker in Sydney and move the camera
                LatLng position = new LatLng(latitude, longitude);
                BitmapDescriptor my_location_marker = BitmapDescriptorFactory.fromResource(R.drawable.my_location_marker);
                MarkerOptions marker = new MarkerOptions().position(position).draggable(true).icon(my_location_marker);
                mMap.addMarker(marker);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.5f ) );
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onMarkerDragEnd(Marker arg0) {
                        Log.d("System out", "onMarkerDragEnd...");
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                        latitude = arg0.getPosition().latitude;
                        longitude = arg0.getPosition().longitude;
                        List<Address> addresses = null;
                        try {
                            addresses = mGeocoder.getFromLocation(latitude,  longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String address1 = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        Log.e("Address", address1);
                        addressText.setText(address1 + " "+city);
                        address = address1 + " " +city;
                    }

                    @Override
                    public void onMarkerDrag(Marker arg0) {
                    }
                });



            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }


    /**
     * permission handling shall be done here
     */

    private void checkLocationPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            //location permission required
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);

        }else{
            //location already granted
            checkCoarseAddress();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //now that there's permission go for address check
                    checkCoarseAddress();
                }else{
                    //permission denied
                    //TODO HANDLE
                    Intent intent = new Intent(MapAddressActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return;
            }

        }
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
        checkCoarseAddress();



    }

    public String LoadNum(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("NUMBER", "NULL");
    }
    public void saveLatitude(String key, double value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latitude", String.valueOf(value));
        editor.apply();
    }
    public void saveLongitude(String key, double value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("longitude", String.valueOf(value));
        editor.apply();
    }
    public void saveAddress(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", address);
        editor.apply();
    }
    public void saveLoggedIn(String key, boolean value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void ConfirmAddress(View view) {
        saveLoggedIn("login", true);
        saveLatitude("latitude", latitude);
        saveLongitude("longitude", longitude);
        saveAddress("address", address);

        Intent intent = new Intent(MapAddressActivity.this, HomeActivity.class);
        startActivity(intent);

    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
