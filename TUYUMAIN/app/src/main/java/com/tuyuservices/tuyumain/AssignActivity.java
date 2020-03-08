package com.tuyuservices.tuyumain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ncorti.slidetoact.SlideToActView;

public class AssignActivity extends AppCompatActivity {

    TextView name, uid, number, date, time, timepreference, address, services, servicePrice;
    private Orders mOrder;
    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    String UID;
    String num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();


        name = (TextView) findViewById(R.id.name);
        uid = (TextView) findViewById(R.id.UID);
        number = (TextView) findViewById(R.id.number);
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        timepreference = (TextView) findViewById(R.id.timepreference);
        services = (TextView) findViewById(R.id.services);
        address = (TextView) findViewById(R.id.address);
        servicePrice = (TextView) findViewById(R.id.serviceprice);

        mOrder = new Orders();
        UID = getIntent().getStringExtra("UID");
        mOrder = (Orders) getIntent().getSerializableExtra("mOrder");
        Log.e("HELLO", mOrder.getNumber());
        num = mOrder.getNumber();
        name.setText(mOrder.getName());
        number.setText(mOrder.getNumber());
        address.setText(mOrder.getAddress());
        date.setText(mOrder.getDate());
        time.setText(mOrder.getTime());
        timepreference.setText(mOrder.getTimepreference());
        services.setText(mOrder.getServicesordered());
        servicePrice.setText(mOrder.getTotalAmount());
        uid.setText(UID);


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
                Toast.makeText(getApplicationContext(), "ORDER FROM "+number+ "ASSIGNED TO "+UID,
                        Toast.LENGTH_LONG).show();
                assignOrder();
                openHomeIntent();

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
        mRef.child("ASSIGNED").child(num).setValue(UID);
        mRef.child("STATUS").child(num).setValue("ASSIGNED");

    }

}
