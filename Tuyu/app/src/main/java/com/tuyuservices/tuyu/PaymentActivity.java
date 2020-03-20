package com.tuyuservices.tuyu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ncorti.slidetoact.SlideToActView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class PaymentActivity extends AppCompatActivity {
    String date, time, name, number, slotTiming, address;
    int[] priceList;
    String[] nameList;
    int size;
    List<Service> cartList;
    int totalPrice;
    String services, shopUID, orderUID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        date = getIntent().getStringExtra("currentDate");
        time  = getIntent().getStringExtra("currentTime");
        slotTiming = getIntent().getStringExtra("slotTiming");
        number = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        priceList = getIntent().getIntArrayExtra("priceList");
        nameList = getIntent().getStringArrayExtra("nameList");
        size = getIntent().getIntExtra("size", 0);
        orderUID= getIntent().getStringExtra("orderuid");
        shopUID = getIntent().getStringExtra("shopuid");
        cartList = new ArrayList<>();
        for(int i =0; i <size;i++){
            Service s = new Service(nameList[i], priceList[i]);
            cartList.add(s);
            totalPrice+=priceList[i];
        }
        assignValues();
    }

    private void assignValues() {
        TextView dateText = (TextView)  findViewById(R.id.date);
        TextView timeText = (TextView) findViewById(R.id.time);
        TextView slotTiming = (TextView) findViewById(R.id.slotTiming);

        TextView nameText = (TextView)  findViewById(R.id.name);
        TextView addressText = (TextView)  findViewById(R.id.address);
        TextView numberText = (TextView) findViewById(R.id.number);

        TextView total= (TextView) findViewById(R.id.total_amount);
        TextView serviceList = (TextView) findViewById(R.id.service_list);

        StringJoiner sj = null;

        //StringJoiner is supported only after nougat. before nougat only the amount of services required will be shown
        //TODO, to be fixed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sj = new StringJoiner(" / ");
            for(Service s: cartList){
                sj.add(s.getServiceName());
            }
            services = sj.toString();
        }else{
            services = String.valueOf(size);
        }



        dateText.setText(date);
        timeText.setText(time);
        slotTiming.setText(this.slotTiming);
        numberText.setText(number);
        nameText.setText(name);
        addressText.setText(address);
        total.setText("₹ "+totalPrice);
        serviceList.setText(services);

        SlideToActView sta = (SlideToActView) findViewById(R.id.slider);

        setupEventCallbacks();


    }

    private void setupEventCallbacks() {
        final SlideToActView slide = findViewById(R.id.slider);
        slide.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideComplete");
            }
        });
        slide.setOnSlideResetListener(new SlideToActView.OnSlideResetListener() {
            @Override
            public void onSlideReset(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideReset");
            }
        });
        slide.setOnSlideUserFailedListener(new SlideToActView.OnSlideUserFailedListener() {
            @Override
            public void onSlideFailed(@NonNull SlideToActView view, boolean isOutside) {
                Log.e("Tag", "\n" + " onSlideUserFailed - Clicked outside: " + isOutside);
            }
        });
        slide.setOnSlideToActAnimationEventListener(new SlideToActView.OnSlideToActAnimationEventListener() {
            @Override
            public void onSlideCompleteAnimationStarted(@NonNull SlideToActView view, float threshold) {
                Log.e("Tag", "\n" + " onSlideCompleteAnimationStarted - " + threshold + "");
            }

            @Override
            public void onSlideCompleteAnimationEnded(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideCompleteAnimationEnded");
                Intent intent = new Intent(PaymentActivity.this, MapsFinalActivity.class);
                intent.putExtra("number", number);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("timepreference", slotTiming );
                intent.putExtra("services", services);
                intent.putExtra("totalprice", totalPrice);
                intent.putExtra("orderuid", orderUID);
                intent.putExtra("shopuid", shopUID);
                startActivity(intent);
            }

            @Override
            public void onSlideResetAnimationStarted(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideResetAnimationStarted");
            }

            @Override
            public void onSlideResetAnimationEnded(@NonNull SlideToActView view) {
                Log.e("Tag", "\n" + " onSlideResetAnimationEnded");
            }
        });
    }




}
