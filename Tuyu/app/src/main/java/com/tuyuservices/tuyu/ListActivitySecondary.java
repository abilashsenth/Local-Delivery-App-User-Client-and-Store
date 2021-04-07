package com.tuyuservices.tuyu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class ListActivitySecondary extends AppCompatActivity {

    private static final String TAG = "SecondarylistTag";
    String shopID;
    private String[] listString;
    DatabaseReference databaseReference;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    RecyclerView mRecyclerView;
    int lengthCount = 0;
    boolean isCartEnabled;
    public List<Product> mProductList;
    Context mainContext;
    List<Product> cartList;
    String userID;
    RelativeLayout cartView;
    private SharedPreferences sharedPreferences;
    TextView shopNamex, shopAddress, shopDistance, shopRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_secondary);
        mainContext = getApplicationContext();
        cartList = new ArrayList<>();
        isCartEnabled = getIntent().getBooleanExtra("openCart", false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID =   sharedPreferences.getString("NUMBER", "NULL");
        shopID = getIntent().getStringExtra("SHOPID");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        retreiveData();
        refreshCartPreview();


    }
    private void updateShopHeader() {

        final String[] address = new String[1];
        final String[] shopName = new String[1];
        final String[] rating = new String[1];
        final String[] distance = new String[1];



        // Read from the database
        ValueEventListener valueEventListenerX = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Log.e("SHOPID", shopID);


                shopName[0] = String.valueOf(dataSnapshot.child("SHOP").child(shopID).child("NAME").getValue());
                String latitude = String.valueOf(dataSnapshot.child("SHOP").child(shopID).child("LAT").getValue());
                String longitude = String.valueOf(dataSnapshot.child("SHOP").child(shopID).child("LONG").getValue());
                rating[0] = String.valueOf(dataSnapshot.child("SHOP").child(shopID).child("RATING").getValue());

                float lat = Float.parseFloat(sharedPreferences.getString("latitude", "null"));
                float lon = Float.parseFloat(sharedPreferences.getString("longitude", "null"));
                Log.e("RATING", rating[0]);
                Log.e("LATITUDE", latitude);
                Log.e("LONGITUDE", longitude);

                distance[0] = getDistance(lat, lon, Float.parseFloat(latitude), Float.parseFloat(longitude));
                address[0] = getAddress();





                shopNamex = findViewById(R.id.activity_secondary_shop_name);
                shopAddress = findViewById(R.id.activity_secondary_shop_address);
                shopDistance = findViewById(R.id.shop_bar_distance);
                shopRating = findViewById(R.id.shop_bar_rating);
                shopAddress.setText(address[0]);
                shopNamex.setText(shopName[0]);
                shopRating.setText(rating[0]+"*");
                shopDistance.setText(distance[0].substring(0, Math.min(distance[0].length(), 3)) + "Kms");
                //setupRecyclerView(mProductList);
                mRef.removeEventListener(valueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }



    private String getDistance(float userLat, float userLong, float lat, float longitude) {
            Location shopLocation = new Location("");
            shopLocation.setLatitude(lat);
            shopLocation.setLongitude(longitude);
            Location location = new Location("");
            shopLocation.setLatitude(userLat);
            shopLocation.setLongitude(userLong);
            float distance = location.distanceTo(shopLocation);
            //we also embed the distance in each shop objects just as we calculate them
            return String.valueOf(distance);

    }

    private String getAddress() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("address", "NULL");
    }


    //simply queries for the cart details under USERCART and USERID and shows appropriate preview if data exists
    ArrayList<Product> cartProducts;
    ValueEventListener cartValueEventListener;
    void refreshCartPreview() {
        // Read from the database
        cartProducts = new ArrayList<Product>();
        cartValueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cartProducts.clear();
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
                            cartProducts.add(p);
                        }

                    }
                }

                //cart products are organized into cartPrducts arraylist. now display the data
                if(!cartProducts.isEmpty()){
                    TextView cartContent = (TextView) findViewById(R.id.list_secondary_mini_cart_Count);
                    TextView cartPrice = (TextView) findViewById(R.id.list_secondary_mini_cart_price);
                    cartView = (RelativeLayout) findViewById(R.id.cartViewListSecondary);
                    cartView.setVisibility(View.VISIBLE);
                    String sizeOfCart = String.valueOf(cartProducts.size());
                    float priceOfCart=0;
                    for(int i=0;i<cartProducts.size();i++){
                        priceOfCart += cartProducts.get(i).price;
                    }
                    cartContent.setText(String.valueOf(sizeOfCart)+"I T E M S");
                    cartPrice.setText(String.valueOf(priceOfCart));
                }

                //mRef.removeEventListener(cartValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    ValueEventListener valueEventListener;
    Product mProduct;
    private void retreiveData() {
        mProductList = new ArrayList<>();
        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.child("PRODUCTS").child(shopID).getChildren()) {
                    String productID = (String) ds.getKey();
                    String productName = String.valueOf(ds.child("NAME").getValue());
                    String productThumbnailURL = String.valueOf(ds.child("THUMBNAILURL").getValue());
                    String productPrice = String.valueOf(ds.child("PRICE").getValue());
                    mProduct = new Product(productID, productName, productThumbnailURL, productPrice);
                    mProductList.add(mProduct);
                    lengthCount++;
                }
                setupRecyclerView(mProductList);
                mRef.removeEventListener(valueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }


    ListAdapterTwo listAdapter;
    private void setupRecyclerView(List<Product> myDataset) {
        listAdapter = new ListAdapterTwo(myDataset, new ListAdapterTwo.ClickListener() {
            @Override
            public void onPositionClicked(int position) {
            }
            @Override
            public void onLongClicked(int position) {
            }
        }, this, shopID);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view2);
        mRecyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        mRecyclerView.setAdapter(listAdapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView,
                new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Write your code here
                //pass servicetag and then the string[position]
                //the code that's supposed to be here goes to addedToCart();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        updateShopHeader();



    }

   
    //passes the cart list and the intention to open cart fragment to HomeActivity
    public void openCart(View view) {
        int size;

        Intent cartIntent = new Intent(ListActivitySecondary.this, HomeActivity.class);
            cartIntent.putExtra("openCart", true);
        Log.e("Cart", "cart data from ListActivitySecondary -> homeActivity(CART)");
        startActivity(cartIntent);

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


}
