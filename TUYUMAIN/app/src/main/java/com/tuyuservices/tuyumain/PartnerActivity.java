package com.tuyuservices.tuyumain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
    List<Partner> mList;


    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    boolean isAssignment;
    private RecyclerView mRecyclerView;
    Orders mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);


        mList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        isAssignment = getIntent().getBooleanExtra("isAssignment", false);

        checkDBChange();
        Log.e("TAG", String.valueOf(mList.size()));
        if(isAssignment){
            mOrder = new Orders();
            mOrder = (Orders) getIntent().getSerializableExtra("mOrder");
            Log.e("HELLO", mOrder.getNumber());

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
                for (DataSnapshot ds : dataSnapshot.child("PARTNER").getChildren()) {
                    String UID = (String) ds.getKey();
                    partner = new Partner();
                    partner.setUID(UID);
                    mList.add(partner);
                    Log.e("num", UID);

                }


                setupCustomRecyclerView(mList);
                mRef.removeEventListener(valueEventListener);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


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
                    String UID = mList.get(position).getUID();
                    Intent assignIntent = new Intent(PartnerActivity.this, AssignActivity.class);
                    assignIntent.putExtra("UID", UID);
                    assignIntent.putExtra("mOrder", mOrder);
                    startActivity(assignIntent);
                }else {
                    Log.e("Tag", " " + position);
                    String UID = mList.get(position).getUID();
                    Intent locationIntent = new Intent(PartnerActivity.this, MapsActivity.class);
                    locationIntent.putExtra("UID", UID);
                    startActivity(locationIntent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

}
