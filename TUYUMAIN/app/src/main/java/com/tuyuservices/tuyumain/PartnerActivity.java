package com.tuyuservices.tuyumain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PartnerActivity extends AppCompatActivity {

    private String TAG = "TAGG";
    List<Partner> mPartnerList;


    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    boolean isAssignment, deliverByMyself;
    private RecyclerView mRecyclerView;
    Orders mOrder;
    String shopID;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);


        mPartnerList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        isAssignment = getIntent().getBooleanExtra("isAssignment", false);
        deliverByMyself = getIntent().getBooleanExtra("deliverByMyself", false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        shopID = sharedPreferences.getString("shopID", "NULL");
        checkDBChange();
        Log.e("TAG", String.valueOf(mPartnerList.size()));
        if(isAssignment){

            mOrder = (Orders) getIntent().getSerializableExtra("mOrder");
            Log.e("HELLO", mOrder.getNumber());
            if(deliverByMyself){
                //auto call AssignActivity with intents sending in shopId, partnerId ("PARTNER"+(SHOPID)+"X"),
                String partnerID = "PARTNER"+shopID+"X";
                Intent assignIntent = new Intent(PartnerActivity.this, AssignActivity.class);
                assignIntent.putExtra("partnerID", partnerID);
                assignIntent.putExtra("mOrder", mOrder);
                assignIntent.putExtra("deliverByMyself", deliverByMyself);
                startActivity(assignIntent);
            }

        }


    }


    int count = 0;

    Partner partner;

    ValueEventListener valueEventListener;


    private void checkDBChange() {
        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.child("PARTNER").child(shopID).getChildren()) {
                    String partnerID = (String) ds.getKey();
                    String partnerPass  = (String) ds.child("PASS").getValue();
                    String partnerLatitude  = String.valueOf(ds.child("LATITUDE").getValue());
                    String partnerLongitude = String.valueOf(ds.child("LONGITUDE").getValue());
                    String partnerNumber =   String.valueOf(ds.child("NUMBER").getValue());

                    partner = new Partner(partnerID, partnerPass, partnerLatitude, partnerLongitude, partnerNumber);
                    mPartnerList.add(partner);
                    Log.e("num", partnerID);

                }


                setupCustomRecyclerView(mPartnerList);
                mRef.removeEventListener(valueEventListener);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


    /**
     *the recyclerview setup of the first list where the recyclerview contains an image and title
     *
     **/


    ListAdapterPartner listAdapter;

    public void setupCustomRecyclerView(List<Partner> list){
        listAdapter = new ListAdapterPartner(list);
        mRecyclerView = (RecyclerView) findViewById(R.id.partnerRecyclerView);
        mRecyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Write your code here
                //pass servicetag and then the string[position]
                if(isAssignment){
                    Log.e("Tag", " " + position);
                    String partnerID = mPartnerList.get(position).getName();
                    Intent assignIntent = new Intent(PartnerActivity.this, AssignActivity.class);
                    assignIntent.putExtra("partnerID", partnerID);
                    assignIntent.putExtra("mOrder", mOrder);
                    startActivity(assignIntent);
                }else {
                    Log.e("Tag", " " + position);
                    String partnerID = mPartnerList.get(position).getName();
                    Intent locationIntent = new Intent(PartnerActivity.this, PartnerLocationActivity.class);
                    locationIntent.putExtra("partnerID", partnerID);
                    startActivity(locationIntent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


    }

}
