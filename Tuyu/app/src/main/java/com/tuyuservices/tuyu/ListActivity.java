package com.tuyuservices.tuyu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {


    private static final String TAG = "FirebaseList1" ;
    /**list of the serviceID
     * s01 - electrical
     * s02 - plumbing
     * s03 - carpentry
     * s04 - airconditioner
     * s05 - computer
     * s06 - cctv
     * s07-  pest control
     * s08 - deepcleaning
     * s09 - packers and movers
     * s10 - homeappliances
     */

    public String serviceTag;
    private int serviceId;
    int lengthCount =0;
    boolean isCartEnabled;

    RecyclerView mRecyclerView;

    DatabaseReference databaseReference;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private RelativeLayout cartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        /**
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        serviceId = getIntent().getIntExtra("SERVICE_ID", 0);
        if(serviceId ==0){
            //activity called from onBackpressed from ListActivitySecondary
            if(getIntent().getStringExtra("SERVICETAG")!=null) {
                isCartEnabled  = getIntent().getBooleanExtra("openCart", false);
                serviceTag = getIntent().getStringExtra("SERVICETAG");
                findServiceFromForward();
            }
        }else {
            //activity called from mainActivity
             isCartEnabled  = getIntent().getBooleanExtra("openCart", false);
            if(isCartEnabled){
                //the activity is called more than first time and theres a cartlist and then the serviceID is different
                Log.e("LISTACTIVITY", "iscartenabled is true");
                findServiceWithExistingCart();
                findServiceFromForward();
            }else{
                //cart is not enabled. the activity is being called first time from Home
                findService();
            }

        }

         */





    }

    /**

    String[] nameList;
    int[] priceList;
    int size;
    List<Service> cartList;

    private void findServiceFromForward() {
        cartList = new ArrayList<>();

        retreiveData(serviceTag);

        //setup the cart
        nameList = getIntent().getStringArrayExtra("namelist");
        priceList = getIntent().getIntArrayExtra("pricelist");
        size = getIntent().getIntExtra("size", 0);

        for(int i =0; i <size;i++){
                Service s = new Service(nameList[i], priceList[i]);
                cartList.add(s);
            }
            for(Service s:cartList){
                Log.e("ListActivity", "cartlist :"+cartList.size());
            }
            if(isCartEnabled){
                setupCart();
            }


    }

    private void setupCart() {
        cartView = (RelativeLayout) findViewById(R.id.cartViewList);

        if(isCartEnabled){
            //the contents of cart are shown in the cart mini view. The number of items and total price is mandatory
            int sizeOfCart = cartList.size();
            int price=0;
            for(Service s:cartList){
                price += s.getPrice();
            }
            TextView cartContent = (TextView) findViewById(R.id.list_mini_cart_Count);
            TextView cartPrice = (TextView) findViewById(R.id.list_mini_cart_price);
            cartContent.setText(String.valueOf(sizeOfCart));
            cartPrice.setText(String.valueOf(price));
            cartView.setVisibility(View.VISIBLE);


        }else if(!isCartEnabled){
            //the contents of cart are empty, the cart mini view shall be hidden

            cartList.clear();
            cartView.setVisibility(View.INVISIBLE);


        }


    }

    private void openListSecondary(int position) {
        if(isCartEnabled){
            Intent intent = new Intent(ListActivity.this, ListActivitySecondary.class);
            intent.putExtra("maintag", serviceTag);
            intent.putExtra("secondtag", serviceList.get(position).getServiceName());

            Log.e("Cart", "cart data from ListActivity -> ListSecondaryActivity");


            intent.putExtra("openCart", true);
            intent.putExtra("pricelist", priceList);
            intent.putExtra("namelist", nameList);
            intent.putExtra("size", size);
            startActivity(intent);
        }else{
            Log.e("Cart", "NO cart data from ListActivity -> ListSecondaryActivity");

            Intent intent = new Intent(ListActivity.this, ListActivitySecondary.class);
            intent.putExtra("maintag", serviceTag);
            intent.putExtra("secondtag", serviceList.get(position).getServiceName());
            startActivity(intent);
        }


    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(isCartEnabled){
            Log.e("Cart", "cart data from ListActivity -> homeActivity");
            Intent homeIntent = new Intent(ListActivity.this, HomeActivity.class);
            homeIntent.putExtra("openCart", true);
            homeIntent.putExtra("pricelist", priceList);
            homeIntent.putExtra("namelist", nameList);
            homeIntent.putExtra("size", size);
            startActivity(homeIntent);
            finish();
        }else{
            Log.e("Cart", "NO cart data from ListActivity -> homeActivity");

            Intent homeIntent = new Intent(ListActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }

    }



     * warning: the servicetags are expected to be exactly the same as in the main database else will lead to exception

    private void findService() {
        switch (serviceId){
            case R.id.s01:
                serviceTag = "electrical";
                retreiveData(serviceTag);
                break;
            case R.id.s02:
            case R.id.z02: //z02 is the main banner which is currently about plumbing
                serviceTag = "plumbing";
                retreiveData(serviceTag);
                //TODO change as the main banner changes

                break;

            case R.id.s03:
                serviceTag = "Carpentry";
                retreiveData(serviceTag);

                break;
            case R.id.s04:
                serviceTag = "Airconditioner";
                retreiveData(serviceTag);

                break;
            case R.id.s05:
                serviceTag = "Computers";
                retreiveData(serviceTag);

                break;
            case R.id.s06:
                serviceTag = "CCTV Services";
                retreiveData(serviceTag);

                break;



                //the rest of the services are for coming soon page
            case R.id.s07:
                comingSoonActivity();
                break;
            case R.id.s08:
                comingSoonActivity();
                break;
            case R.id.s09:
                comingSoonActivity();
                break;
            case R.id.s10:
                comingSoonActivity();
                break;





        }


    }

    private void findServiceWithExistingCart() {
        switch (serviceId){
            case R.id.s01:
                serviceTag = "electrical";
                break;
            case R.id.s02:
                serviceTag = "plumbing";
                break;
            case R.id.s03:
                serviceTag = "Carpentry";
                break;
            case R.id.s04:
                serviceTag = "Airconditioner";
                break;
            case R.id.s05:
                serviceTag = "Computers";
                break;
            case R.id.s06:
                serviceTag = "CCTV Services";
                break;



            //the rest of the services are for coming soon page
            case R.id.s07:
                comingSoonActivity();
                break;
            case R.id.s08:
                comingSoonActivity();
                break;
            case R.id.s09:
                comingSoonActivity();
                break;
            case R.id.s10:
                comingSoonActivity();
                break;

        }


    }


    //temporary activity for all the coming soon services
    private void comingSoonActivity() {
        Intent intent = new Intent(ListActivity.this, ComingSoonActivity.class);
        startActivity(intent);

    }

    String[] listString;
    List<Service> serviceList;

    ValueEventListener valueEventListener;


    private void retreiveData(final String x) {

        listString = new String[10];
        serviceList = new ArrayList<>();
        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(x!= null) {
                    for (DataSnapshot ds : dataSnapshot.child(x).getChildren()) {
                        String value = (String) ds.getKey();

                        //TODO make sure the image uri will be dynamic
                        Uri uri = Uri.parse("android.resource://com.tuyuservices.tuyu/drawable/homeicon");
                        try {
                            InputStream stream = getContentResolver().openInputStream(uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        Service listoneservice = new Service(value, uri.toString());
                        serviceList.add(listoneservice);
                        lengthCount++;

                    }
                }

                setupCustomRecyclerView(serviceList);
                mRef.removeEventListener(valueEventListener);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }



     *the recyclerview setup of the first list where the recyclerview contains an image and title
     *



    ListAdapter listAdapter;

    public void setupCustomRecyclerView(List<Shop> list){
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



    //passes the cart list and the intention to open cart fragment to HomeActivity
    public void openCart(View view) {
        int size;

        Intent cartIntent = new Intent(ListActivity.this, HomeActivity.class);
        cartIntent.putExtra("openCart", true);
        size = cartList.size();
        int[] priceList = new int[size];
        String[] nameList = new String[size];
        for(int i=0;i<size;i++){
            priceList[i] = cartList.get(i).getPrice();
            nameList[i] = cartList.get(i).getServiceName();
        }
        cartIntent.putExtra("pricelist", priceList);
        cartIntent.putExtra("namelist", nameList);
        cartIntent.putExtra("size", size);
        cartIntent.putExtra("cartActive", true);
        cartIntent.putExtra("openCartx", true);
        Log.e("Cart", "cart data from ListActivity -> homeActivity(CART)");

        startActivity(cartIntent);

    }
     **/

}
