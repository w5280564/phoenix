package com.zhengshuo.phoenix.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.dialog.HTAlertDialog;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片查看
 */

public class PhotoFragment extends BaseFragment implements PhotoViewAttacher.OnViewTapListener, View.OnLongClickListener {
    PhotoView mPhotoView;
    private String img_url;

    //监听回调
    FragmentCallBack mFragmentCallBack;

    /**
     * 获取这个fragment需要展示图片的url
     * @param url
     * @return
     */
    public static PhotoFragment newInstance(String url) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initLocalData() {
        getDataFromArguments();
    }

    @Override
    protected void initView(View mRootView) {
        mPhotoView = mRootView.findViewById(R.id.photoview);
        mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mPhotoView.setOnViewTapListener(this);
        mPhotoView.setOnLongClickListener(this);
        Glide.with(mContext)
                .load(img_url)
                .apply(new RequestOptions()
                        .error(R.mipmap.error_image_placeholder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mPhotoView);
    }

    private void getDataFromArguments() {
        Bundle b = getArguments();
        if (b!=null) {
            img_url = getArguments().getString("url");
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_img;
    }


    @Override
    public void onViewTap(View view, float x, float y) {
        //消息回调到 Activity
        if (mFragmentCallBack!=null) {
            mFragmentCallBack.onFinish();
        }
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ///获取绑定的监听
        if (context instanceof FragmentCallBack) {
            mFragmentCallBack = (FragmentCallBack) context;
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentCallBack = null;
    }

    @Override
    public boolean onLongClick(View v) {
        showDialog();
        return true;
    }


    public  interface FragmentCallBack {
        void onFinish();
        void onSaveImage(String imageUrl);
    }


    private void showDialog() {
        HTAlertDialog dialog = new HTAlertDialog(mActivity, null, new String[]{getString(R.string.save)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0://保存的判断
                        if (mFragmentCallBack!=null) {
                            mFragmentCallBack.onSaveImage(img_url);
                        }
                        break;
                }
            }
        });
    }


}
