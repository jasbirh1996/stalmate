package com.stalmate.user.modules.reels.filters.exoplayerfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import com.stalmate.user.R;
import com.stalmate.user.modules.reels.filters.epf.filter.GlBilateralFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlBoxBlurFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlBrightnessFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlBulgeDistortionFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlCGAColorspaceFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlContrastFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlCrosshatchFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlExposureFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlFilterGroup;
import com.stalmate.user.modules.reels.filters.epf.filter.GlGammaFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlGaussianBlurFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlGrayScaleFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlHalftoneFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlHazeFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlHighlightShadowFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlHueFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlInvertFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlLookUpTableFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlLuminanceFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlLuminanceThresholdFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlMonochromeFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlOpacityFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlPixelationFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlPosterizeFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlRGBFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlSaturationFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlSepiaFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlSharpenFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlSolarizeFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlSphereRefractionFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlSwirlFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlToneCurveFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlToneFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlVibranceFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlVignetteFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlWatermarkFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlWeakPixelInclusionFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlWhiteBalanceFilter;
import com.stalmate.user.modules.reels.filters.epf.filter.GlZoomBlurFilter;

import com.stalmate.user.modules.reels.filters.epf.filter.GlFilter;
import com.stalmate.user.modules.reels.filters.exoplayerfilter.filtersample.GlBitmapOverlaySample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sudamasayuki on 2017/05/18.
 */

public enum FilterType {
    DEFAULT,
    BITMAP_OVERLAY_SAMPLE,
    BILATERAL_BLUR,
    BOX_BLUR,
    BRIGHTNESS,
    BULGE_DISTORTION,
    CGA_COLORSPACE,
    CONTRAST,
    CROSSHATCH,
    EXPOSURE,
    FILTER_GROUP_SAMPLE,
    GAMMA,
    GAUSSIAN_FILTER,
    GRAY_SCALE,
    HAZE,
    HALFTONE,
    HIGHLIGHT_SHADOW,
    HUE,
    INVERT,
    LUMINANCE,
    LUMINANCE_THRESHOLD,
    MONOCHROME,
    OPACITY,
    OVERLAY,
    PIXELATION,
    POSTERIZE,
    RGB,
    SATURATION,
    SEPIA,
    SHARP,
    SOLARIZE,
    SPHERE_REFRACTION,
    SWIRL,
    TONE_CURVE_SAMPLE,
    TONE,
    VIBRANCE,
    VIGNETTE,
    LOOK_UP_TABLE_SAMPLE,
    WATERMARK,
    WEAK_PIXEL,
    WHITE_BALANCE,
    ZOOM_BLUR,
    ;


    public static List<FilterType> createFilterList() {
        return Arrays.asList(FilterType.values());
    }

//    public static GlFilter createGlFilter(FilterType filterType, Context context) {
//        switch (filterType) {
//            case DEFAULT:
//                return new GlFilter();
//            case SEPIA:
//                return new GlSepiaFilter();
//            case GRAY_SCALE:
//                return new GlGrayScaleFilter();
//            case INVERT:
//                return new GlInvertFilter();
//            case HAZE:
//                return new GlHazeFilter();
//            case MONOCHROME:
//                return new GlMonochromeFilter();
//            case BILATERAL_BLUR:
//                return new GlBilateralFilter();
//            case BOX_BLUR:
//                return new GlBoxBlurFilter();
//            case LOOK_UP_TABLE_SAMPLE:
//                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_sample);
//
//                return new GlLookUpTableFilter(bitmap);
//            case TONE_CURVE_SAMPLE:
//                try {
//                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
//                    return new GlToneCurveFilter(is);
//                } catch (IOException e) {
//                    Log.e("FilterType", "Error");
//                }
//                return new GlFilter();
//
//            case SPHERE_REFRACTION:
//                return new GlSphereRefractionFilter();
//            case VIGNETTE:
//                return new GlVignetteFilter();
//            case FILTER_GROUP_SAMPLE:
//                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
//            case GAUSSIAN_FILTER:
//                return new GlGaussianBlurFilter();
//            case BULGE_DISTORTION:
//                return new GlBulgeDistortionFilter();
//            case CGA_COLORSPACE:
//                return new GlCGAColorspaceFilter();
//            case SHARP:
//                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
//                glSharpenFilter.setSharpness(4f);
//                return glSharpenFilter;
//            case BITMAP_OVERLAY_SAMPLE:
//                return new GlBitmapOverlaySample(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round));
//            default:
//                return new GlFilter();
//        }
//    }


    public static GlFilter createGlFilter(FilterType filterType, Context context) {
        switch (filterType) {
            case DEFAULT:
                return new GlFilter();
            case BILATERAL_BLUR:
                return new GlBilateralFilter();
            case BOX_BLUR:
                return new GlBoxBlurFilter();
            case BRIGHTNESS:
                GlBrightnessFilter glBrightnessFilter = new GlBrightnessFilter();
                glBrightnessFilter.setBrightness(0.2f);
                return glBrightnessFilter;
            case BULGE_DISTORTION:
                return new GlBulgeDistortionFilter();
            case CGA_COLORSPACE:
                return new GlCGAColorspaceFilter();
            case CONTRAST:
                GlContrastFilter glContrastFilter = new GlContrastFilter();
                glContrastFilter.setContrast(2.5f);
                return glContrastFilter;
            case CROSSHATCH:
                return new GlCrosshatchFilter();
            case EXPOSURE:
                return new GlExposureFilter();
            case FILTER_GROUP_SAMPLE:
                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
            case GAMMA:
                GlGammaFilter glGammaFilter = new GlGammaFilter();
                glGammaFilter.setGamma(2f);
                return glGammaFilter;
            case GAUSSIAN_FILTER:
                return new GlGaussianBlurFilter();
            case GRAY_SCALE:
                return new GlGrayScaleFilter();
            case HALFTONE:
                return new GlHalftoneFilter();
            case HAZE:
                GlHazeFilter glHazeFilter = new GlHazeFilter();
                glHazeFilter.setSlope(-0.5f);
                return glHazeFilter;
            case HIGHLIGHT_SHADOW:
                return new GlHighlightShadowFilter();
            case HUE:
                return new GlHueFilter();
            case INVERT:
                return new GlInvertFilter();
            case LOOK_UP_TABLE_SAMPLE:
               // Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_sample);
            /*    return new GlLookUpTableFilter(bitmap);*/
                return new GlInvertFilter();
            case LUMINANCE:
                return new GlLuminanceFilter();
            case LUMINANCE_THRESHOLD:
                return new GlLuminanceThresholdFilter();
            case MONOCHROME:
                return new GlMonochromeFilter();
            case OPACITY:
                return new GlOpacityFilter();
            case PIXELATION:
                return new GlPixelationFilter();
            case POSTERIZE:
                return new GlPosterizeFilter();
            case RGB:
                GlRGBFilter glRGBFilter = new GlRGBFilter();
                glRGBFilter.setRed(0f);
                return glRGBFilter;
            case SATURATION:
                return new GlSaturationFilter();
            case SEPIA:
                return new GlSepiaFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(4f);
                return glSharpenFilter;
            case SOLARIZE:
                return new GlSolarizeFilter();
            case SPHERE_REFRACTION:
                return new GlSphereRefractionFilter();
            case SWIRL:
                return new GlSwirlFilter();
            case TONE_CURVE_SAMPLE:
                try {
                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
                    return new GlToneCurveFilter(is);
                } catch (IOException e) {
                    Log.e("FilterType", "Error");
                }
                return new GlFilter();
            case TONE:
                return new GlToneFilter();
            case VIBRANCE:
                GlVibranceFilter glVibranceFilter = new GlVibranceFilter();
                glVibranceFilter.setVibrance(3f);
                return glVibranceFilter;
            case VIGNETTE:
                return new GlVignetteFilter();
            case WATERMARK:
                return new GlWatermarkFilter(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round), GlWatermarkFilter.Position.RIGHT_BOTTOM);
            case WEAK_PIXEL:
                return new GlWeakPixelInclusionFilter();
            case WHITE_BALANCE:
                GlWhiteBalanceFilter glWhiteBalanceFilter = new GlWhiteBalanceFilter();
                glWhiteBalanceFilter.setTemperature(2400f);
                glWhiteBalanceFilter.setTint(2f);
                return glWhiteBalanceFilter;
            case ZOOM_BLUR:
                return new GlZoomBlurFilter();
            case BITMAP_OVERLAY_SAMPLE:
                return new GlBitmapOverlaySample(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round));
            default:
                return new GlFilter();
        }
    }


}
