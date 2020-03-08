package com.tuyuservices.tuyu;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URI;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private List<Service> services;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView displayImage;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.list_item_type_one_text);
            displayImage = (ImageView) view.findViewById(R.id.list_item_type_one_image);
        }
    }

    public ListAdapter(List<Service> mList){
        services  = mList;
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
        Service service = services.get(position);
        holder.title.setText(service.getServiceName());
        holder.displayImage.setImageURI(Uri.parse(service.getImageUri()));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }



}
