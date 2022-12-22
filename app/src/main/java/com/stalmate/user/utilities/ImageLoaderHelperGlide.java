package com.stalmate.user.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.stalmate.user.R;


public class ImageLoaderHelperGlide extends Activity {
    public static void setGlide(Context context, ImageView imageView, String imageLink,int placeholer) {
        int radius = context.getResources().getDimensionPixelSize(R.dimen.dp_20);
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                // .dontAnimate()
                .transform(new CenterCrop(), new RoundedCorners(4))
                .placeholder(placeholer)
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT);
        Glide.with(context)
                .load(imageLink)
                .apply(requestOptions)
                .into(imageView);
      //  Glide.get(context).clearMemory();
    }


    public static void setGlideCorner(Context context, ImageView imageView, String imageLink, int placeholer) {
        int radius = context.getResources().getDimensionPixelSize(R.dimen.dp_20);
        RequestOptions requestOptions = new RequestOptions()
              .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
               // .dontAnimate()
                .transform(new CenterCrop(), new RoundedCorners(5))
              .placeholder(placeholer)
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT);
        Glide.with(context)
                .load(imageLink)
                .apply(requestOptions)
                .into(imageView);
      //  Glide.get(context).clearMemory();
    }

}
