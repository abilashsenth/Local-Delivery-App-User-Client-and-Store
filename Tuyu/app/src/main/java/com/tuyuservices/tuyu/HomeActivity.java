package com.tuyuservices.tuyu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/
public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,   HomeRecyclerAdapter.ItemClickListener {

    FrameLayout fragmentcontainer;
    BottomNavigationView bottomNavigationView;
    RecyclerView mRecyclerView;
    Intent intent;
    int[] priceList;
    String[] nameList;
    List<Service> cartList;
    boolean cartActive;
    boolean openCart;
    private SharedPreferences sharedPreferences;
    String userID;
    LinearLayout mProgressBar;
    LinearLayout mLinearLayout;

    //Database references
    DatabaseReference databaseReference;
    private ValueEventListener valueEventListener, valueEventListenerTrack;
    private ArrayList<Shop> shopList;
    private ArrayList<Shop> geoSortedShops;
    private String TAG = "HOMEACTIVITY";
    private ListAdapterOrders listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadFragment(new HomeFragment());
        //get userID
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID =   sharedPreferences.getString("NUMBER", "NULL");



        cartList = new ArrayList<>();
        intent = getIntent();
        fragmentcontainer  = (FrameLayout) findViewById(R.id.fragment_container);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        cartActive = getIntent().getBooleanExtra("cartActive", false);
        openCart = getIntent().getBooleanExtra("openCart", false);
        mProgressBar = findViewById(R.id.loaderview);
        mLinearLayout = (LinearLayout) findViewById(R.id.home_fragment_main);

        retreiveShopData();
        retrieveUnfinishedOrders(false);



        if(openCart){
            openCartFragment(cartActive);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveUnfinishedOrders(false);


    }

    /**
     * loading a fragment when user clicks bottomnavigationview
     **/

    private boolean loadFragment(Fragment fragment){
        //swtichfragment
        if(fragment!=null){
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.fragment_container, fragment).
                    commit();

            return true;
        }
        return false;

    }


    public void openMapAddressActivity(){
        Intent mHomeIntent = new Intent(HomeActivity.this, MapAddressActivity.class);
        startActivity(mHomeIntent);
    }



    /** when the user clicks on the bottom navigation bar, the fragment according to that is loaded up
     * and then inflated with the local method loadFragment();
     * all the fragment related code is in HomeActivity - home - search- cart and user
     * **/

    public void loadTrackFragment(){
        Fragment fragment = new TrackFragment();
        retrieveUnfinishedOrders(true);
        loadFragment(fragment);
        bottomNavigationView.setSelectedItemId(R.id.action_track);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //when a bottom navigation button is selected this is called
        Fragment fragment  = null;
        switch (menuItem.getItemId()){

            case R.id.action_home:
                fragment = new HomeFragment();
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
                break;

            case R.id.action_track:
                fragment = new TrackFragment();
                retrieveUnfinishedOrders(true);
                break;

            case R.id.action_cart:
                fragment = new CartFragment();
                openCartFragment(cartActive);
                break;

            case R.id.action_profile:
                fragment = new ProfileFragment();
                Log.e("LOAD", "loadprofilefragment");
                loadProfileFragment();
                break;
        }
        return loadFragment(fragment);
    }

    /**Track fragment -
     * gets all order objects of the userID that is not finished
     *
     * **/
    Orders orders;
    ArrayList<Orders> trackOrderList;
    private void retrieveUnfinishedOrders(final boolean isCalledFromTrackFragment) {
        Log.e("USERIDHOME", userID);
        trackOrderList= new ArrayList<>();
        valueEventListenerTrack =  databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child("ORDERSPLACED").getChildren()) {
                    if(String.valueOf(ds.child("NUMBER").getValue()).equals(userID)){
                        Log.e("VALUEOF", String.valueOf(ds.child("STATUS").getValue()));
                        if(!String.valueOf(ds.child("STATUS").getValue()).equals("FINISHED")){
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
                            orders = new Orders(OID, shopName, status, name, number, address, date, time, timePreference, totalAmount, paymentMethod, totalProducts);
                            trackOrderList.add(orders);
                        }
                    }
                }
                databaseReference.removeEventListener(valueEventListener);
                if(isCalledFromTrackFragment){
                    setupTrackOrderRecyclerView(trackOrderList);
                }else{
                    setupHomeHeaderRecyclerView(trackOrderList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Home- Trackorderlist", "Failed to read value.", databaseError.toException());
            }
        });
    }

    RecyclerView mRecyclerViewTrack;
    private void setupTrackOrderRecyclerView(ArrayList<Orders> trackOrderList) {
        listAdapter = new ListAdapterOrders(trackOrderList, getApplicationContext(), true);
        mRecyclerViewTrack = (RecyclerView) findViewById(R.id.recycler_views_track_unfinished_orders);
        mRecyclerViewTrack.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerViewTrack.setLayoutManager(mLayoutManager);


        mRecyclerViewTrack.setAdapter(listAdapter);
        mRecyclerViewTrack.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerViewTrack, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e("Tag"," "+position );
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    /**
     * Home Header Part-
     * works to show the latest unfinished order preview from tracking list
     * and also shows the user address.
     * permits user to click the map icon and address can be changed with location service help
     */

    ListAdapterHomeHeader mListAdapterHomeHeader;
    RecyclerView mRecyclerViewHomeHeader;
    private void setupHomeHeaderRecyclerView(ArrayList<Orders> trackOrderList) {
        //note: trackOrderList consists of all the orders that are unfinished. The  homeHeaderUI shows only the latestOne aka index size-1
        String address = loadAddress();
        String name = loadName();
        Orders order0 = trackOrderList.get(trackOrderList.size()-1);
        HomeHeader mHomeHeader = new HomeHeader(name, address, true, order0);
        ArrayList<HomeHeader> homeHeaderArrayList = new ArrayList<>();
        homeHeaderArrayList.add(mHomeHeader);
        setupHomeHeaderRecyclerViewAdapter(homeHeaderArrayList);
    }


    private void setupHomeHeaderRecyclerViewAdapter(ArrayList<HomeHeader> homeHeaderArrayList) {
        mListAdapterHomeHeader = new ListAdapterHomeHeader(homeHeaderArrayList, this);
        mRecyclerViewHomeHeader = (RecyclerView) findViewById(R.id.recycler_view_home_header);
        mRecyclerViewHomeHeader.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerViewHomeHeader.setLayoutManager(mLayoutManager);
        mRecyclerViewHomeHeader.setAdapter(mListAdapterHomeHeader);
        mRecyclerViewHomeHeader.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerViewHomeHeader, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e("Tag"," "+position );
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }




    /**home fragment -
     * when an icon in the list is clicked - openList(View V);
     * **/
    //setting up recyclerviews of shops available within the area
    Shop shop;
    private void retreiveShopData() {
        shopList = new ArrayList<>();
        // Read from the database
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //value is the shopUniqueID embedded into each shop object inside the arraylists
                for (DataSnapshot ds : dataSnapshot.child("SHOP").getChildren()) {
                    String value = (String) ds.getKey();
                    Log.e("HOMEACTIVITY_SHOPSID", value);
                    shop = new Shop(value,String.valueOf(ds.child("LAT").getValue()),
                           String.valueOf( ds.child("LONG").getValue()),
                            String.valueOf(ds.child("NAME").getValue()),
                            String.valueOf(ds.child("RATING").getValue()),
                            String.valueOf(ds.child("SAMPLEPRICE").getValue()),
                            String.valueOf(ds.child("SHOPIMAGE").getValue()));
                    shopList.add(shop);

                }
                //setupCustomRecyclerView(serviceList);
                databaseReference.removeEventListener(valueEventListener);
                geoSortedShops = geoSortShops(shopList);
                //assining the geosorted shops into the recyclerview
                setRecyclerView(geoSortedShops);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("HomeActivity- Shoplist", "Failed to read value.", error.toException());
            }
        });

    }

    private ArrayList<Shop> geoSortShops(ArrayList<Shop> shopList) {
        //retrieve user latitude and longitude from sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        float lat = Float.parseFloat(sharedPreferences.getString("latitude", "null"));
        float lon = Float.parseFloat(sharedPreferences.getString("longitude", "null"));
        //gets raw shop data from the entire db, checks for shops located with a distance <= 5km and sorts them
        LocationService mLocationService = new LocationService(shopList,lat,lon );
        return mLocationService.sortLocation(5);
    }

    HomeRecyclerAdapter homeRecyclerAdapter;
    private void setRecyclerView(ArrayList<Shop> geoSortedShops) {
        //the recyclerview in the home fragment uses HomeRecyclerAdapter and home_recycler_layout.xml
        mRecyclerView = (RecyclerView) findViewById(R.id.home_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeRecyclerAdapter = new HomeRecyclerAdapter(this, geoSortedShops, getApplicationContext());
        homeRecyclerAdapter.setClickListener(this);
        mRecyclerView.setAdapter(homeRecyclerAdapter);
        bottomNavigationView.setVisibility(View.VISIBLE);
        mLinearLayout = findViewById(R.id.home_fragment_main);
        mProgressBar = findViewById(R.id.loaderview);
        mLinearLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        openList(geoSortedShops.get(position).shopId);
    }


    public void openList(String shopId){

        Intent listIntent = new Intent(HomeActivity.this, ListActivitySecondary.class);
        if(cartList.size()!=0){
            Log.e("CART", "cart data from HomeActivity to ListActivity");
            listIntent.putExtra("openCart", true);
            listIntent.putExtra("pricelist", priceList);
            listIntent.putExtra("namelist", nameList);
            listIntent.putExtra("size", cartList.size());

        }else{
            listIntent.putExtra("openCart", false);
            Log.e("CART", "NO cart data from HomeActivity to ListActivity");
        }

        listIntent.putExtra("SHOPID", shopId);
        startActivity(listIntent);

    }





    /**
     *
     * Cart fragment area
     *
     */
    ValueEventListener cartValueEventListener;
    private void openCartFragment(boolean cartActive) {
        //checks whether USERCART data exists under the USERID
        cartValueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("USERCART").child(userID).hasChildren()) {
                   //cart value exists but could be null value. therefore loop and check for non null value to confirm prescence of a cart list data
                    for(DataSnapshot ds : dataSnapshot.child("USERCART").child(userID).getChildren()){
                        String shopID = String.valueOf(ds.getKey());
                        Log.e("SHOPID",  shopID);
                        for(DataSnapshot mDs: ds.getChildren()){
                            if(!String.valueOf(mDs.getValue()).equals("null")){
                                Log.e("TEMP", "Cart object exists");
                                openCart(shopID, userID);
                            }
                        }
                    }

                }
                databaseReference.removeEventListener(cartValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


    private void openCart(String shopID, String UserId) {
        Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
        startActivity(cartIntent);
    }

    public void noItemInCart(View v){
        //no item in cart. user presssed button to go back to home
        loadFragment(new HomeFragment());
        bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);

    }


    /**
     *     //to load the user credentials for profile fragment
     */

    private void loadProfileFragment() {
    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
    startActivity(intent);
    }

    /**
     * loaders
     * @return
     */

    public String LoadNum(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("NUMBER", "NULL");
    }

    public String loadName(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("NAME", "NULL");
    }
    public String loadAddress(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  sharedPreferences.getString("address", "NULL");
    }

    public void logOut(View v){
        SaveLoggedIn("login", false);
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
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
        //leaving it empty so that the user wont go to login.
        //TO be Changed into a better practice
    }

}
