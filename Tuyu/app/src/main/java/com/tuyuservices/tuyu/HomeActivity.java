package com.tuyuservices.tuyu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_FINE_LOCATION = 23;
    Boolean locationPermission;
    FrameLayout fragmentcontainer;
    BottomNavigationView bottomNavigationView;
    TextView addressLine1, addressLine2;
    RecyclerView mRecyclerView, mRecyclerView2;
    boolean isCartEnabled= false;
    Intent intent;
    int[] priceList;
    String[] nameList;
    List<Service> cartList;
    private CartListAdapter mAdapter;
    boolean cartActive;
    boolean openCart;
    private SharedPreferences sharedPreferences;
    private ViewGroup container;
    View view; //pass the correct layout name for the fragmen
    private ArrayList<Shop> shopList;
    private DatabaseReference databaseReference;
    private DatabaseReference mRef;
    private ValueEventListener valueEventListener;

    //get latitude and longitude
    String latitude, longitude;

    //implement listview in home
    private String TAG = "LISTHOME";
    private String serviceTag;
    String[] listString;
    int lengthCount =0;


    private FirebaseDatabase mFirebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadFragment(new HomeFragment());


        cartList = new ArrayList<>();
        intent = getIntent();
        fragmentcontainer  = (FrameLayout) findViewById(R.id.fragment_container);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        cartActive = getIntent().getBooleanExtra("cartActive", false);
        openCart = getIntent().getBooleanExtra("openCartx", false);
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");

        if(openCart){
            getCartListWithIntent(cartActive);
        }else {
            getCartList(cartActive);
        }


        //initialize the list of shops

        initializeWaterShops( );


    }

    private void initializeWaterShops() {


        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        serviceTag = "Airconditioner";
        retreiveData(serviceTag);
    }

    private float compareDistance(Location a, Location b){
        return   a.distanceTo(b);
    }



    private void retreiveData(final String x) {

        shopList = new ArrayList<>();
        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                    for (DataSnapshot ds : dataSnapshot.child("SHOP").getChildren()) {
                        String UID = (String) ds.getKey();
                        String name = null, rating = null, sampleprice = null, shopimage = null, location = null;
                        for(DataSnapshot d:ds.child(UID).getChildren()){
                            name = (String) d.child("NAME").getValue();
                            Log.e(TAG, name);
                            rating = (String) d.child("RATING").getValue();
                            sampleprice = (String) d.child("SAMPLEPRICE").getValue();
                            shopimage = (String) d.child("SAMPLEIMAGE").getValue();
                            location = ((String) d.child("LAT").getValue())+((String) d.child("LONG").getValue());
                        }



                       //create instances of shops, add them into the list
                        Shop mShop = new Shop(UID, name, rating, sampleprice, shopimage, location);
                        shopList.add(mShop);
                        lengthCount++;


                        //TODO make sure the image uri will be dynamic
                        Uri uri = Uri.parse("android.resource://com.tuyuservices.tuyu/drawable/homeicon");
                        try {
                            InputStream stream = getContentResolver().openInputStream(uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }


               // setupCustomRecyclerView(shopList);
               // mRef.removeEventListener(valueEventListener);


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

    public void setupCustomRecyclerView(List<Service> list){
        listAdapter = new ListAdapter(list);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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

                openListSecondary(position);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }




    private void openListSecondary(int position) {
        if(isCartEnabled){
            Intent intent = new Intent(HomeActivity.this, ListActivitySecondary.class);

            intent.putExtra("maintag", serviceTag);
            intent.putExtra("secondtag", "Airconditioner");

            Log.e("Cart", "cart data from ListActivity -> ListSecondaryActivity");


            intent.putExtra("openCart", true);
            intent.putExtra("pricelist", priceList);
            intent.putExtra("namelist", nameList);
            intent.putExtra("size", size);
            startActivity(intent);
        }else{
            Log.e("Cart", "NO cart data from ListActivity -> ListSecondaryActivity");

            Intent intent = new Intent(HomeActivity.this, ListActivitySecondary.class);
            intent.putExtra("maintag", serviceTag);
            intent.putExtra("secondtag", "Airconditioner");
            startActivity(intent);
        }


    }

    /**
     * loading a fragment when user clicks bottomnavigationview
     *
     *
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


    /** when the user clicks on the bottom navigation bar, the fragment according to that is loaded up
     * and then inflated with the local method loadFragment();
     * all the fragment related code is in HomeActivity - home - search- cart and user
     *
     *
     * **/


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //when a bottom navigation button is selected this is called
        Fragment fragment  = null;
        switch (menuItem.getItemId()){

            case R.id.action_home:
                fragment = new HomeFragment();
                break;
           /** case R.id.action_search:
                fragment = new SearchFragment();
                break;
            **/
            case R.id.action_cart:

                fragment = new CartFragment();
                //using ListAdapterTwo to implement the cart recyclerView
                getCartListWithIntent(cartActive);



                break;
            case R.id.action_profile:
                fragment = new ProfileFragment();
                Log.e("LOAD", "loadprofilefragment");
                loadProfileFragment();
                break;
        }


        return loadFragment(fragment);
    }




    /**home fragment -
     * when an icon in the list is clicked - openList(View V);
     * **/
    int size;

    public void openList(View v){
        int serviceID = v.getId();

        Intent listIntent = new Intent(HomeActivity.this, ListActivity.class);

        //passes the cart data to the intent to list
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

        listIntent.putExtra("SERVICE_ID", serviceID);
        startActivity(listIntent);

    }


    //code implementation for search fragment. to enable, add menu option in bottom lost
    /** search fragment
     * when the search fragment is open and the user searches for a service


    DatabaseReference dbRef;
    FirebaseDatabase mFirebaseDatabase;

    int lengthCount;
    List<Service> searchList;
    String[] listString;
    //implementation to search through the data from firebase
    public void searchValue(View v){

        listString = new  String[10];
        EditText searchBar = (EditText) findViewById(R.id.search_edit_text);
        String queryText = searchBar.getText().toString();
        SearchValue sv = new SearchValue(queryText);
        searchList = sv.searchForVal();
        //temporary solution of a 2 second buffer to retrieve data
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                getsearchList();
            }
        };
        handler.postDelayed(r, 5000);


    }


    /**
    public void openSearch(View v){
        //when the searchbar in the home screen is tapped
        //bottomnavigation view has to be highlighted manually in event of fragment chnge manually
        loadFragment(new SearchFragment());
        bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
    }



    public void getsearchList(){



        if(searchList != null) {
            Log.e("SearchedServices", String.valueOf(searchList.size()));
            setupSearchRecyclerView(searchList);
        }
    }



    //recyclerview for the search results in the searchfragment
    //listadapter two is the type of adapter containg add and minus buttons, ect;
    ListAdapterTwo listAdapter;
    private void setupSearchRecyclerView(List<Service> myDataset) {
        listAdapter = new ListAdapterTwo(myDataset, new ListAdapterTwo.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        },this);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);
        mRecyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        mRecyclerView.setAdapter(listAdapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //code goes here, goes to addedToCart();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }




     **/

    public void addedToCart(){
        //when a button is clicked in recyclerview + or - or add to cart the method is executed
        /**
        boolean cartcontent = listAdapter.;
        if(cartcontent){
            //cartList = listAdapter.returnCart();
        }
         **/

    }



    @Override
    public void onBackPressed() {
        //leaving it empty so that the user wont go to login.
        //TODO Change into a better practice
    }


    /**
     *
     * Cart fragment area
     *
     */

    private void getCartList(boolean cartActive) {
        Log.e("Cart", "cart data is in homeactivty (cartscreen)");

        int size = intent.getIntExtra("size", 0);
        if(size!=0){
            priceList = intent.getIntArrayExtra("pricelist");
            nameList = intent.getStringArrayExtra("namelist");
            isCartEnabled = true;
        }
        for(int i =0;i <size;i++){
            Service service = new Service(nameList[i], priceList[i]);
            cartList.add(service);
        }
        if(cartList!= null) {
            Log.e("TEMP", String.valueOf(cartList.size()));
            if(cartList.size() !=0) {
                if(cartActive) {
                    loadFragment(new CartFragment());
                }
                for(Service c :cartList){
                    Log.e("TAG", c.getServiceName());
                }




            }
        }
    }



    private void getCartListWithIntent(boolean cartActive) {
        Log.e("Cart", "cart data is in homeactivty (cartscreen)");

        int size = intent.getIntExtra("size", 0);
        if(size!=0){
            priceList = intent.getIntArrayExtra("pricelist");
            nameList = intent.getStringArrayExtra("namelist");
            isCartEnabled = true;
        }
        for(int i =0;i <size;i++){
            Service service = new Service(nameList[i], priceList[i]);
            cartList.add(service);
        }
        if(cartList!= null) {
            Log.e("TEMP", String.valueOf(cartList.size()));
            if(cartList.size() !=0) {
                if(cartActive) {
                    //loadFragment(new CartFragment());
                }
                for(Service c :cartList){
                    Log.e("TAG", c.getServiceName());
                }


                openCart(nameList, priceList, size);

            }else{
                //the user hasnt added anything in the cart yet. 0 items
                //showing message and an option to return home
                Log.e("TEMP", "EMPTY CART");


            }
        }
    }




    private void openCart( String[] nameList, int[] priceList, int size) {
        Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
        cartIntent.putExtra("nameList", nameList);
        cartIntent.putExtra("priceList", priceList);
        cartIntent.putExtra("size", size);
        startActivity(cartIntent);
    }

    public void noItemInCart(View v){
        //no item in cart. user presssed button to go back to home
        loadFragment(new HomeFragment());
        bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);

    }




    //to load the user credentials for profile fragment

    private void loadProfileFragment() {
    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
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

}
