package com.tuyuservices.tuyu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


    private SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private ArrayList<Orders> orderList;
    private String userNumber;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadProfileFragment();
    }

    //to load the user credentials for profile fragment

    private void loadProfileFragment() {
        String number= LoadNum();
        String name = LoadName();
        String address = LoadAddress();
        userNumber = number;
        Log.e("number", number);
        Log.e("name", name);

        TextView numberText = (TextView)  findViewById(R.id.profile_number);
        numberText.setText(number);
        TextView nameText = (TextView) findViewById(R.id.profile_name);
        nameText.setText(name);
        TextView addressText = (TextView) findViewById(R.id.profile_address);
        addressText.setText(address);
        
        recievePreviousOrders();


    }

    Orders order;
    private void recievePreviousOrders() {
        orderList = new ArrayList<>();
        // Read from the database
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.child("ORDERSPLACED").getChildren()) {
                    //     for(DataSnapshot snapshot: ds.getChildren()){
                    //adding every order which is not finished by status.
                    if(String.valueOf(ds.child("STATUS").getValue()).equals("FINISHED") && String.valueOf(ds.child("NUMBER").getValue()).equals(userNumber)) {
                        String OID = ds.getKey();
                        String shopID = String.valueOf(ds.child("SHOPID").getValue());
                        String shopName = String.valueOf(dataSnapshot.child("SHOP").child(shopID).child("NAME").getValue());
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
                            totalProducts = sb.append(productName + "/-/ ").toString();
                        }
                        order = new Orders(OID, shopName, status, name, number, address, date, time, timePreference, totalAmount, paymentMethod, totalProducts);
                        orderList.add(order);

                    }
                }
                setupOrdersRecyclerView(orderList);
                //mRef.removeEventListener(cartValueEventListener);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("HomeActivity- Shoplist", "Failed to read value.", error.toException());
            }
        });

    }


    ListAdapterOrders listAdapter;

    public void setupOrdersRecyclerView(ArrayList<Orders> list){
        listAdapter = new ListAdapterOrders(list, getApplicationContext(), false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_views_orders);
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
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }




    public String LoadNum(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("NUMBER", "NULL");
    }

    public String LoadName(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("NAME", "NULL");
    }
    public String LoadAddress(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("address", "NULL");
    }

    public void logOut(View v){
        SaveLoggedIn("login", false);
        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void SaveLoggedIn(String key, boolean value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
