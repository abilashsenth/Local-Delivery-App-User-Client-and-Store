package com.tuyuservices.tuyu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncorti.slidetoact.SlideToActView;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class PaymentActivity extends AppCompatActivity {
    String date, time, name, number, slotTiming, userID, address;
    float priceList;
    String[] nameList;
    int size, reserveCanCount;
    ArrayList<Product> cartList;
    int totalPrice =0;
    String services;
    private DatabaseReference databaseReference;
    private ValueEventListener cartValueEventListener;
    private String shopID, shopNameText;
    private String TAG="PAYMENTACTIVITY";
    private String paymentMethod;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        date = getIntent().getStringExtra("currentDate");
        time  = getIntent().getStringExtra("currentTime");
        slotTiming = getIntent().getStringExtra("slotTiming");
        number = getIntent().getStringExtra("phone");
        userID = getIntent().getStringExtra("userID");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        paymentMethod = getIntent().getStringExtra("paymentmethod");
        reserveCanCount = getIntent().getIntExtra("reservecancount",0);

        cartList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
                        shopNameText = String.valueOf(dataSnapshot.child("SHOP").child(shopID).child("NAME").getValue());
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
                                cartList.add(p);


                            }
                        }
                        Log.e("CARTPRDUCTLIST", String.valueOf(cartList.size()));
                    }
                }
                assignValues(cartList);
                databaseReference.removeEventListener(cartValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


    private void assignValues(ArrayList<Product> cartList) {
        TextView dateText = (TextView)  findViewById(R.id.date);
        TextView timeText = (TextView) findViewById(R.id.time);
        TextView slotTiming = (TextView) findViewById(R.id.slotTiming);
        TextView shopName = (TextView) findViewById(R.id.shop_name);


        TextView nameText = (TextView)  findViewById(R.id.name);
        TextView addressText = (TextView)  findViewById(R.id.address);
        TextView numberText = (TextView) findViewById(R.id.number);

        TextView total= (TextView) findViewById(R.id.total_amount);
        TextView total_final= (TextView) findViewById(R.id.total_amount_final);
        TextView serviceList = (TextView) findViewById(R.id.service_list);
        TextView deliverCharges = (TextView) findViewById(R.id.delivery_charges);

        StringJoiner sj = null;

        //StringJoiner is supported only after nougat. before nougat only the amount of services required will be shown
        //TODO, to be fixed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sj = new StringJoiner(" / ");
            for(Product s: cartList){
                sj.add(s.getProductName());
                totalPrice += s.getPrice();
            }

            services = sj.toString();
        }else{
            services = String.valueOf(size);
        }



        dateText.setText("Date : "+date);
        timeText.setText("Time : "+time);
        slotTiming.setText("Time Preference : " +this.slotTiming);
        numberText.setText(number);
        nameText.setText(name);
        shopName.setText(shopNameText);
        addressText.setText(address);
        total.setText(totalPrice+" ₹");
        deliverCharges.setText(0+" ₹");
        total_final.setText(totalPrice+" ₹");
        serviceList.setText(services);

    }




    public void goToOrderTrackActivity(View view) {
        Intent intent = new Intent(PaymentActivity.this, OrderTrackActivity.class);
        intent.putExtra("number", number);
        intent.putExtra("userID", userID);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("timepreference", slotTiming );
        intent.putExtra("paymentmethod", paymentMethod);
        intent.putExtra("reservecancount", reserveCanCount);
        intent.putExtra("totalprice", totalPrice);
        intent.putExtra("shopname", shopNameText);
        startActivity(intent);
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
