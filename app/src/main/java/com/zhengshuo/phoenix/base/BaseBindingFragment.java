package com.zhengshuo.phoenix.base;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.dylanc.viewbinding.base.ViewBindingUtil;
import com.gyf.barlibrary.SimpleImmersionFragment;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.dialog.LoadingDialog;
import com.zhengshuo.phoenix.ui.video.VideoPlayActivity;

import java.io.Serializable;
import java.util.List;

import butterknife.Unbinder;

/**
 * @Description: Fragment的基类
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public abstract class BaseBindingFragment<VB extends ViewBinding> extends SimpleImmersionFragment {
    protected BaseApplication mApplication;
    protected Context mContext;
    protected BaseBindingActivity mActivity;
    private Unbinder unbinder;
    private LoadingDialog mLoadingDialog;
    private Handler handler = new Handler();

    public VB binding;

    public VB getBinding() {
        return binding;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewBindingUtil.inflateWithGeneric(this, inflater, container, false);
//        View mRootView = inflater.inflate(getLayoutId(), container, false);
//        unbinder = ButterKnife.bind(this, mRootView);
        mApplication = BaseApplication.getInstance();
        mContext = mApplication.getContext();
        mActivity = (BaseBindingActivity) getActivity();
        initLocalData();
        initBinding(binding);
        //view与数据绑定
        initView(binding.getRoot());
        //设置监听
        initEvent();
        InitViewModel();
        return binding.getRoot();
    }

    protected  void  initBinding(VB binding){

    }


    /**
     * 绑定布局文件
     */
//    protected abstract int getLayoutId();


    /**
     * 初始化本地数据
     */
    protected void initLocalData() {

    }


    /**
     * 初始化view
     */
    protected void initView(View mRootView) {

    }


    /**
     * 设置监听
     */
    protected void initEvent() {

    }

    /**
     * 设置ViewModel
     */
    protected void InitViewModel() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 建议在`自定义页面`的页面结束函数中调用
        unbinder.unbind();
        binding = null;
    }


    /**
     * 功能描述:简单地Activity的跳转(不携带任何数据)
     */
    protected void skipAnotherActivity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(mActivity, targetActivity);
        startActivity(intent);
    }


    /**
     * 跳转到查看图片页
     *
     * @param position
     * @param mImageList
     */
    public void jumpToPhotoShowActivity(int position, List<String> mImageList) {
        Intent intent = new Intent(mActivity, PhotoShowActivity.class);
        intent.putExtra("index", position);
        intent.putExtra("imgList", (Serializable) mImageList);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    /**
     * 跳转到查看图片页(单张图片)
     *
     * @param image_url
     */
    protected void jumpToPhotoShowActivitySingle(String image_url) {
        Intent intent = new Intent(mActivity, PhotoShowActivity.class);
        intent.putExtra("image_url", image_url);
        intent.putExtra("single", true);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    /**
     * 跳转到播放视频页
     */
    protected void jumpToPlayViewActivity(String localPath, String videoName) {
        Intent intent = new Intent(mActivity, VideoPlayActivity.class);
        intent.putExtra(VideoPlayActivity.VIDEO_NAME, videoName);
        intent.putExtra(VideoPlayActivity.VIDEO_PATH, localPath);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public void initImmersionBar() {

    }


    private long dialogCreateTime;

    /**
     * 显示加载 dialog
     *
     * @param msg
     */
    public void showLoadingDialog(String msg) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) return;
        if (mLoadingDialog == null || (mLoadingDialog != null && !mLoadingDialog.isShowing())) {
            dialogCreateTime = System.currentTimeMillis();
            mLoadingDialog = new LoadingDialog(mActivity, msg);
            mLoadingDialog.show();
        }
    }

    /**
     * 显示加载 dialog
     *
     * @param msgResId
     */
    public void showLoadingDialog(int msgResId) {
        showLoadingDialog(getString(msgResId));
    }

    /**
     * 取消加载dialog
     */
    public void dismissLoadingDialog() {
        dismissLoadingDialog(null);
    }

    /**
     * 取消加载dialog. 因为延迟， 所以要延时完成之后， 再在 runnable 中执行逻辑.
     *
     * <p>延迟关闭时间是因为接口有时返回太快。
     */
    public void dismissLoadingDialog(Runnable runnable) {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            // 由于可能请求接口太快，则导致加载页面一闪问题， 所有再次做判断，
            // 如果时间太快（小于 500ms）， 则会延时 1s，再做关闭。
            if (System.currentTimeMillis() - dialogCreateTime < 500) {
                handler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (runnable != null) {
                                    runnable.run();
                                }
                                if (mLoadingDialog != null) {
                                    mLoadingDialog.dismiss();
                                    mLoadingDialog = null;
                                }
                            }
                        },
                        1000);

            } else {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }


    protected View addEmptyView(int textResource, int imgResource) {
        View emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_view_layout, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView iv_noData = emptyView.findViewById(R.id.iv_noData);

        if (textResource != 0) {
            TextView tv_noData = emptyView.findViewById(R.id.tv_noData);
            tv_noData.setText(getString(textResource));
        }

        if (imgResource != 0) {
            iv_noData.setImageResource(imgResource);
        } else {
            iv_noData.setImageResource(R.mipmap.error_image_placeholder);
        }

        return emptyView;
    }

}

