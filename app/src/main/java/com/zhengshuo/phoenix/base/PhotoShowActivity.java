package com.zhengshuo.phoenix.base;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.common.Constants;
import com.zhengshuo.phoenix.common.runtimepermissions.PermissionsManager;
import com.zhengshuo.phoenix.common.runtimepermissions.PermissionsResultAction;
import com.zhengshuo.phoenix.util.CommonUtils;
import com.zhengshuo.phoenix.util.FileHelper;
import com.zhengshuo.phoenix.util.ListUtil;
import com.zhengshuo.phoenix.util.LogUtil;
import com.zhengshuo.phoenix.widget.ViewPagerPhoto;
import com.gyf.barlibrary.ImmersionBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import okhttp3.Call;

/**
 * 查看图片的公共页面
 */
public class PhotoShowActivity extends BaseActivity implements PhotoFragment.FragmentCallBack {
    @BindView(R.id.viewpager)
    ViewPagerPhoto viewPager;
    @BindView(R.id.tv_num)
    TextView tvNum;
    private List<String> imageUrlList = new ArrayList<>();
    private int index;


    @Override
    protected int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏
        return R.layout.activity_photo_show;
    }



    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).init();
    }


    @Override
    protected void initLocalData(Intent mIntent) {
        getDataFromIntent();
    }


    private void getDataFromIntent() {
        boolean single = getIntent().getBooleanExtra("single",false);//单张图片
        if (single) {
            String image_url = getIntent().getStringExtra("image_url");
            imageUrlList.add(image_url);
            tvNum.setVisibility(View.GONE);
        }else{
            index = getIntent().getIntExtra("index",0);
            List<String> tempList = (List<String>) getIntent().getSerializableExtra("imgList");
            if(!ListUtil.isEmpty(tempList)){
                imageUrlList.addAll(tempList);
                tvNum.setText((index + 1) + "/" + imageUrlList.size());
            }
        }
        initPagerAdapter();
    }

    private void initPagerAdapter() {
        PhotoPagerAdapter viewPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(), imageUrlList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvNum.setText((position + 1) + "/" + imageUrlList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(index);
    }




    @Override
    public void onFinish() {
        back();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onSaveImage(String imageUrl) {
        checkPermission(imageUrl,System.currentTimeMillis()+".jpg");
    }



    /**
     * 保存视频
     *
     * @param imageUrl
     * @param fileName
     */
    private void checkPermission(final String imageUrl, String fileName) {
        if (PermissionsManager.getInstance().hasAllPermissions(getBaseContext(), Constants.PERMS_WRITE_READ)){
            saveImage(imageUrl, fileName);
        }else {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, Constants.PERMS_WRITE_READ, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    saveImage(imageUrl, fileName);
                }

                @Override
                public void onDenied(String permission) {
                    showToast(R.string.need_sdcard_permission);
                }
            });
        }
    }

    private void saveImage(String imageUrl, String fileName) {
        CommonUtils.showDialogNumal(this, getString(R.string.saving));
        if (TextUtils.isEmpty(imageUrl)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToast(R.string.saving_failed);
                }
            }, 500);
            return;
        }
        final String savePath = getSaveImagePath();
        LogUtil.e("saveImage","saveImage>>savePath>>>"+savePath);
        if (!imageUrl.startsWith("http")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean file = CommonUtils.copyFile(PhotoShowActivity.this, imageUrl, savePath);
                    if (file){
                        showToast(R.string.saving_successful);
                    }else{
                        showToast(R.string.saving_failed);
                    }
                }
            }, 500);
        }else{
            /**
             * 下载附件
             */
            OkHttpUtils.get()
                    .url(imageUrl)
                    .build()
                    .execute(new FileCallBack(savePath, fileName) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            showToast(R.string.saving_failed);
                            LogUtil.e("saveImage", "onError :" + e.getMessage());
                        }

                        @Override
                        public void inProgress(float progress, long total, int id) {
                            super.inProgress(progress, total, id);
                        }

                        @Override
                        public void onResponse(File downloadFile, int id) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    FileHelper.saveImgToAlbum(PhotoShowActivity.this,downloadFile.getAbsolutePath());
                                    showToast(R.string.saving_successful);
                                    LogUtil.e("saveImage", "onResponse :" + downloadFile.getAbsolutePath());
                                }
                            });
                        }
                    });

        }
    }

    @NonNull
    private String getSaveImagePath() {
        String dirFilePath = BaseApplication.getInstance().getImagePath();
        return dirFilePath ;
    }

    /**
     * 吐司
     *
     * @param resId
     */
    private void showToast(final int resId) {
        CommonUtils.cencelDialog();
        CommonUtils.showToastShort(getBaseContext(), resId);
    }

}
