package com.stalmate.user.modules.reels.workers;

import android.content.Context;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.VideoFormatMimeType;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.google.common.util.concurrent.ListenableFuture;
import com.stalmate.user.modules.reels.utils.VideoUtil;

public class VideoSpeedWorker extends ListenableWorker {

    public static final String KEY_INPUT = "input";
    public static final String KEY_OUTPUT = "output";
    public static final String KEY_SPEED = "speed";
    public static final String TAG = "VideoSpeedWorker";

    public VideoSpeedWorker(@NonNull Context context, @NonNull WorkerParameters params) {
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
        String output = getInputData().getString(KEY_OUTPUT);
        float speed = getInputData().getFloat(KEY_SPEED, 1f);
        Mp4Composer composer = new Mp4Composer(input, output);
        composer.fillMode(FillMode.PRESERVE_ASPECT_FIT);
        Size size = null;
        size = VideoUtil.getDimensions(input);
        composer.videoBitrate((int) (.07 * 30 * size.getWidth() * size.getHeight()));
        composer.mute(true);
        composer.timeScale(speed);
        composer.listener(new Mp4Composer.Listener() {

            @Override
            public void onProgress(double progress) { }

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
