package com.tuyuservices.tuyu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListActivitySecondary extends AppCompatActivity {

    private static final String TAG = "SecondarylistTag";
    String mainTag, secondaryTag;
    private String[] listString;
    DatabaseReference databaseReference;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    RecyclerView mRecyclerView;
    int lengthCount = 0;
    boolean isCartEnabled;
    public List<Service> mList;
    Context mainContext;
    List<Service> cartList;
    RelativeLayout cartView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_secondary);
        mainContext = getApplicationContext();
        cartList = new ArrayList<>();
        isCartEnabled = getIntent().getBooleanExtra("openCart", false);
        mainTag = getIntent().getStringExtra("maintag");
        secondaryTag = getIntent().getStringExtra("secondtag");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        retreiveData();
        if(isCartEnabled){
            loadCart();
        }
    }

    private void loadCart() {

        //when a button is clicked in recyclerview + or - or add to cart the method is executed
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

    ValueEventListener valueEventListener;


    private void retreiveData() {

        mList = new ArrayList<>();

        // Read from the database
        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.child(mainTag).child(secondaryTag).getChildren()) {
                    String value = (String) ds.getKey();
                    Long price = (Long) ds.getValue();
                    int pricey = price.intValue();

                    Service mService = new Service(value, pricey);
                    mList.add(mService);
                    lengthCount++;

                }

                setupRecyclerView(mList);
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
    private void setupRecyclerView(List<Service> myDataset) {
        listAdapter = new ListAdapterTwo(myDataset, new ListAdapterTwo.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        }, this);
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

    public void addedToCart(boolean isCalledFromAdapter){
        //when a button is clicked in recyclerview + or - or add to cart the method is executed

        boolean cartEmpty =  (cartList.size()==0);
        cartView = (RelativeLayout) findViewById(R.id.cartViewListSecondary);
        boolean cartcontent = listAdapter.isCartEmpty;

        if(!cartcontent){
            //cart is not empty and the cart is shown
            if(isCalledFromAdapter){
                if(isCartEnabled){
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


        }else if(cartcontent){
            //the contents of cart are empty, the cart mini view shall be hidden

            cartList.clear();
            cartView.setVisibility(View.INVISIBLE);


        }

    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();

        openBackListActivity();
    }

    private void openBackListActivity() {
        Intent backIntent = new Intent(ListActivitySecondary.this, ListActivity.class);


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

             Log.e("Cart", "cart data from ListActivitySecondary -> ListActivity");
             backIntent.putExtra("openCart", true);
         backIntent.putExtra("pricelist", priceList);
         backIntent.putExtra("namelist", nameList);
         backIntent.putExtra("size", size);
         }

        //backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        backIntent.putExtra("SERVICETAG", mainTag);
        startActivity(backIntent);

        finish();
    }


    //passes the cart list and the intention to open cart fragment to HomeActivity
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
            cartIntent.putExtra("pricelist", priceList);
            cartIntent.putExtra("namelist", nameList);
            cartIntent.putExtra("size", size);
            cartIntent.putExtra("cartActive", true);
            cartIntent.putExtra("openCartx", true);
        Log.e("Cart", "cart data from ListActivitySecondary -> homeActivity(CART)");

        startActivity(cartIntent);

    }
}
