package com.tuyuservices.tuyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoadTask /*extends AsyncTask<Void, Void, Bitmap>*/ {
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

    /**
     * The AsyncTask is not used, Since Glide Image Loading/Cache Library proved to run faster and more memory/processor efficient
     * And also for supporting future use of Gifs
     */

    private String url;
    private ImageView imageView;
    private Context mContext;

    public ImageLoadTask(String url, ImageView imageView, Context mContext) {
        this.url = url;
        this.mContext = mContext;
        this.imageView = imageView;
        runGlide();

    }

    private void runGlide() {
        Glide.with(mContext).load(url).into(imageView);

    }

    /*
    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }

     */

    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/


}