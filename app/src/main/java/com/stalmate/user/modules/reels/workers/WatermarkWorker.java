package com.stalmate.user.modules.reels.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.daasuu.mp4compose.VideoFormatMimeType;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlWatermarkFilter;
import com.google.common.util.concurrent.ListenableFuture;
import com.stalmate.user.R;
import com.stalmate.user.modules.reels.utils.VideoUtil;

import java.io.IOException;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;


import com.google.common.util.concurrent.ListenableFuture;


public class WatermarkWorker extends ListenableWorker {

    public static final String KEY_INPUT = "input";
    public static final String KEY_OUTPUT = "output";
    public static final String TAG = "WatermarkWorker";
    public static final String ICON ="icon" ;
    public static final String HEIGHT ="height" ;
    public static final String WIDTH ="width" ;


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
        String str = getInputData().getString(ICON);




        Size size = VideoUtil.getDimensions(input);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap watermark = BitmapFactory.decodeFile(str,bmOptions);
        Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, size.getWidth(), size.getHeight());
        watermark.recycle();
        watermark = scaled;
        Log.d("asdasdasd",String.valueOf(size.getHeight()));
        Log.d("asdasdasd",String.valueOf(size.getWidth()));
       /* int optimal = (int) (size.getWidth()  * .2);
        if (watermark.getWidth() != optimal || watermark.getHeight() != optimal) {
         //   Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, optimal, optimal);
            Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, width, height);
            watermark.recycle();
            watermark = scaled;
        }*/

 /*       BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap watermark = BitmapFactory.decodeFile(str,bmOptions);
        watermark = Bitmap.createScaledBitmap(watermark,size.getWidth(),size.getHeight(),true);*/
        // Log.v(TAG, "Watermark bitmap size is " + watermark.getWidth() + 'x' + watermark.getHeight() + '.');
        String output = getInputData().getString(KEY_OUTPUT);

        Mp4Composer composer = new Mp4Composer(input, output);
        composer.videoBitrate((int) (.07 * 30 * size.getWidth() * size.getHeight()));
        composer.filter(new GlWatermarkFilter(watermark, GlWatermarkFilter.Position.LEFT_BOTTOM));
        composer.listener(new Mp4Composer.Listener() {
            @Override
            public void onProgress(double progress) {

            }

            @Override
            public void onCurrentWrittenVideoTime(long timeUs) {

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
