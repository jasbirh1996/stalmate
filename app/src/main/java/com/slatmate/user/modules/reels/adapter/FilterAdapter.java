package com.slatmate.user.modules.reels.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.filter.Filters;
import com.otaliastudios.cameraview.filters.BrightnessFilter;
import com.otaliastudios.cameraview.filters.GammaFilter;
import com.otaliastudios.cameraview.filters.GrayscaleFilter;
import com.otaliastudios.cameraview.filters.SepiaFilter;
import com.otaliastudios.cameraview.filters.VignetteFilter;
import com.slatmate.user.R;
import com.slatmate.user.modules.reels.utils.VideoFilter;


import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSolarizeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private final Context mContext;
    private final List<VideoFilter> mFilters = Arrays.asList(VideoFilter.values());
    private OnFilterSelectListener mListener;
    private final Bitmap mThumbnail;
    private CameraView cameraView;
    private Boolean isImage;

    public FilterAdapter(Context context, Bitmap thumbnail, CameraView cameraVieww, Boolean isImage) {
        mContext = context;
        mThumbnail = thumbnail;
        cameraView = cameraVieww;
        this.isImage = isImage;
    }

    @Override
    public int getItemCount() {
        return mFilters.size();
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_filter, parent, false);
        FilterViewHolder holder = new FilterViewHolder(view);
        holder.setIsRecyclable(false);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(24));
        holder.image.setImage(mThumbnail);
        holder.image.setVisibility(View.VISIBLE);
        //  holder.filteredcameraView = cameraView;
        return holder;
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {


        final VideoFilter filter = mFilters.get(position);
        if (isImage) {
            switch (filter) {
                case BRIGHTNESS: {
                    GPUImageBrightnessFilter glf = new GPUImageBrightnessFilter();
                    glf.setBrightness(0.2f);
                    holder.image.setFilter(glf);
                    break;
                }
                case EXPOSURE:
                    holder.image.setFilter(new GPUImageExposureFilter());
                    break;
                case GAMMA: {
                    GPUImageGammaFilter glf = new GPUImageGammaFilter();
                    glf.setGamma(2f);
                    holder.image.setFilter(glf);
                    break;
                }
                case GRAYSCALE:
                    holder.image.setFilter(new GPUImageGrayscaleFilter());
                    break;
                case HAZE: {
                    GPUImageHazeFilter glf = new GPUImageHazeFilter();
                    glf.setSlope(-0.5f);
                    holder.image.setFilter(glf);
                    break;
                }
                case INVERT:
                    holder.image.setFilter(new GPUImageColorInvertFilter());
                    break;
                case MONOCHROME:
                    holder.image.setFilter(new GPUImageMonochromeFilter());
                    break;
                case PIXELATED: {
                    GPUImagePixelationFilter glf = new GPUImagePixelationFilter();
                    glf.setPixel(5);
                    holder.image.setFilter(glf);
                    break;
                }
                case POSTERIZE:
                    holder.image.setFilter(new GPUImagePosterizeFilter());
                    break;
                case SEPIA:
                    holder.image.setFilter(new GPUImageSepiaToneFilter());
                    break;
                case SHARP: {
                    GPUImageSharpenFilter glf = new GPUImageSharpenFilter();
                    glf.setSharpness(1f);
                    holder.image.setFilter(glf);
                    break;
                }
                case SOLARIZE:
                    holder.image.setFilter(new GPUImageSolarizeFilter());
                    break;
                case VIGNETTE:
                    holder.image.setFilter(new GPUImageVignetteFilter());
                    break;
                default:
                    holder.image.setFilter(new GPUImageFilter());
                    break;
            }

            String name = filter.name().toLowerCase(Locale.US);
            holder.name.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
            holder.itemView.setOnClickListener(view -> {
                if (mListener != null) {
                    mListener.onSelectFilter(filter);
                }
            });
        }
    }

    public void setListener(OnFilterSelectListener listener) {
        mListener = listener;
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {

        public GPUImageView image;
        public TextView name;


        public FilterViewHolder(@NonNull View root) {
            super(root);
            image = root.findViewById(R.id.image);
            name = root.findViewById(R.id.name);


        }
    }


    public interface OnFilterSelectListener {

        void onSelectFilter(VideoFilter filter);
    }
}
