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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CartActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView2;
    private CartListAdapter mAdapter;

    int[] pricelist;
    int size;
    String[] nameList;
    String timing;
    String shopUID;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        List<Service> mList = new ArrayList<>();
        timing = "As soon as possible";
        pricelist = getIntent().getIntArrayExtra("priceList");
        nameList = getIntent().getStringArrayExtra("nameList");
        size = getIntent().getIntExtra("size", 0);
        shopUID = getIntent().getStringExtra("shopuid");
        if(size!=0){
            for(int i =0;i<size;i++){
                Service s = new Service(nameList[i], pricelist[i]);
                mList.add(s);
            }
            displayRecyclerView(mList);
        }


    }

    private void displayRecyclerView(List<Service> mList) {
        //create a new
        mRecyclerView2 = (RecyclerView) findViewById(R.id.cartOrderView);
        mAdapter = new CartListAdapter(mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView2.setLayoutManager(mLayoutManager);
        mRecyclerView2.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView2.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();

            Log.e("Cart", "cart data from CartActivity -> homeActivity");
            Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);
            homeIntent.putExtra("openCart", true);
            homeIntent.putExtra("pricelist", pricelist);
            homeIntent.putExtra("namelist", nameList);
            homeIntent.putExtra("size", size);
            startActivity(homeIntent);
            finish();

    }

    public void slotSelection(View view) {

        switch (view.getId()){
            case R.id.slot1: timing = "As soon as possible";
                Toast.makeText(getApplicationContext(),timing, Toast.LENGTH_SHORT).show();

                break;

            case R.id.slot2: timing = "Today Evening";
                Toast.makeText(getApplicationContext(),timing,Toast.LENGTH_SHORT).show();

                break;

            case R.id.slot3: timing = "Today Afternoon ";
                Toast.makeText(getApplicationContext(),timing,Toast.LENGTH_SHORT).show();

                break;


            case R.id.slot4: timing = "Tomorrow Morning";
                Toast.makeText(getApplicationContext(),timing,Toast.LENGTH_SHORT).show();

                break;
        }
    }

    public void proceedToPayment(View view) {
        //pass in user phone number, name, address,date,  slottiming, cart list - namelist - pricelist - size of the cartlist
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String slottiming = timing;

        String orderID = "O"+String.valueOf(randomize());
        String shopUID = this.shopUID;

        String phone = LoadNum();
        String name = LoadName();
        String address = LoadAddress();

        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        intent.putExtra("currentDate", currentDate);
        intent.putExtra("currentTime", currentTime);
        intent.putExtra("slotTiming", slottiming);
        intent.putExtra("phone", phone);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("nameList", nameList);
        intent.putExtra("priceList", pricelist);
        intent.putExtra("size", size);
        intent.putExtra("orderuid", orderID);
        intent.putExtra("shopuid", shopUID);
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

    public int randomize(){
        final int min = 100000;
        final int max = 999999;
        final int random = new Random().nextInt((max - min) + 1) + min;
        return random;
    }


}
