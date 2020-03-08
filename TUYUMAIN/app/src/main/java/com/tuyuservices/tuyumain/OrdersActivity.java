package com.tuyuservices.tuyumain;

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

public class OrdersActivity extends AppCompatActivity {

    private String TAG= "TAGG";
    List<String> numbers;
    List<Orders> mList;



    DatabaseReference databaseReference;
    String fBaseURL = "https://tuyuservices.firebaseio.com/";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        numbers = new ArrayList<>();
        mList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        checkDBChange();
        Log.e("TAG", String.valueOf(mList.size()));
    }
    int count = 0;

    Orders orders = new Orders();
    ValueEventListener valueEventListener;

    private void checkDBChange() {
        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.child("ORDERSPLACED").getChildren()) {
                    String number = (String) ds.getKey();
                    numbers.add(number);
                }
                for (String num : numbers) {
                    for (DataSnapshot ds : dataSnapshot.child("ORDERSPLACED").child(num).getChildren()) {
                        String value = (String) ds.getKey();
                        String price = String.valueOf(ds.getValue());
                        if (value.equals("NAME")) {
                            orders.setName(price);
                        } else if (value.equals("ADDRESS")) {
                            orders.setAddress(price);
                        } else if (value.equals("TIME")) {
                            orders.setTime(price);
                        } else if (value.equals("DATE")) {
                            orders.setDate(price);
                        } else if (value.equals("TIMEPREFERENCE")) {
                            orders.setTimepreference(price);
                        } else if (value.equals("SERVICESORDERED")) {
                            orders.setServicesordered(price);
                        } else if (value.equals("TOTALAMOUNT")) {
                            orders.setTotalAmount(price);
                        }

                    }
                    orders.setNumber(String.valueOf(num));
                    Log.e("num", num);
                    mList.add(orders);
                    orders = new Orders();
                }
                mRef.removeEventListener(valueEventListener);
                setupCustomRecyclerView(mList);


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


        ListAdapter listAdapter;

        public void setupCustomRecyclerView(List<Orders> list){
            listAdapter = new ListAdapter(list);
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
        Intent intent = new Intent(OrdersActivity.this, PartnerActivity.class);
        intent.putExtra("isAssignment", true);
        intent.putExtra("mOrder", mList.get(position));
        startActivity(intent);
    }


    public void openPartnersView(View view) {
        Intent intent = new Intent(OrdersActivity.this, PartnerActivity.class);
        intent.putExtra("isAssignment", false);
        startActivity(intent);

    }


    public void refreshButton(View v){
        recreate();
    }

}
