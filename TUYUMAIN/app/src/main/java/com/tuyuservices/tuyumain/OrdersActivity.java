package com.tuyuservices.tuyumain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class OrdersActivity extends AppCompatActivity {

    private static final int PERMISSION_FINE_LOCATION =12 ;
    private String TAG= "ORDERSACTIVITY";
    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private RecyclerView mRecyclerView;
    private SharedPreferences sharedPreferences;
    private String shopID;
    Switch toggleOnline, toggleDeliverMethod;
    boolean deliverMyself, onlineState;
    private boolean locationPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        checkDBChange();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        shopID = sharedPreferences.getString("shopID", "NULL");
        checkLocationPermission();
        setTogglers();

    }




    private void setTogglers() {

        toggleOnline = (Switch) findViewById(R.id.offline_online_toggle);
        toggleDeliverMethod = (Switch) findViewById(R.id.delivery_preference_toggle);
        deliverMyself = true; onlineState = true;
        toggleOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 onlineState = isChecked;
                Log.e("SHOPIDX", shopID);
                mRef.child("SHOP").child(shopID).child("ONLINESTATUS").setValue(String.valueOf(onlineState));
                Log.e("onlineState", String.valueOf(isChecked));

                if(locationPermission){
                    if(onlineState){
                        Intent intent = new Intent(OrdersActivity.this, MyService.class);
                        intent.putExtra("partnerID", "PARTNER"+shopID+"X");
                        intent.putExtra("shopID", shopID);
                        startService(intent);

                        Intent intent2 = new Intent(OrdersActivity.this, NotificationService.class);
                        intent2.putExtra("partnerID", "PARTNER"+shopID+"X");
                        intent2.putExtra("shopID", shopID);
                        startService(intent2);

                    }else {
                        stopService(new Intent(OrdersActivity.this,MyService.class));
                        stopService(new Intent(OrdersActivity.this,NotificationService.class));
                    }
                }
            }
        });

        toggleDeliverMethod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                deliverMyself = isChecked;
                Log.e("delivermyself", String.valueOf(isChecked));

            }
        });

    }

    ArrayList<Orders> ordersArrayList;
    ArrayList<Product> productArrayList;
    ValueEventListener cartValueEventListener;
    Orders order;
    public void checkDBChange() {
        // Read from the database
        ordersArrayList = new ArrayList<Orders>();
        productArrayList = new ArrayList<Product>();
        cartValueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.child("ORDERSPLACED").getChildren()) {
               //     for(DataSnapshot snapshot: ds.getChildren()){
                        //adding every order which is not finished by status.
                        if(!String.valueOf(ds.child("STATUS").getValue()).equals("FINISHED")) {
                            String OID = ds.getKey();
                            String shopID = String.valueOf(ds.child("SHOPID").getValue());
                            String status = String.valueOf(ds.child("STATUS").getValue());
                            String name = String.valueOf(ds.child("NAME").getValue());
                            String number = String.valueOf(ds.child("NUMBER").getValue());
                            String address = String.valueOf(ds.child("ADDRESS").getValue());
                            String date = String.valueOf(ds.child("DATE").getValue());
                            String time = String.valueOf(ds.child("TIME").getValue());
                            String timePreference = String.valueOf(ds.child("TIMEPREFERENCE").getValue());
                            String totalAmount = String.valueOf(ds.child("TOTALAMOUNT").getValue());
                            String paymentMethod = String.valueOf(ds.child("PAYMENTMETHOD").getValue());
                            String totalProducts = "";
                            StringBuilder sb = new StringBuilder();

                            for (DataSnapshot p : ds.child("ORDERS").getChildren()) {
                                String productName = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("NAME").getValue());
                                String productPrice = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("PRICE").getValue());
                                String productThumbnailURL = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("THUMBNAILURL").getValue());
                                Log.e("PRODUCTNAME", String.valueOf(p.getValue()));
                                totalProducts = sb.append(productName+"/-/ ").toString();
                            }
                            order = new Orders(OID, shopID, status, name, number, address, date, time, timePreference, totalAmount, paymentMethod, totalProducts);
                            ordersArrayList.add(order);

                        }

                   // }
                }
                setupOrdersRecyclerView(ordersArrayList);
                mRef.removeEventListener(cartValueEventListener);
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


        ListAdapterOrders listAdapter;

        public void setupOrdersRecyclerView(ArrayList<Orders> list){
            listAdapter = new ListAdapterOrders(list, getApplicationContext());
            mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
            mRecyclerView.setHasFixedSize(true);


            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);


            mRecyclerView.setAdapter(listAdapter);
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    // Write your code here
                    //pass servicetag and then the string[position]
                    Log.e("Tag"," "+position );
                    openPartnerWithOrder(position);
                }

                @Override
                public void onLongClick(View view, int position) {

            }
        }));


    }

    private void openPartnerWithOrder(int position) {
            if(deliverMyself){
                Intent intent = new Intent(OrdersActivity.this, PartnerActivity.class);
                intent.putExtra("isAssignment", true);
                intent.putExtra("mOrder", ordersArrayList.get(position));
                intent.putExtra("deliverByMyself", true);
                startActivity(intent);
            }else{
                Intent intent = new Intent(OrdersActivity.this, PartnerActivity.class);
                intent.putExtra("isAssignment", true);
                intent.putExtra("mOrder", ordersArrayList.get(position));
                intent.putExtra("deliverByMyself", false);
                startActivity(intent);
            }

    }


    public void openPartnersView(View view) {
        Intent intent = new Intent(OrdersActivity.this, PartnerActivity.class);
        intent.putExtra("isAssignment", false);
        startActivity(intent);

    }


    public void refreshButton(View v){
        recreate();
    }

    public void managePartnerActivity(View view) {
            Intent intent = new Intent(OrdersActivity.this, ManagePartnerActivity.class);
            startActivity(intent);
    }

    public void shopProfile(View view) {
        Intent intent = new Intent(OrdersActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
    public void dialUserIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(intent);
    }

    public void manageFinancesActivity(View view) {
        Intent intent = new Intent(OrdersActivity.this, ManageFinancesActivity.class);
        startActivity(intent);
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

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
}
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

