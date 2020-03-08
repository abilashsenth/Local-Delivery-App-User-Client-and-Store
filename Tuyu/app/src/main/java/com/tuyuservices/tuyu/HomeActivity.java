package com.tuyuservices.tuyu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        if(openCart){
            getCartListWithIntent(cartActive);
        }else {
            getCartList(cartActive);
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
            case R.id.action_search:
                fragment = new SearchFragment();
                break;
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


    /** search fragment
     * when the search fragment is open and the user searches for a service
     */

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


    public void addedToCart(){
        //when a button is clicked in recyclerview + or - or add to cart the method is executed
        boolean cartcontent = listAdapter.isCartEmpty;
        if(cartcontent){
            //cartList = listAdapter.returnCart();
        }

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
