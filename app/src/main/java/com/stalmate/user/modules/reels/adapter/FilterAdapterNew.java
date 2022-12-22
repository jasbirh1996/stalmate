package com.stalmate.user.modules.reels.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.otaliastudios.cameraview.CameraView;
import com.stalmate.user.R;
import com.stalmate.user.modules.reels.utils.VideoFilter;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilterAdapterNew extends RecyclerView.Adapter<FilterAdapterNew.FilterViewHolder> {
    private Integer showingposition=0;
    private final Context mContext;
    private final List<FilterData> mFilters = new ArrayList<FilterData>();
    private OnFilterSelectListener mListener;

    private CameraView cameraView;
    private Boolean isImage;
    private Boolean isAllShowing=true;
    public FilterAdapterNew(Context context, CameraView cameraVieww, Boolean isImage) {
        mContext = context;

        cameraView = cameraVieww;
        this.isImage = isImage;
        mFilters.add(new FilterData(true,VideoFilter.NONE,R.color.white));
        mFilters.add(new FilterData(true,VideoFilter.BRIGHTNESS,R.color.colorLightGray));
        mFilters.add(new FilterData(true,VideoFilter.EXPOSURE,R.color.colorGray));
        mFilters.add(new FilterData(true,VideoFilter.GAMMA,R.color.app_color));
        mFilters.add(new FilterData(true,VideoFilter.GRAYSCALE,R.color.colorTextDarkGray));
        mFilters.add(new FilterData(true,VideoFilter.HAZE,R.color.app_color));
        mFilters.add(new FilterData(true,VideoFilter.INVERT,R.color.app_color));
        mFilters.add(new FilterData(true,VideoFilter.MONOCHROME,R.color.colorGray));
        mFilters.add(new FilterData(true,VideoFilter.PIXELATED,R.color.brown_color_picker));
        mFilters.add(new FilterData(true,VideoFilter.POSTERIZE,R.color.brown_color_picker));
        mFilters.add(new FilterData(true,VideoFilter.SEPIA,R.color.brown_color_picker));
        mFilters.add(new FilterData(true,VideoFilter.SHARP,R.color.white));
        mFilters.add(new FilterData(true,VideoFilter.SOLARIZE,R.color.white));
        mFilters.add(new FilterData(true,VideoFilter.VIGNETTE,R.color.app_color));


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





        //  Glide.with(mContext).load(mThumbnail).apply(requestOptions).into(holder.image.)



        holder.image.setVisibility(View.VISIBLE);
        //  holder.filteredcameraView = cameraView;
        return holder;
    }
    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = mContext.getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, 60, 60);
        drawable.draw(canvas);
        return bitmap;
    }
    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {

        final VideoFilter filter = mFilters.get(position).filterType;

        String name = filter.name().toLowerCase(Locale.US);
        holder.name.setText(name.substring(0, 1).toUpperCase() + name.substring(1));

        holder.image.setBackground(ContextCompat.getDrawable(mContext,mFilters.get(position).color));



        if (position==showingposition){
            holder.layout.setBackground(ContextCompat.getDrawable(mContext,R.drawable.round_circle_primary));
        }else {
            holder.layout.setBackground(ContextCompat.getDrawable(mContext,R.drawable.round_circle_white));
        }




        if (isAllShowing){
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }else {
            if (position==showingposition){
                holder.itemView.setVisibility(View.VISIBLE);
               holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            }else {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        }






        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                showingposition=position;
              //  isAllShowing= !isAllShowing;
                mListener.onSelectFilter(filter,position,isAllShowing,false);
            }
        });



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {

                if (mListener != null) {
                    showingposition=position;
                    //  isAllShowing= !isAllShowing;
                    mListener.onSelectFilter(filter,position,isAllShowing,true);
                }
                return true;
            }
        });

    }

    public void setListener(OnFilterSelectListener listener) {
        mListener = listener;
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;

        public ConstraintLayout layout;

        public FilterViewHolder(@NonNull View root) {
            super(root);
            image = root.findViewById(R.id.image);
            name = root.findViewById(R.id.name);

            layout = root.findViewById(R.id.frmBorder);
        }
    }



    public void setCenterColor(Integer position){

        showingposition=position;
        notifyDataSetChanged();


    }
    
    
    
    public void showAllFilters(Boolean isShow){

        if (isShow){
            for (int i = 0; i<mFilters.size(); i++){
                mFilters.get(i).isShowing=true;
            }
            notifyDataSetChanged();


        }else {
            for (int i = 0; i<mFilters.size(); i++){
                if (showingposition==i){
                    mFilters.get(i).isShowing=true;
                }else {
                    mFilters.get(i).isShowing=false;
                }

            }
            notifyDataSetChanged();
        }

    }


    public interface OnFilterSelectListener {
        void onSelectFilter(VideoFilter filter, int position, Boolean isAllShowing,Boolean isLongClick);
    }
    public class FilterData {


        FilterData(Boolean isShowing, VideoFilter filterType, int color){
            this.isShowing=isShowing;
            this.filterType=filterType;
            this.color=color;
        }

        public Boolean isShowing;
        public VideoFilter filterType;
        public int color;
    }


}




