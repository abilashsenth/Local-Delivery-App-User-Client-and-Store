package com.tuyuservices.tuyumain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class ListAdapterOrders extends RecyclerView.Adapter<ListAdapterOrders.MyViewHolder> {

    private final Context mContext;
    private ArrayList<Orders> orders;




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView oid, shopID, status, name, number, address, date, time, timepreference, total, paymentMethod, productsOrdered;
        public Button callUser;
        public MyViewHolder(View view) {
            super(view);
            oid = (TextView) view.findViewById(R.id.list_item_type_one_oid);
            shopID = (TextView) view.findViewById(R.id.list_item_type_one_shopID);
            status = (TextView) view.findViewById(R.id.list_item_type_one_status);
            name = (TextView) view.findViewById(R.id.list_item_type_one_name);
            number =(TextView) view.findViewById(R.id.list_item_type_one_number);
            address = (TextView) view.findViewById(R.id.list_item_type_one_address);
            date = (TextView) view.findViewById(R.id.list_item_type_one_date);
            time = (TextView) view.findViewById(R.id.list_item_type_one_time);
            timepreference = (TextView) view.findViewById(R.id.list_item_type_one_timepref);
            total = (TextView) view.findViewById(R.id.list_item_type_one_totalamt);
            paymentMethod = (TextView) view.findViewById(R.id.list_item_type_one_paymentMethod);
            productsOrdered = (TextView) view.findViewById(R.id.list_item_type_one_productsOrdered);
            callUser = (Button) view.findViewById(R.id.list_item_type_one_call_customer);
            callUser.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Log.e("Callbutton", "called" );
            ((OrdersActivity)mContext).dialUserIntent(String.valueOf(number.getText()));
        }
    }



    public ListAdapterOrders(ArrayList<Orders> mList, Context applicationContext){
        orders = mList;
        mContext = applicationContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_type_one, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //TODO add image bitmap
        Orders order = orders.get(position);
        holder.oid.setText(order.getOID());
        holder.shopID.setText(order.getShopID());
        holder.status.setText(order.getStatus());
        holder.name.setText(order.getName());
        holder.number.setText(order.getNumber());
        holder.address.setText(order.getAddress());
        holder.date.setText(order.getDate());
        holder.time.setText(order.getTime());
        holder.timepreference.setText(order.getTimepreference());
        holder.total.setText(order.getTotalAmount());
        holder.paymentMethod.setText(order.getPaymentMethod());
        holder.productsOrdered.setText(order.getTotalProducts());



    }

    @Override
    public int getItemCount() {
        return orders.size();
    }



    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/







}
