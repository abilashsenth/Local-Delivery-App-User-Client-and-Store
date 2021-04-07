package com.tuyuservices.tuyu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class ListAdapterOrders extends RecyclerView.Adapter<ListAdapterOrders.MyViewHolder> {

    private final Context mContext;
    private ArrayList<Orders> orders;
    private boolean isCalledFromHome;



    public ListAdapterOrders(ArrayList<Orders> mList, Context applicationContext, boolean isCalledFromHome){
        orders = mList;
        mContext = applicationContext;
        this.isCalledFromHome = isCalledFromHome;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView oid, shopID, status, name, number, address, date, time, timepreference, total, paymentMethod, productsOrdered;
        public ImageView imageButton;
        public MyViewHolder(View view) {
            super(view);
            oid = (TextView) view.findViewById(R.id.list_item_type_order_oid);
            shopID = (TextView) view.findViewById(R.id.list_item_type_order_shopname);
            status = (TextView) view.findViewById(R.id.list_item_type_order_status);
            date = (TextView) view.findViewById(R.id.list_item_type_order_date);
            time = (TextView) view.findViewById(R.id.list_item_type_order_time);
            total = (TextView) view.findViewById(R.id.list_item_type_order_totalamt);
            paymentMethod = (TextView) view.findViewById(R.id.list_item_type_order_paymentMethod);
            productsOrdered = (TextView) view.findViewById(R.id.list_item_type_order_productsOrdered);
            imageButton = view.findViewById(R.id.list_item_type_one_order_button);

            if(isCalledFromHome){
                imageButton.setImageResource(R.drawable.track_order_button);
                imageButton.setOnClickListener(this);
            }else{
                imageButton.setImageResource(R.drawable.reorder_icon);
                imageButton.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == imageButton.getId()){
                Log.e("IMAGEBUTTON", "Clicked");
                if(isCalledFromHome){
                    /**
                     * userID = intent.getStringExtra("userID");
                     *         number = intent.getStringExtra("number");
                     *         name = intent.getStringExtra("name");
                     *         address = intent.getStringExtra("address");
                     *         date = intent.getStringExtra("date");
                     *         time = intent.getStringExtra("time");
                     *         timepreference = intent.getStringExtra("timepreference");
                     *         paymentMethod = intent.getStringExtra("paymentmethod");
                     *         reserveCanCount = intent.getIntExtra("reservecancount", 0);
                     *         totalprice= intent.getIntExtra("totalprice",0);
                     */
                    Intent intent = new Intent(mContext, OrderTrackActivity.class);
                    intent.putExtra("orderExistsAlready", true);
                    intent.putExtra("number", orders.get(getAdapterPosition()).getNumber());
                    intent.putExtra("name", orders.get(getAdapterPosition()).getName());
                    intent.putExtra("address", orders.get(getAdapterPosition()).getAddress());
                    intent.putExtra("date", orders.get(getAdapterPosition()).getDate());
                    intent.putExtra("time", orders.get(getAdapterPosition()).getTime());
                    intent.putExtra("timepreference", orders.get(getAdapterPosition()).getTimepreference());
                    intent.putExtra("paymentMethod", orders.get(getAdapterPosition()).getPaymentMethod());
                    intent.putExtra("reservecancount", 0);
                    intent.putExtra("totalprice", orders.get(getAdapterPosition()).getTotalAmount());


                    intent.putExtra("OID", orders.get(getAdapterPosition()).getOID());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }else{
                    //TODO HANDLE REORDERING
                }
            }
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_type_order, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //TODO add image bitmap
        Orders order = orders.get(position);
        holder.oid.setText(order.getOID());
        holder.shopID.setText(order.getShopID());
        holder.status.setText(order.getStatus()+"✔");
       // holder.name.setText(order.getName());
       // holder.number.setText(order.getNumber());
       // holder.address.setText(order.getAddress());
        holder.date.setText(order.getDate());
        holder.time.setText(order.getTime()+"⏱");
       // holder.timepreference.setText(order.getTimepreference());
        holder.total.setText(order.getTotalAmount()+"₹");
        holder.paymentMethod.setText(order.getPaymentMethod()+"\uD83D\uDCB5");
        holder.productsOrdered.setText(order.getTotalProducts());



    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
