package com.tuyuservices.tuyu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    private ArrayList<Shop> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context mContext;



    // data is passed into the constructor
    HomeRecyclerAdapter(Context context, ArrayList<Shop> data, Context mContext) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = mContext;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.home_recycler_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = mData.get(position).name;
        String rating = String.valueOf(mData.get(position).rating);
        String samplePrice = String.valueOf(mData.get(position).samplePrice);
        String distance = String.valueOf( mData.get(position).dist/1000);
        String imageUrl = String.valueOf(mData.get(position).shopImageUrl);
        Log.e("THE URL IS" , imageUrl);


        holder.homeRecyclerNameText.setText(name);
        holder.homeRecyclerSamplePriceText.setText("Avg Pricing: "+samplePrice+"₹");
        holder.homeRecyclerRatingText.setText("⭐"+rating);
        holder.homeRecyclerDistanceText.setText(distance.substring(0, Math.min(distance.length(), 4)) + "Kms");
        //set the image bitmap from url ( background asynctask - ImageLoadTask)
        ImageLoadTask imageLoadTask = new ImageLoadTask(imageUrl,holder.shopThumbnail,mContext);
        //imageLoadTask.execute();

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView homeRecyclerNameText, homeRecyclerRatingText, homeRecyclerSamplePriceText, homeRecyclerDistanceText;
        ImageView shopThumbnail;
        ViewHolder(View itemView) {
            super(itemView);
            homeRecyclerNameText = itemView.findViewById(R.id.home_recycler_name);
            homeRecyclerRatingText = itemView.findViewById(R.id.home_recycler_rating);
            homeRecyclerSamplePriceText = itemView.findViewById(R.id.home_recycler_sampleprice);
            homeRecyclerDistanceText = itemView.findViewById(R.id.home_recycler_distance);
            shopThumbnail = itemView.findViewById(R.id.home_recycler_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).name;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}