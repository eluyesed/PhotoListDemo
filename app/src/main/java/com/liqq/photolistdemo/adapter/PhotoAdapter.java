package com.liqq.photolistdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liqq.photolistdemo.R;
import com.liqq.photolistdemo.bean.LruPhoto;
import com.liqq.photolistdemo.widget.ScaleImageView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liqq on 2018/12/22.
 *
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder>{

    private final String TAG = "PhotoAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private int recyclerViewWidth, recyclerViewHeight;

    private List<LruPhoto> photoList;

    public PhotoAdapter(Context context){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        recyclerViewWidth = dm.widthPixels;
        recyclerViewHeight = dm.heightPixels;
        initLruBitmapList();
    }

    public void setPhotoList(List<LruPhoto> photoList){
        this.photoList = photoList;
    }


    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.photo_list_item,parent,false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoHolder holder, int position) {
        //Glide.with(mContext).load(photoList.get(position)).into(holder.photoImageView);
        Bitmap bitmap = getLruBitmap(String.valueOf(position));
        LruPhoto lruPhoto = photoList.get(position);
        /*if(lruPhoto.isCreate()){
            holder.photoImageView.setInitSize(lruPhoto.getWidth(),lruPhoto.getHeight());
        }else{
            holder.photoImageView.setInitSize(recyclerViewWidth/2,recyclerViewHeight/3);
        }*/
      //  holder.photoImageView.setInitSize(recyclerViewWidth/2,recyclerViewHeight/3);
        if(bitmap == null){
            asyncGetBitmap(holder,position);
        }else{
            holder.photoImageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public int getItemCount() {
        return photoList == null ? 0 : photoList.size();
    }


    private void asyncGetBitmap(final PhotoHolder holder, final int position){
        Glide.with(mContext).load(photoList.get(position).getImageResources()).asBitmap().skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                int imageWidth = resource.getWidth();
                int imageHeight = resource.getHeight();
                float saceWidth = (float) recyclerViewWidth/imageWidth;
                float saceHeight = (float) recyclerViewHeight /imageHeight;
                Matrix matrix = new Matrix();
                matrix.postScale(saceWidth, saceHeight); // 长和宽放大缩小的比例
                Bitmap changeBitmap = Bitmap.createBitmap(resource,0,0,imageWidth,imageHeight);
                putLruBitmap(String.valueOf(position),changeBitmap);
                LruPhoto lruPhoto = photoList.get(position);
                boolean isCreate = false;
                if(lruPhoto.isCreate()){
                    isCreate = true;
                    lruPhoto.setCreate(true);
                    lruPhoto.setWidth(changeBitmap.getWidth());
                    lruPhoto.setHeight(changeBitmap.getHeight());
                  //  holder.photoImageView.setInitSize(changeBitmap.getWidth(),changeBitmap.getHeight());
                }
                Log.i(TAG,"recyclerViewWidth "  + recyclerViewWidth +" recyclerViewHeight " + recyclerViewHeight+" imageWidth " + imageWidth +" imageHeight " + imageHeight +" isCreate " + isCreate);
                holder.photoImageView.setImageBitmap(changeBitmap);
            }
        });
    }

    public class PhotoHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.photo_image_view)
        ScaleImageView photoImageView;

        @BindView(R.id.photo_parent_view)
        RelativeLayout photoParentView;

        @BindView(R.id.photo_card_view)
        CardView photoCardView;


        public PhotoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    private LruCache<String,Bitmap> lruBitmapList;
    private void initLruBitmapList(){
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 4);
        lruBitmapList = new LruCache<String,Bitmap>(cacheSize){

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }


    private void putLruBitmap(String key,Bitmap bitmap){
        lruBitmapList.put(key, bitmap);
    }


    private Bitmap getLruBitmap(String key){
        return lruBitmapList.get(key);
    }


    public void removeLruBitmap(String key){
        lruBitmapList.remove(key);
    }

    public void removeAllLruBitmap(){

    }


}
