package com.tuyuservices.tuyumain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class AssignActivity extends AppCompatActivity {

    public TextView OID, shopIDTextView, status, name, number, address, date, time, timepreference, total, paymentMethod, productsOrdered;
    public Button callUser;
    private Orders mOrder;
    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    String partnerID, shopIDVariable;
    private SharedPreferences sharedPreferences;
    private boolean deliverByMyself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        shopIDVariable = sharedPreferences.getString("shopID", "NULL");


        OID = (TextView) findViewById(R.id.oid);
        shopIDTextView = (TextView) findViewById(R.id.shopIDx);
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
        callUser = (Button) findViewById(R.id.call_customer);

        partnerID = getIntent().getStringExtra("partnerID");
        deliverByMyself = getIntent().getBooleanExtra("deliverByMyself", false);

        mOrder = (Orders) getIntent().getSerializableExtra("mOrder");
        Log.e("HELLO", mOrder.getNumber());

        OID.setText(mOrder.getOID());
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


        setupEventCallbacks();
    }

    private void setupEventCallbacks() {
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
                Toast.makeText(getApplicationContext(), "ORDER ID "+OID+ "ASSIGNED TO "+ partnerID,
                        Toast.LENGTH_LONG).show();
                assignOrder();
                if(deliverByMyself){
                    Intent responseIntent  =new Intent(AssignActivity.this, StatusResponseActivity.class);
                    responseIntent.putExtra("Orders", mOrder);
                    startActivity(responseIntent);
                }else{
                    openHomeIntent();
                }

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

    private void openHomeIntent() {
        Intent intent = new Intent(AssignActivity.this, OrdersActivity.class );
        startActivity(intent);
    }

    private void assignOrder() {
        //assign orderID to partnerID. change orderID status to assigned


        mRef.child("ASSIGNED").child(mOrder.getOID()).child(shopIDVariable).child(partnerID).setValue("ASSIGNED");
        mRef.child("ORDERSPLACED").child(mOrder.getOID()).child("STATUS").setValue("ASSIGNED");

    }

    public void callCustomer(View view) {
        String phoneNumber = String.valueOf(number.getText());
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(intent);
    }


}
