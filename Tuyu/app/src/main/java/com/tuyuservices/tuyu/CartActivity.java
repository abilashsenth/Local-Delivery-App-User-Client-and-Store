package com.tuyuservices.tuyu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


public class CartActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView2;
    private CartListAdapter mAdapter;
    float pricelist;
    int size;
    String userID;
    String shopID;
    String[] nameList;
    ValueEventListener cartValueEventListener;
    String timing;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private String TAG ="CARTACTIVITY";

    ArrayList<Product> cartProductList;
    private CartListAdapter mAdapter2;
    private String paymentMethod;
    int reserve_can_count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //get userID
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID =   sharedPreferences.getString("NUMBER", "NULL");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        cartProductList = new ArrayList<>();


        timing = "As soon as possible";
        //TODO MANAGE PRICELIST NAMELIST AND SIZE
        paymentMethod = "COD";
        getCartData();

    }




    private void getCartData() {
        //checks whether USERCART data exists under the USERID
        // Read from the database
        cartValueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.child("USERCART").child(userID).hasChildren()) {
                    //cart value exists but could be null value. therefore loop and check for non null value to confirm prescence of a cart list data
                    for(DataSnapshot ds : dataSnapshot.child("USERCART").child(userID).getChildren()){
                        shopID = String.valueOf(ds.getKey());
                        Log.e("SHOPID",  shopID);
                        for(DataSnapshot mDs: ds.getChildren()){
                            if(!String.valueOf(mDs.getValue()).equals("null")){
                                String productCode, productName, productThumbUrl, productPrice;

                                productCode = String.valueOf(dataSnapshot.child("PRODUCTS").child
                                        (shopID).child(String.valueOf(mDs.getValue())).getKey());

                                productName = String.valueOf(dataSnapshot.child("PRODUCTS").child
                                        (shopID).child(String.valueOf(mDs.getValue())).child("NAME").getValue());

                                productThumbUrl = String.valueOf(dataSnapshot.child("PRODUCTS").child
                                        (shopID).child(String.valueOf(mDs.getValue())).child("THUMBNAILURL").getValue());

                                productPrice =  String.valueOf(dataSnapshot.child("PRODUCTS").child
                                        (shopID).child(String.valueOf(mDs.getValue())).child("PRICE").getValue());

                                //according to the existing db rules mds always is an int
                                Product p = new Product(productCode, productName, productThumbUrl, productPrice, Integer.parseInt(mDs.getKey()));
                                cartProductList.add(p);


                            }
                        }
                        Log.e("CARTPRDUCTLIST", String.valueOf(cartProductList.size()));
                    }
                }
                displayRecyclerView(cartProductList);
                prepareTotalPrice(cartProductList);
                databaseReference.removeEventListener(cartValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void prepareTotalPrice(ArrayList<Product> cartProductList) {
        for(Product p: cartProductList){
            pricelist += p.getPrice();
        }
    }

    private void displayRecyclerView(ArrayList<Product> mList) {

        mAdapter = new CartListAdapter(mList, new CartListAdapter.ClickListener(){

            @Override
            public void onPositionClicked(int position) {
            }

            @Override
            public void onLongClicked(int position) {
            }
        },this, shopID);


        //create a new AdapterClass
        mRecyclerView2 = (RecyclerView) findViewById(R.id.cartListRecycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView2.setLayoutManager(mLayoutManager);
        mRecyclerView2.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView2.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mRecyclerView2.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView2,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                    }
                }));

    }



    private void refreshRecyclerView(ArrayList<Product> mList) {

        mAdapter2 = new CartListAdapter(mList, new CartListAdapter.ClickListener(){

            @Override
            public void onPositionClicked(int position) {
            }

            @Override
            public void onLongClicked(int position) {
            }
        },this, shopID);

        //create a new AdapterClass
        mRecyclerView2 = (RecyclerView) findViewById(R.id.cartListRecycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView2.setLayoutManager(mLayoutManager);
        mRecyclerView2.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView2.setAdapter(mAdapter2);
        mAdapter.notifyDataSetChanged();
        mRecyclerView2.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView2,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                    }
                }));

    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();

            Log.e("Cart", "cart data from CartActivity -> homeActivity");
            Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);


            startActivity(homeIntent);
            finish();

    }

    public void slotSelection(View view) {
        ImageView view1, view2;
        view1 = findViewById(R.id.slot1);
        view2= findViewById(R.id.slot2);

        switch (view.getId()){
            case R.id.slot1: timing = "As soon as possible";
                view2.setImageResource(R.drawable.super_thirty_five_button);
                view1.setImageResource(R.drawable.as_soon_as_possible_highlighted_button);
                Toast.makeText(getApplicationContext(),timing, Toast.LENGTH_SHORT).show();
                break;

            case R.id.slot2: timing = "Super 35";
                view2.setImageResource(R.drawable.super_thirty_five_button_highlighted);
                view1.setImageResource(R.drawable.as_soon_as_possible_button);
                Toast.makeText(getApplicationContext(),timing,Toast.LENGTH_SHORT).show();
                break;


        }
    }

    public void proceedToPayment(View view) {
        //pass in user phone number, name, address,date,  slottiming, cart list - namelist - pricelist - size of the cartlist
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String slottiming = timing;

        String phone = LoadNum();
        String name = LoadName();
        String address = LoadAddress();

        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        intent.putExtra("currentDate", currentDate);
        intent.putExtra("currentTime", currentTime);
        intent.putExtra("slotTiming", slottiming);
        intent.putExtra("phone", phone);
        intent.putExtra("userID", phone);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("paymentmethod", paymentMethod);
        intent.putExtra("reservecancount", reserve_can_count);
    Log.e("RCANCONT1", String.valueOf(reserve_can_count));

        startActivity(intent);


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



    //simply queries for the cart details under USERCART and USERID and shows appropriate preview if data exists
    void refreshCartPreview() {
        // Read from the database
        cartProductList.clear();
        cartValueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cartProductList.clear();
                for (DataSnapshot ds : dataSnapshot.child("USERCART").child(userID).getChildren()) {
                    for(DataSnapshot snapshot: ds.getChildren()){
                        String productId = String.valueOf(snapshot.getValue());
                        String shopId = String.valueOf(ds.getKey());
                        String productName = String.valueOf(dataSnapshot.child("PRODUCTS").
                                child(shopId).child(productId).child("NAME").getValue());
                        String productPrice = String.valueOf(dataSnapshot.child("PRODUCTS").
                                child(shopId).child(productId).child("PRICE").getValue());
                        String productThumbURL = String.valueOf(dataSnapshot.child("PRODUCTS").
                                child(shopId).child(productId).child("THUMBNAILURL").getValue());

                        if(!productId.equals("null")) {
                            Product p = new Product(productId, productName, productThumbURL, productPrice);
                            cartProductList.add(p);
                        }

                    }
                }

                //cart products are organized into cartPrducts arraylist. now display the data
                refreshRecyclerView(cartProductList);
                prepareTotalPrice(cartProductList);

                //mRef.removeEventListener(cartValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    public void reserve_can_count(View v) {

         ImageView addToCart, plus, minus;
         TextView  indicator;
        LinearLayout list_display_one, list_display_two;
        list_display_one = (LinearLayout) findViewById(R.id.list_display_one);
        list_display_two = (LinearLayout) findViewById(R.id.list_display_two);


        indicator = (TextView) findViewById(R.id.indicator_number);

        addToCart =  findViewById(R.id.add_button_list);
        plus = findViewById(R.id.plus_button_list);
        minus = findViewById(R.id.minus_button_list);

        if (v.getId() == addToCart.getId()) {
                //one instance of the order is added to the class


                list_display_one.setVisibility(View.GONE);
                list_display_two.setVisibility(View.VISIBLE);
                indicator.setText(String.valueOf(reserve_can_count));



            }else if(v.getId() == plus.getId()){


                if(reserve_can_count <8){
                    reserve_can_count++;
                    indicator.setText(String.valueOf(reserve_can_count));
                    //Maximum of only 8 orders allowed per service

                }

            }else if(v.getId() == minus.getId()){
                if(reserve_can_count >0){
                    reserve_can_count = reserve_can_count -1;
                    indicator.setText(String.valueOf(reserve_can_count));

                }
            }
        }


}
