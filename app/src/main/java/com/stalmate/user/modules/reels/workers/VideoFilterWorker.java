package com.stalmate.user.modules.reels.workers;

import android.content.Context;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.composer.Rotation;
import com.daasuu.gpuv.egl.filter.GlBrightnessFilter;
import com.daasuu.gpuv.egl.filter.GlExposureFilter;
import com.daasuu.gpuv.egl.filter.GlFilterGroup;

import com.daasuu.gpuv.egl.filter.GlGammaFilter;
import com.daasuu.gpuv.egl.filter.GlGrayScaleFilter;
import com.daasuu.gpuv.egl.filter.GlHazeFilter;
import com.daasuu.gpuv.egl.filter.GlInvertFilter;
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter;
import com.daasuu.gpuv.egl.filter.GlPixelationFilter;
import com.daasuu.gpuv.egl.filter.GlPosterizeFilter;
import com.daasuu.gpuv.egl.filter.GlSepiaFilter;
import com.daasuu.gpuv.egl.filter.GlSharpenFilter;
import com.daasuu.gpuv.egl.filter.GlSolarizeFilter;
import com.daasuu.gpuv.egl.filter.GlVignetteFilter;
import com.google.common.util.concurrent.ListenableFuture;
import com.stalmate.user.modules.reels.utils.VideoFilter;
import com.stalmate.user.modules.reels.utils.VideoUtil;

import java.io.IOException;


public class VideoFilterWorker extends ListenableWorker {

    public static final String KEY_FILTER = "filter";
    public static final String KEY_INPUT = "input";
    public static final String KEY_OUTPUT = "output";
    public static final String TAG = "ClipFilterWorker";

    public VideoFilterWorker(@NonNull Context context, @NonNull WorkerParameters params) {
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
        GPUMp4Composer composer = new GPUMp4Composer(input, output);
        Size size = null;
        size = VideoUtil.getDimensions(input);
        composer.videoBitrate((int) (.07 * 30 * size.getWidth() * size.getHeight()));
        VideoFilter filter = VideoFilter.valueOf(getInputData().getString(KEY_FILTER));
        switch (filter) {
            case BRIGHTNESS: {
                GlBrightnessFilter glf = new GlBrightnessFilter();
                glf.setBrightness(0.2f);
                composer.filter(glf);
                break;
            }
            case EXPOSURE:
                composer.filter(new GlExposureFilter());
                break;
            case GAMMA: {
                GlGammaFilter glf = new GlGammaFilter();
                glf.setGamma(2f);
                composer.filter(glf);
                break;
            }
            case GRAYSCALE:
                composer.filter(new GlGrayScaleFilter());
                break;
            case HAZE: {
                GlHazeFilter glf = new GlHazeFilter();
                glf.setSlope(-0.5f);
                composer.filter(glf);
                break;
            }
            case INVERT:
                composer.filter(new GlInvertFilter());
                break;
            case MONOCHROME:
                composer.filter(new GlMonochromeFilter());
                break;
            case PIXELATED:
                composer.filter(new GlPixelationFilter());
                break;
            case POSTERIZE:
                composer.filter(new GlPosterizeFilter());
                break;
            case SEPIA:
                composer.filter(new GlSepiaFilter());
                break;
            case SHARP: {
                GlSharpenFilter glf = new GlSharpenFilter();
                glf.setSharpness(1f);
                composer.filter(glf);
                break;
            }
            case SOLARIZE:
                composer.filter(new GlSolarizeFilter());
                break;
            case VIGNETTE:
                composer.filter(new GlVignetteFilter());
                break;
            default:
                break;
        }

        composer.listener(new GPUMp4Composer.Listener() {
            @Override
            public void onProgress(double progress) { }


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
