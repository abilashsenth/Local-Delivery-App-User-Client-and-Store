package com.tuyuservices.tuyumain;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

public class ImageLoadTask {

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

}
