package com.tuyuservices.tuyumain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncorti.slidetoact.SlideToActView;

import java.util.List;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class StatusResponseActivity extends AppCompatActivity {
    Button call;
    Orders mOrder;
    String partnerID;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    DatabaseReference databaseReference;
    private String TAG = "tag";
    List<String> numbers;
    List<Orders> mList;
    SlideToActView mSlideToActView1, mSlideToActView2;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mPostReference;
    private String shopID;
    private String latitude, longitude;
    public TextView oid, shopIDTextView, status, name, number, address, date, time, timepreference, total, paymentMethod, productsOrdered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_response);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        partnerID = sharedPreferences.getString("partnerID", "NULL");
        shopID = sharedPreferences.getString("shopID", "NULL");

        Log.e("MAINPARTNID", partnerID);
        Log.e("MAINSHOPID", shopID);


        mSlideToActView1 = (SlideToActView) findViewById(R.id.slider);
        mSlideToActView2 = (SlideToActView) findViewById(R.id.finish_slider);


        mOrder = (Orders) getIntent().getSerializableExtra("Orders");


        call= findViewById(R.id.call_user);
        assignValues();
        orderAcceptEventCallbacks();
    }




    private void assignValues() {

        oid = (TextView) findViewById(R.id.oid);
        shopIDTextView = (TextView) findViewById(R.id.shop_ID);
        status = (TextView) findViewById(R.id.status);
        name = (TextView) findViewById(R.id.name);
        number =(TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        timepreference = (TextView) findViewById(R.id.timepref);
        total = (TextView) findViewById(R.id.totalamt);
        paymentMethod = (TextView) findViewById(R.id.paymentMethod);
        productsOrdered = (TextView) findViewById(R.id.productsOrdered);


        oid.setText(mOrder.getOID());
        shopIDTextView.setText(mOrder.getShopID());
        status.setText(mOrder.getStatus());
        name.setText(mOrder.getName());
        number.setText(mOrder.getNumber());
        address.setText(mOrder.getAddress());
        date.setText(mOrder.getDate());
        time.setText(mOrder.getTime());
        timepreference.setText(mOrder.getTimepreference());
        total.setText(mOrder.getTotalAmount());
        paymentMethod.setText(mOrder.getPaymentMethod());
        productsOrdered.setText(mOrder.getTotalProducts());

    }


    private void orderAcceptEventCallbacks() {
        final SlideToActView slide = findViewById(R.id.slider);
        slide.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideComplete");
            }
        });
        slide.setOnSlideResetListener(new SlideToActView.OnSlideResetListener() {
            @Override
            public void onSlideReset(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideReset");
            }
        });
        slide.setOnSlideUserFailedListener(new SlideToActView.OnSlideUserFailedListener() {
            @Override
            public void onSlideFailed(@NonNull SlideToActView view, boolean isOutside) {
                Log.e("Tag", "\n" + " onSlideUserFailed - Clicked outside: " + isOutside);
            }
        });
        slide.setOnSlideToActAnimationEventListener(new SlideToActView.OnSlideToActAnimationEventListener() {
            @Override
            public void onSlideCompleteAnimationStarted(@NonNull SlideToActView view, float threshold) {
                Log.e("Tag", "\n" + " onSlideCompleteAnimationStarted - " + threshold + "");
            }

            @Override
            public void onSlideCompleteAnimationEnded(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideCompleteAnimationEnded");
                orderAccepted();
                mSlideToActView1.setVisibility(View.GONE);
                mSlideToActView2.setVisibility(View.VISIBLE);
                orderFinishedEventCallbacks();
            }

            @Override
            public void onSlideResetAnimationStarted(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideResetAnimationStarted");
            }

            @Override
            public void onSlideResetAnimationEnded(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideResetAnimationEnded");
            }
        });
    }

    private void orderFinishedEventCallbacks() {
        final SlideToActView slide = findViewById(R.id.finish_slider);
        slide.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideComplete");
            }
        });
        slide.setOnSlideResetListener(new SlideToActView.OnSlideResetListener() {
            @Override
            public void onSlideReset(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideReset");
            }
        });
        slide.setOnSlideUserFailedListener(new SlideToActView.OnSlideUserFailedListener() {
            @Override
            public void onSlideFailed(@NonNull SlideToActView view, boolean isOutside) {
                Log.e("Tag", "\n" + " onSlideUserFailed - Clicked outside: " + isOutside);
            }
        });
        slide.setOnSlideToActAnimationEventListener(new SlideToActView.OnSlideToActAnimationEventListener() {
            @Override
            public void onSlideCompleteAnimationStarted(@NonNull SlideToActView view, float threshold) {
                Log.e("Tag", "\n" + " onSlideCompleteAnimationStarted - " + threshold + "");
            }

            @Override
            public void onSlideCompleteAnimationEnded(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideCompleteAnimationEnded");
                orderFinished();
                mSlideToActView1.setVisibility(View.GONE);
                mSlideToActView2.setVisibility(View.GONE);
            }

            /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

            @Override
            public void onSlideResetAnimationStarted(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideResetAnimationStarted");
            }

            @Override
            public void onSlideResetAnimationEnded(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideResetAnimationEnded");
            }
        });
    }


    private void orderAccepted() {
        Toast.makeText(getApplicationContext(), "Order Accepted!!", Toast.LENGTH_LONG).show();
        call.setVisibility(View.VISIBLE);
        databaseReference.child("ORDERSPLACED").child(mOrder.getOID()).child("STATUS").setValue("ACCEPTED");
        databaseReference.child("ACCEPTED").child(mOrder.getOID()).child(shopID).child(partnerID).setValue("ACCEPTED");
    }


    private void orderFinished() {
        Toast.makeText(getApplicationContext(), "Order Finished!!", Toast.LENGTH_LONG).show();
        call.setVisibility(View.GONE);
        databaseReference.child("ORDERSPLACED").child(mOrder.getOID()).child("STATUS").setValue("FINISHED");
        databaseReference.child("FINISHED").child(mOrder.getOID()).child(shopID).child(partnerID).setValue("FINISHED");
        deleteDoneTask();


        Intent backIntent = new Intent(StatusResponseActivity.this, OrdersActivity.class);
        startActivity(backIntent);
    }

    private void deleteDoneTask() {
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("ASSIGNED").child(mOrder.getOID());
        mPostReference.removeValue();
    }




    //misc
    public void callUser(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mOrder.getNumber(), null));
        startActivity(intent);
    }
    public String LoadID(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("UID", "NULL");
    }



    ValueEventListener statusValueEventListener;
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    public void mapIntent(View v){
            final String[] lat = new String[1];
            final String[] lon = new String[1];
            statusValueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lat[0] = String.valueOf(dataSnapshot.child("ORDERSPLACED").child(mOrder.getOID()).child("LATITUDE").getValue());
                    lon[0] = String.valueOf(dataSnapshot.child("ORDERSPLACED").child(mOrder.getOID()).child("LONGITUDE").getValue());
                    Log.e("MAINLAT", lat[0]+"is the latitude");
                    Log.e("MAINLONG", lon[0]  +"is the longitude");
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr="+Float.parseFloat(lat[0])+","+Float.parseFloat(lon[0])));
                    startActivity(intent);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


    }


}

