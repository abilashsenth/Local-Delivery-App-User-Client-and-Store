package com.tuyuservices.tuyumain;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private List<Orders> orders;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, date, time, timepreference, orders, total, number;
        public ImageView displayImage;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.list_item_type_one_name);
            address = (TextView) view.findViewById(R.id.list_item_type_one_address);
            date = (TextView) view.findViewById(R.id.list_item_type_one_date);
            time = (TextView) view.findViewById(R.id.list_item_type_one_time);
            timepreference = (TextView) view.findViewById(R.id.list_item_type_one_timepreference);
            total = (TextView) view.findViewById(R.id.list_item_type_one_serviceprice);
            orders = (TextView) view.findViewById(R.id.list_item_type_one_services);
            number =(TextView) view.findViewById(R.id.list_item_type_one_number);
        }
    }

    public ListAdapter(List<Orders> mList){
        orders = mList;
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
        holder.name.setText(order.getName());
        holder.address.setText(order.getAddress());
        holder.date.setText(order.getDate());
        holder.time.setText(order.getTime());
        holder.timepreference.setText(order.getTimepreference());
        holder.orders.setText(order.getServicesordered());
        holder.total.setText(order.getTotalAmount());
        holder.number.setText(String
                .valueOf(order.getNumber()));

        // holder.displayImage.setImageURI(Uri.parse(order.getImageUri()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }



}
