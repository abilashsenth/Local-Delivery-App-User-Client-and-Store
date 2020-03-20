package com.tuyuservices.tuyu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class ListActivitySecondary extends AppCompatActivity {

    private static final String TAG = "SecondarylistTag";
    String shopUID, secondaryTag;
    private String[] listString;

    DatabaseReference databaseReference;
    FirebaseDatabase mFirebaseDatabase;
    ValueEventListener valueEventListener;
    DatabaseReference mRef;

    RecyclerView mRecyclerView;
    int lengthCount = 0;
    boolean isCartEnabled;
    public List<Service> serviceList;
    Context mainContext;
    List<Service> cartList = new ArrayList<>();
    RelativeLayout cartView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_secondary);
        mainContext = getApplicationContext();

        isCartEnabled = getIntent().getBooleanExtra("openCart", false);
        shopUID = getIntent().getStringExtra("maintag"); // SHOP UID is the maintag
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        //retrieves services under the shop UID, and also retrieves services in cart if any
        retreiveData();
        if(isCartEnabled){
            loadCart();
        }
    }


    /**
     * the activity recieves the UID via getIntent.getStringExtra();
     * sets a valueEventListener for firebase realtime db,
     * saves an arraylist of all the services  (ie, water can capacity) and their prices
     */
    private void retreiveData() {

        serviceList = new ArrayList<>();
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.child("SERVICES").child(shopUID).getChildren()) {
                    String value = (String) ds.getKey();
                    Long price = (Long) ds.getValue();
                    int pricey = price.intValue();

                    Service mService = new Service(value, pricey);
                    serviceList.add(mService);
                    lengthCount++;

                }

                //passes the existing service data to recyclerview setup
                setupRecyclerView(serviceList);
                mRef.removeEventListener(valueEventListener);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    /**
     * passed on the Service list according to shops, into the recyclerview adapter
     * ListAdapterTwo handles the data into recyclerview in the activity
     */

    ListAdapterTwo listAdapter;
    private void setupRecyclerView(List<Service> serviceListRecycler) {
        listAdapter = new ListAdapterTwo(serviceListRecycler, new ListAdapterTwo.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        }, this, shopUID);
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


    }

    /**
     * when plus, minus or add button is pressed in the recyclerview, ListAdapterTwo handles
     * the list of services and calls this function, to show the mini cartview and updates info
     *
     * cartlist is the service arraylist that is passed on from adapter
     */
    public void addedToCart(boolean isCalledFromAdapter){

        cartView = (RelativeLayout) findViewById(R.id.cartViewListSecondary);
        boolean isCartEmpty = listAdapter.isCartEmpty;

        if(!isCartEmpty){
            //cart is not empty and the cart is shown
            if(isCalledFromAdapter){
                if(isCartEnabled){
                    cartList.clear();
                    cartList.addAll(listAdapter.returnCart());
                isCartEnabled=false;
                }else {
                    int size = listAdapter.returnCart().size();
                    Service s = listAdapter.returnCart().get(size-1);
                    cartList.add(s);
                }

            }else{
                cartList.addAll(listAdapter.returnCart());

            }
            //the contents of cart are shown in the cart mini view. The number of items and total price is mandatory
            int sizeOfCart = cartList.size();
            int price=0;
            for(Service s:cartList){
                price += s.getPrice();
            }
            TextView cartContent = (TextView) findViewById(R.id.list_secondary_mini_cart_Count);
            TextView cartPrice = (TextView) findViewById(R.id.list_secondary_mini_cart_price);
            cartContent.setText(String.valueOf(sizeOfCart));
            cartPrice.setText(String.valueOf(price));
            cartView.setVisibility(View.VISIBLE);


        }else if(isCartEmpty){
            //the contents of cart are empty, the cart mini view shall be hidden

            cartList.clear();
            cartView.setVisibility(View.INVISIBLE);


        }

    }


    /**
     * loads up the mini cart preview in the bottom and updates relevant info
     */
    private void loadCart() {

        cartView = (RelativeLayout) findViewById(R.id.cartViewListSecondary);


        //get carlist value from intents
        int[] priceList = getIntent().getIntArrayExtra("pricelist");
        String[] nameList = getIntent().getStringArrayExtra("namelist");
        int size = getIntent().getIntExtra("size", 0);
        for(int i = 0; i <size; i++){
            Service s = new Service(nameList[i], priceList[i]);
            cartList.add(s);
        }

        //the contents of cart are shown in the cart mini view. The number of items and total price is mandatory
        int sizeOfCart = cartList.size();
        Log.e("TAG", String.valueOf(sizeOfCart)
        );
        int price=0;
        for(Service s:cartList){
            price += s.getPrice();
        }
        TextView cartContent = (TextView) findViewById(R.id.list_secondary_mini_cart_Count);
        TextView cartPrice = (TextView) findViewById(R.id.list_secondary_mini_cart_price);
        cartContent.setText(String.valueOf(sizeOfCart));
        cartPrice.setText(String.valueOf(price));

        cartView.setVisibility(View.VISIBLE);



    }



    /**
     * when the user goes back one level, the user goes to homeactivity with cart data if any exists
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent backIntent = new Intent(ListActivitySecondary.this, HomeActivity.class);
        int size;
        size = cartList.size();

        if(size!=0) {
            //get the cart list and a boolean true value and pass it onto the backintent
            int[] priceList = new int[size];
            String[] nameList = new String[size];
            for (int i = 0; i < size; i++) {
                priceList[i] = cartList.get(i).getPrice();
                nameList[i] = cartList.get(i).getServiceName();
            }

            Log.e("Cart", "cart data from ListActivitySecondary -> HomeActivity");
            backIntent.putExtra("openCart", true);
            backIntent.putExtra("pricelist", priceList);
            backIntent.putExtra("namelist", nameList);
            backIntent.putExtra("size", size);
        }

        //backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        backIntent.putExtra("SERVICETAG", shopUID);
        startActivity(backIntent);

        finish();
    }


    /**
     * when the user presses open cart button in the bottom cart preview
     * passes the cart list to homeactivity, which then passes data to cartActivity
     * along with intent extra to openCart = true
     *
     */
    public void openCart(View view) {
        int size;

        Intent cartIntent = new Intent(ListActivitySecondary.this, HomeActivity.class);
            cartIntent.putExtra("openCart", true);
        size = cartList.size();
        int[] priceList = new int[size];
            String[] nameList = new String[size];
            for(int i=0;i<size;i++){
                priceList[i] = cartList.get(i).getPrice();
                nameList[i] = cartList.get(i).getServiceName();
            }
            cartIntent.putExtra("shopuid", shopUID);
            cartIntent.putExtra("pricelist", priceList);
            cartIntent.putExtra("namelist", nameList);
            cartIntent.putExtra("size", size);
            cartIntent.putExtra("cartActive", true);
            cartIntent.putExtra("openCartx", true);
        Log.e("Cart", "cart data from ListActivitySecondary -> homeActivity(CART)");

        startActivity(cartIntent);

    }
}
