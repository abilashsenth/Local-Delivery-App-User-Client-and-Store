package com.tuyuservices.tuyumain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ManageFinancesActivity extends AppCompatActivity {
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    private String TAG ="ManageFinancesActivity";
    private ArrayList<Orders> orderList;
    private DatabaseReference databaseReference;
    private DatabaseReference mRef;
    private SharedPreferences sharedPreferences;
    private String shopID;
    TextView totalEarningsText, todayEarningsText, owedEarningsText;

    private RecyclerView mRecyclerView;
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_finances);

        orderList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        checkDBChange();
        Log.e("TAG", String.valueOf(orderList.size()));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        shopID = sharedPreferences.getString("shopID", "NULL");

        todayEarningsText = (TextView) findViewById(R.id.today_earnings);
        totalEarningsText = (TextView) findViewById(R.id.total_earnings);
        owedEarningsText = (TextView) findViewById(R.id.earnings_owed_water360);
        todayDate  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }


    ArrayList<Orders> ordersArrayList;
    ArrayList<Product> productArrayList;

    ValueEventListener cartValueEventListener;
    Orders order;
    public void checkDBChange() {
        // Read from the database
        ordersArrayList = new ArrayList<Orders>();
        cartValueEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ordersArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.child("ORDERSPLACED").getChildren()) {
                    for(DataSnapshot snapshot: ds.getChildren()){
                        //productArrayList.clear();
                        //adding every order which is not finished by status.
                        if(String.valueOf(snapshot.child("STATUS").getValue()).equals("FINISHED")) {
                            Log.e("ADDING ORDER", snapshot.getKey());
                            String OID = snapshot.getKey();
                            String shopID = String.valueOf(snapshot.child("SHOPID").getValue());
                            String status = String.valueOf(snapshot.child("STATUS").getValue());
                            String name = String.valueOf(snapshot.child("NAME").getValue());
                            String number = String.valueOf(snapshot.child("NUMBER").getValue());
                            String address = String.valueOf(snapshot.child("ADDRESS").getValue());
                            String date = String.valueOf(snapshot.child("DATE").getValue());
                            String time = String.valueOf(snapshot.child("TIME").getValue());
                            String timePreference = String.valueOf(snapshot.child("TIMEPREFERENCE").getValue());
                            String totalAmount = String.valueOf(snapshot.child("TOTALAMOUNT").getValue());
                            String paymentMethod = String.valueOf(snapshot.child("PAYMENTMETHOD").getValue());
                            String totalProducts="";
                            StringBuilder sb = new StringBuilder();

                            for (DataSnapshot p : snapshot.child("ORDERS").getChildren()) {
                                String productName = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("NAME").getValue());
                                String productPrice = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("PRICE").getValue());
                                String productThumbnailURL = String.valueOf(dataSnapshot.child("PRODUCTS").child(shopID).child(String.valueOf(p.getValue())).child("THUMBNAILURL").getValue());
                                totalProducts = sb.append(productName+"/-/ ").toString();

                                //  Product product = new Product(String.valueOf(p.getValue()), productName, productPrice, productThumbnailURL);
                              //  productArrayList.add(product);
                            }
                            order = new Orders(OID, shopID, status, name, number, address, date, time, timePreference, totalAmount, paymentMethod, totalProducts);
                           // order.setProductArrayList(productArrayList);
                            ordersArrayList.add(order);
                        }

                    }
                }
                setFinances();
                setupOrdersRecyclerView(ordersArrayList);
                //mRef.removeEventListener(cartValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

        });
    }





    ListAdapterOrders listAdapter;

    public void setupOrdersRecyclerView(ArrayList<Orders> list){
        listAdapter = new ListAdapterOrders(list, getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.manageFinancesRecyclerView);
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
    //total earnings, today earnings as well as amount owed is logged
    private void setFinances() {
        float totalEarnings =0, todayEarnings = 0, amountOwed =0;
        for(Orders o: orderList){
            totalEarnings += Float.parseFloat( o.getTotalAmount());
            if(o.getDate().equals(todayDate)){
                todayEarnings += Float.parseFloat( o.getTotalAmount());
                amountOwed += 5;
            }
        }
        totalEarningsText.setText("TOTAL EARNINGS" + String.valueOf(totalEarnings));
        todayEarningsText.setText("TODAY EARNINGS" + String.valueOf(todayEarnings));
        owedEarningsText.setText("AMOUNT OWED" + String.valueOf(amountOwed));



    }


    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
