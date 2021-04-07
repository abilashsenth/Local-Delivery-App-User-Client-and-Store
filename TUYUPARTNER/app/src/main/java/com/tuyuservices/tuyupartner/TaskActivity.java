package com.tuyuservices.tuyupartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    private static final int PERMISSION_FINE_LOCATION = 20;
    boolean loggedIn;
    String partnerID;
    private SharedPreferences sharedPreferences;
    private double latitude;
    private double longitude;
    private boolean locationPermission;

    DatabaseReference databaseReference;
    private String TAG = "tag", shopID;
    List<String> orderIDs;
    List<Orders> orderList;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        loggedIn = LoadLoginBool();
        checkLoggedIn();
        checkLocationPermission();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        partnerID = sharedPreferences.getString("partnerID", "NULL");
        shopID = sharedPreferences.getString("shopID", "NULL");

        Log.e("partnerID is ", partnerID);
        if(locationPermission){
            Intent intent = new Intent(TaskActivity.this, MyService.class);
            intent.putExtra("partnerID", partnerID);
            intent.putExtra("shopID", shopID);
            startService(intent);
        }

        orderIDs = new ArrayList<>();
        orderList = new ArrayList<>();

        getOrderIDList();
        getOrders();
    }


    ValueEventListener valueEventListener;
    private void getOrderIDList() {
        // Read from the database
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               // This method is called once with the initial value and again
               // whenever data at this location is updated.
               for (DataSnapshot ds : dataSnapshot.child("ASSIGNED").getChildren()) {
                   String orderID = String.valueOf(ds.getKey());
                   for(DataSnapshot ds1: ds.getChildren()){
                       if((ds1.getKey().equals(shopID)) && (ds1.child(partnerID).exists())){
                           orderIDs.add(orderID);
                       }
                   }
               }
               databaseReference.removeEventListener(valueEventListener);
           }

           @Override
           public void onCancelled(DatabaseError error) {
               // Failed to read value
               Log.w(TAG, "Failed to read value.", error.toException());
           }
       });
    }

    Orders order = new Orders();
    ArrayList<Orders> ordersArrayList;
    ArrayList<Product> productArrayList;

    ValueEventListener cartValueEventListener;
    public void getOrders() {
        // Read from the database
        ordersArrayList = new ArrayList<Orders>();
        productArrayList = new ArrayList<Product>();
        cartValueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(String key:orderIDs){

                    for (DataSnapshot ds : dataSnapshot.child("ORDERSPLACED").getChildren()) {
                            String OID = key;

                        String shopID = String.valueOf(ds.child("SHOPID").getValue());
                        Log.e("ORDERTAGG", shopID);

                        String status = String.valueOf(ds.child("STATUS").getValue());

                            String name = String.valueOf(ds.child("NAME").getValue());
                            String number = String.valueOf(ds.child("NUMBER").getValue());
                            String address = String.valueOf(ds.child("ADDRESS").getValue());
                            String date = String.valueOf(ds.child("DATE").getValue());
                            String time = String.valueOf(ds.child("TIME").getValue());
                            String timePreference = String.valueOf(ds.child("TIMEPREFERENCE").getValue());
                            String totalAmount = String.valueOf(ds.child("TOTALAMOUNT").getValue());
                            String paymentMethod = String.valueOf(ds.child("PAYMENTMETHOD").getValue());
                        String latitude = String.valueOf(ds.child("LATITUDE").getValue());
                        String longitude = String.valueOf(ds.child("LONGITUDE").getValue());

                        String totalProducts = "";
                            StringBuilder sb = new StringBuilder();

                            for (DataSnapshot p : ds.child("ORDERS").getChildren()) {
                                String productName = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("NAME").getValue());
                                String productPrice = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("PRICE").getValue());
                                String productThumbnailURL = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("THUMBNAILURL").getValue());
                                Log.e("PRODUCTNAME", String.valueOf(p.getValue()));
                                totalProducts = sb.append(productName+"/-/ ").toString();
                            }
                            order = new Orders(OID, shopID, status, name, number, address, date, time, timePreference, totalAmount, paymentMethod, totalProducts, latitude, longitude);
                            ordersArrayList.add(order);

                    }

                }
                setupOrdersRecyclerView(ordersArrayList);
                databaseReference.removeEventListener(cartValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }



    /**
     *the recyclerview setup of the first list where the recyclerview contains an image and title
     *
     **/

    ListAdapterStatusResponse listAdapterStatusResponse;
    public void setupOrdersRecyclerView(List<Orders> ordersList) {
        listAdapterStatusResponse = new ListAdapterStatusResponse(ordersList, getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(listAdapterStatusResponse);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Write your code here
                //pass servicetag and then the string[position]
                Log.e("TASKACTIVITY RCLR", " " + position);
                startResponseActivity(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private void startResponseActivity(int position) {
        //position corresponds to the position of mList which was chosen;
        Orders mOrder = ordersArrayList.get(position);
        Intent responseIntent  =new Intent(TaskActivity.this, StatusResponseActivity.class);
        responseIntent.putExtra("Orders", mOrder);
        startActivity(responseIntent);
    }


    private void checkLoggedIn() {
        if(!loggedIn){
            //user is not logged in. go back to login screen
            Intent loginIntent = new Intent(TaskActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            Toast.makeText(getApplicationContext(), "please login to continue", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean LoadLoginBool(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getBoolean("login", false);
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
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //now that there's permission go for address check
                    locationPermission = true;
                }else{
                    //permission denied
                    //TODO HANDLE
                }
                return;
            }

        }
    }



    //misc
    public void refreshButton(View v){
        recreate();
    }

    public void partnerProfile(View view) {
        Intent intent = new Intent(TaskActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    public void dialUserIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(intent);
    }

}
