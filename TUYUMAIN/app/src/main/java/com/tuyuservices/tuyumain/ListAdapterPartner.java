package com.tuyuservices.tuyumain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapterPartner extends RecyclerView.Adapter<ListAdapterPartner.MyViewHolder> {

    private List<Partner> partners;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, date, time, timepreference, orders, total, number;
        public ImageView displayImage;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.list_item_type_two_name);
        }
    }

    public ListAdapterPartner(List<Partner> mList){
        partners = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_type_two, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //TODO add image bitmap
        Partner partner = partners.get(position);
        holder.name.setText(partner.getUID());


        // holder.displayImage.setImageURI(Uri.parse(order.getImageUri()));
    }

    @Override
    public int getItemCount() {
        return partners.size();
    }



}
