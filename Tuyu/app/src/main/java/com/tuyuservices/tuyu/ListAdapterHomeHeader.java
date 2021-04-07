package com.tuyuservices.tuyu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class ListAdapterHomeHeader extends RecyclerView.Adapter<ListAdapterHomeHeader.MyViewHolder> {
    private final Context mContext;
    private ArrayList<HomeHeader > homeHeader;

    public ListAdapterHomeHeader(ArrayList<HomeHeader> mList, Context applicationContext){
        homeHeader = mList;
        mContext = applicationContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView location, shopName, orderDetails;
        public ImageView imageButton;
        public CardView mCardView;
        public MyViewHolder(View view) {
            super(view);
           
            location = view.findViewById(R.id.list_item_home_header_address);
            shopName = view.findViewById(R.id.list_item_home_header_shopname);
            orderDetails = view.findViewById(R.id.list_item_home_header_order_overview);
            imageButton = view.findViewById(R.id.home_header_location_marker);
            mCardView = view.findViewById(R.id.list_item_home_recycler_header_base2);
            mCardView.setOnClickListener(this);
            imageButton.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.home_header_location_marker:
                    ((HomeActivity)mContext).openMapAddressActivity();
                    break;
                case R.id.list_item_home_recycler_header_base2:
                    ((HomeActivity)mContext).loadTrackFragment();
                    break;
            }
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_home_header, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //TODO add image bitmap
        HomeHeader homeHeader = this.homeHeader.get(position);
        holder.location.setText(homeHeader.getAddress());
        Orders order = homeHeader.getOrders();
        String totalProducts = order.getTotalProducts();
        String[] tokens = totalProducts.split("/-/");
        int count=0;
        for (String t : tokens) {
            count++;
        }
        holder.orderDetails.setText(String.valueOf(count-1)+" item(s) | "+order.getTotalAmount()+" ₹");
        holder.shopName.setText(order.getShopID());


    }

    @Override
    public int getItemCount() {
        return homeHeader.size();
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}