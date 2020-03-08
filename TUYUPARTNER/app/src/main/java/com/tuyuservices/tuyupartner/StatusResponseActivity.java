package com.tuyuservices.tuyupartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class StatusResponseActivity extends AppCompatActivity {
    Button call;
    Orders mOrder;
    String partnerID;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private String TAG = "tag";
    List<String> numbers;
    List<Orders> mList;
    SlideToActView mSlideToActView1, mSlideToActView2;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mPostReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_response);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        partnerID = LoadID();
        mSlideToActView1 = (SlideToActView) findViewById(R.id.slider);
        mSlideToActView2 = (SlideToActView) findViewById(R.id.finishSlider);


        mOrder = (Orders) getIntent().getSerializableExtra("Orders");
        call= findViewById(R.id.callUser);
        assignValues();
        setupEventCallbacks();
    }

    private void assignValues() {

        TextView dateText = (TextView)  findViewById(R.id.date);
        TextView timeText = (TextView) findViewById(R.id.time);
        TextView slotTiming = (TextView) findViewById(R.id.slotTiming);

        TextView nameText = (TextView)  findViewById(R.id.name);
        TextView addressText = (TextView)  findViewById(R.id.address);
        TextView numberText = (TextView) findViewById(R.id.number);

        TextView total= (TextView) findViewById(R.id.total_amount);
        TextView serviceList = (TextView) findViewById(R.id.service_list);
        dateText.setText(mOrder.getDate());
        timeText.setText(mOrder.getTime());
        slotTiming.setText(mOrder.getTimepreference());
        nameText.setText(mOrder.getName());
        numberText.setText(mOrder.getNumber());
        addressText.setText(mOrder.getAddress());
        total.setText(mOrder.getTotalAmount());
        serviceList.setText(mOrder.getServicesordered());

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
                orderAccepted();
                mSlideToActView1.setVisibility(View.GONE);
                mSlideToActView2.setVisibility(View.VISIBLE);
                setupEventCallbacks2();
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

    private void setupEventCallbacks2() {
        final SlideToActView slide = findViewById(R.id.finishSlider);
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

    private void orderFinished() {
        Toast.makeText(getApplicationContext(), "Order Accepted!!", Toast.LENGTH_LONG).show();
        call.setVisibility(View.GONE);
        databaseReference.child("FINISHED").child(mOrder.getNumber()).setValue(partnerID);
        //goes back to task intent
        deleteDoneTask();
        databaseReference.child("STATUS").child(mOrder.getNumber()).setValue("FINISHED");

        Intent backIntent = new Intent(StatusResponseActivity.this, TaskActivity.class);
        startActivity(backIntent);
    }

    private void deleteDoneTask() {
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("ORDERSPLACED").child(mOrder.getNumber());
        mPostReference.removeValue();
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("ASSIGNED").child(mOrder.getNumber());
        mPostReference.removeValue();
    }


    private void orderAccepted() {
        Toast.makeText(getApplicationContext(), "Order Accepted!!", Toast.LENGTH_LONG).show();
        call.setVisibility(View.VISIBLE);
        databaseReference.child("STATUS").child(mOrder.getNumber()).setValue("ACCEPTED");
        databaseReference.child("ACCEPTED").child(mOrder.getNumber()).setValue(partnerID);




    }


    public void callUser(View view) {
    }
    public String LoadID(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("UID", "NULL");
    }
}
