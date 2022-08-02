package com.slatmate.user.modules.reels.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.IOException;


public class FileDownloadWorker extends Worker {

    public static final String KEY_PATH = "path";
    public static final String KEY_URL = "url";
    private static final String TAG = "FileDownloadWorker";


    public FileDownloadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String path = getInputData().getString(KEY_PATH);
        String url = getInputData().getString(KEY_URL);
        //noinspection ConstantConditions
/*        return Result.success();*/

        return Result.failure();
    }
}
