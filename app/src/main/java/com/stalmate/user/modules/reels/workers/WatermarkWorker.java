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

import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlWatermarkFilter;
import com.google.common.util.concurrent.ListenableFuture;
import com.stalmate.user.R;
import com.stalmate.user.modules.reels.utils.VideoUtil;

import java.io.IOException;


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
        Log.d("alsdkasd10","poaskdasd");
        String input = getInputData().getString(KEY_INPUT);
        String iconPath = getInputData().getString(ICON);
        //int height = getInputData().getInt(ICON,0);
        // int width = getInputData().getInt(ICON,0);
        Size size = null;
        try {
            size = VideoUtil.getDimensions(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
/*        Bitmap watermark =
                BitmapFactory.decodeResource(
                        // getApplicationContext().getResources(), R.drawable.ic_user_icon);
                        getApplicationContext().getResources(),iconDrawable);*/


        Bitmap watermark = BitmapFactory.decodeFile(iconPath);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getRealMetrics(displayMetrics);



        Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, (int) (size.getWidth()), (int) (size.getHeight()));
        watermark.recycle();
        watermark = scaled;
       /* int optimal = (int) (size.getWidth()  * .2);
        if (watermark.getWidth() != optimal || watermark.getHeight() != optimal) {
         //   Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, optimal, optimal);
            Bitmap scaled = ThumbnailUtils.extractThumbnail(watermark, 250, 250);
            watermark.recycle();
            watermark = scaled;
        }*/
        Log.d(TAG, "Watermark bitmap size is " + watermark.getWidth() + 'x' + watermark.getHeight() + '.');
        String output = getInputData().getString(KEY_OUTPUT);
        Mp4Composer composer = new Mp4Composer(input, output);
      //  composer.videoBitrate((int) (.07 * 30 * size.getWidth() * size.getHeight()));
        //  composer.filter(new GlWatermarkFilter(watermark, GlWatermarkFilter.Position.RIGHT_TOP));



        GlWatermarkFilter.Position position =  GlWatermarkFilter.Position.LEFT_TOP;
        composer.filter(new GlWatermarkFilter(watermark, position));

        composer.listener(new Mp4Composer.Listener() {
            @Override
            public void onProgress(double progress) {
                Log.d("alsdkasd10","poashjghkdasd");
            }

            @Override
            public void onCurrentWrittenVideoTime(long timeUs) {
                Log.d("alsdkasd10","poasqwekdasd");
            }


            @Override
            public void onCompleted() {
                Log.d("alsdkasd10","poaskdawersd");
                Log.d(TAG, "MP4 composition has finished.");
                completer.set(Result.success());
            }

            @Override
            public void onCanceled() {
                Log.d("alsdkasd10","poasertkdasd");
                Log.d(TAG, "MP4 composition was cancelled.");
                completer.setCancelled();
            }

            @Override
            public void onFailed(Exception e) {
                Log.d("alsdkasd10","poasertkdasd");
                Log.d(TAG, "MP4 composition failed with error.", e);
                completer.setException(e);
            }
        });
        composer.start();
    }




}

