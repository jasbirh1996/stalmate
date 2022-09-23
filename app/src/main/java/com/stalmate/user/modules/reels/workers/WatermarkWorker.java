package com.stalmate.user.modules.reels.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlWatermarkFilter;
import com.google.common.util.concurrent.ListenableFuture;
import com.stalmate.user.R;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class WatermarkWorker extends ListenableWorker {

    public static final String KEY_INPUT = "input";
    public static final String KEY_OUTPUT = "output";
    public static final String TAG = "WatermarkWorker";
    public static final String ICON ="icon" ;
    public static final String HEIGHT ="height" ;
    public static final String WIDTH ="width" ;
    Bitmap bitmap;
    Canvas canvas;

    public WatermarkWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            doActualWork(completer);
            return null;
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void doActualWork(CallbackToFutureAdapter.Completer<Result> completer) {
        String input = getInputData().getString(KEY_INPUT);
        String icon = getInputData().getString(ICON);
        //int height = getInputData().getInt(ICON,0);
       // int width = getInputData().getInt(ICON,0);
     //   Size size = VideoUtil.getDimensions(input);

        Drawable yourDrawable;
        try {
            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(Uri.parse(icon));
            yourDrawable = Drawable.createFromStream(inputStream, Uri.parse(icon).toString() );
        } catch (FileNotFoundException e) {
            yourDrawable = getApplicationContext().getDrawable(R.drawable.placeholder_filter);
        }

        Bitmap watermark = BitmapFactory.decodeFile(icon);
 /*       Bitmap watermark =
                BitmapFactory.decodeResource(
                       // getApplicationContext().getResources(), R.drawable.ic_user_icon);
                        getApplicationContext().getResources(),yourDrawable);*/
        Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, 500, 500);
        watermark.recycle();
        watermark = scaled;
       /* int optimal = (int) (size.getWidth()  * .2);
        if (watermark.getWidth() != optimal || watermark.getHeight() != optimal) {
         //   Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, optimal, optimal);
            Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, 250, 250);
            watermark.recycle();
            watermark = scaled;
        }*/
        Log.v(TAG, "Watermark bitmap size is " + watermark.getWidth() + 'x' + watermark.getHeight() + '.');
        String output = getInputData().getString(KEY_OUTPUT);
        Mp4Composer composer = new Mp4Composer(input, output);
      //  composer.videoBitrate((int) (.07 * 30 * size.getWidth() * size.getHeight()));
      //  composer.filter(new GlWatermarkFilter(watermark, GlWatermarkFilter.Position.RIGHT_TOP));



         GlWatermarkFilter.Position position =  GlWatermarkFilter.Position.valueOf("x=w-tw-10:y=h-th-10");
         composer.filter(new GlWatermarkFilter(watermark, position));

        composer.listener(new Mp4Composer.Listener() {
            @Override
            public void onProgress(double progress) {

            }


            @Override
            public void onCompleted() {
                Log.d(TAG, "MP4 composition has finished.");
                completer.set(Result.success());
            }

            @Override
            public void onCanceled() {
                Log.d(TAG, "MP4 composition was cancelled.");
                completer.setCancelled();
            }

            @Override
            public void onFailed(Exception e) {
                Log.d(TAG, "MP4 composition failed with error.", e);
                completer.setException(e);
            }
        });
        composer.start();
    }
}
