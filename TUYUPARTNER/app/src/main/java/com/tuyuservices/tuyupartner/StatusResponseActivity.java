package com.tuyuservices.tuyupartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ncorti.slidetoact.SlideToActView;

import java.util.List;
import java.util.Locale;

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

        mSlideToActView1 = (SlideToActView) findViewById(R.id.slider);
        mSlideToActView2 = (SlideToActView) findViewById(R.id.finish_slider);


        mOrder = (Orders) getIntent().getSerializableExtra("Orders");
        latitude = mOrder.getLatitude();
        longitude = mOrder.getLongitude();

        call= findViewById(R.id.callUser);
        assignValues();
        orderAcceptEventCallbacks();
    }

    private void assignValues() {

        oid = (TextView) findViewById(R.id.oid);
        shopIDTextView = (TextView) findViewById(R.id.shopID);
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
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/



    private void orderFinished() {
        Toast.makeText(getApplicationContext(), "Order Finished!!", Toast.LENGTH_LONG).show();
        call.setVisibility(View.GONE);
        databaseReference.child("ORDERSPLACED").child(mOrder.getOID()).child("STATUS").setValue("FINISHED");
        databaseReference.child("FINISHED").child(mOrder.getOID()).child(shopID).child(partnerID).setValue("FINISHED");
        deleteDoneTask();


        Intent backIntent = new Intent(StatusResponseActivity.this, TaskActivity.class);
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

    public void mapIntent(View v){
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+Float.parseFloat(latitude)+","+Float.parseFloat(longitude)));
        startActivity(intent);
    }


}
