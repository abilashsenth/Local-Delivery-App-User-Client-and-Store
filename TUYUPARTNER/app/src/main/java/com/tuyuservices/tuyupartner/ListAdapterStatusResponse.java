package com.tuyuservices.tuyupartner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

import java.util.List;

public class ListAdapterStatusResponse extends RecyclerView.Adapter<ListAdapterStatusResponse.MyViewHolder> {

    private List<Orders> orders;
    public Context mContext;

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

        }

        @Override
        public void onClick(View v) {

        }
    }

    public ListAdapterStatusResponse(List<Orders> mList, Context mContext){
        orders = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_type_one_status_response, parent, false);

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
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }



}
