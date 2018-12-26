package com.liqq.photolistdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.liqq.photolistdemo.adapter.PhotoAdapter;
import com.liqq.photolistdemo.bean.LruPhoto;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "PhotoList";
    private Context mContext;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PhotoAdapter mPhotoAdapter;
    private Unbinder mUnBinder;

    //http://api.douban.com/v2/movie/top250
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnBinder = ButterKnife.bind(this);
        mContext = MainActivity.this;
        initRecyclerView();
        initPhotoList();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }



    private void initRecyclerView(){
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mPhotoAdapter = new PhotoAdapter(mContext);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mPhotoAdapter);
        mRecyclerView.addItemDecoration(new StaggeredDividerItemDecoration(mContext,5));
    }

    List<LruPhoto> photoList;
    private void initPhotoList(){
        Observable.create(new ObservableOnSubscribe<List<LruPhoto>>() {
            @Override
            public void subscribe(ObservableEmitter<List<LruPhoto>> emitter) throws Exception {
                try {
                    List<LruPhoto> photoList = new ArrayList<>();
                    TypedArray ar = mContext.getResources().obtainTypedArray(R.array.photo_image);
                    int len = ar.length();
                    for (int i = 0; i < len; i++){
                        Integer imageResources = ar.getResourceId(i, 0);
                        LruPhoto lruPhoto = new LruPhoto();
                        lruPhoto.setCreate(false);
                        lruPhoto.setImageResources(imageResources);
                        photoList.add(lruPhoto);
                    }



                    ar.recycle();
                    emitter.onNext(photoList);
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onError(new Throwable("init photoList is error!"));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LruPhoto>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<LruPhoto> result) {
                photoList = result;
                mPhotoAdapter.setPhotoList(photoList);
                mPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }


    /**
     * 自定义分割线
     *
     */
    public class MyItemDection extends RecyclerView.ItemDecoration {
        int space;

        public MyItemDection(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = space;
            outRect.bottom = space;
            outRect.left = space;
            outRect.right = space;
        }
    }
}
