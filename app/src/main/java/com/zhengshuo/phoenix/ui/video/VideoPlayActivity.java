package com.zhengshuo.phoenix.ui.video;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseApplication;
import com.zhengshuo.phoenix.common.Constants;
import com.zhengshuo.phoenix.common.runtimepermissions.PermissionsManager;
import com.zhengshuo.phoenix.common.runtimepermissions.PermissionsResultAction;
import com.zhengshuo.phoenix.ui.dialog.HTAlertDialog;
import com.zhengshuo.phoenix.util.CommonUtils;
import com.zhengshuo.phoenix.util.FileHelper;
import com.zhengshuo.phoenix.util.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import java.io.File;
import cn.jzvd.JzvdStd;
import okhttp3.Call;


/**
 * @Description: 视频播放界面
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class VideoPlayActivity extends AppCompatActivity {
    public static final String VIDEO_NAME = "videoName";
    public static final String VIDEO_PATH = "videoPath";
    private String videoName, videoUrl;
    private JzvdStd myVideoView;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉头部title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_play);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {
        videoName = getIntent().getStringExtra(VIDEO_NAME);
        videoUrl = getIntent().getStringExtra(VIDEO_PATH);
    }

    private void setListener() {
        myVideoView.backButton.setVisibility(View.GONE);
        myVideoView.thumbImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(v);
                return true;
            }
        });
        myVideoView.findViewById(R.id.surface_container).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(v);
                return true;
            }
        });
    }

    private void initData() {
        myVideoView.setUp(videoUrl, "", JzvdStd.SCREEN_FULLSCREEN);
        myVideoView.startButton.performClick();
        myVideoView.startVideo();
        myVideoView.backButton.setVisibility(View.GONE);
        myVideoView.tinyBackImageView.setVisibility(View.GONE);
        myVideoView.batteryLevel.setVisibility(View.GONE);
        myVideoView.batteryTimeLayout.setVisibility(View.GONE);
        myVideoView.totalTimeTextView.setVisibility(View.VISIBLE);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        myVideoView = findViewById(R.id.vv_paly);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JzvdStd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.closeButton:
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showDialog(final View view) {
        HTAlertDialog dialog = new HTAlertDialog(VideoPlayActivity.this, null, new String[]{getString(R.string.save)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0://保存的判断
                        checkPermission(videoUrl,videoName);
                        break;
                }
            }
        });
    }

    @NonNull
    private String getSaveVideoPath() {
        String dirFilePath = BaseApplication.getInstance().getVideoPath();
        return dirFilePath ;
    }

    /**
     * 保存视频
     *
     * @param videoUrl
     * @param fileName
     */
    private void checkPermission(final String videoUrl, String fileName) {
        if (PermissionsManager.getInstance().hasAllPermissions(getBaseContext(), Constants.PERMS_WRITE_READ)){
            saveVideo(videoUrl, fileName);
        }else {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, Constants.PERMS_WRITE_READ, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    saveVideo(videoUrl, fileName);
                }

                @Override
                public void onDenied(String permission) {
                    showToast(R.string.need_sdcard_permission);
                }
            });
        }
    }

    private void saveVideo(String videoUrl, String fileName) {
        CommonUtils.showDialogNumal(VideoPlayActivity.this, getString(R.string.saving));
        if (TextUtils.isEmpty(videoUrl)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToast(R.string.saving_failed);
                }
            }, 500);
            return;
        }

        final String savePath = getSaveVideoPath();
        LogUtil.e("saveVideo","saveVideo>>savePath>>>"+savePath);
        if (!videoUrl.startsWith("http")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean file = CommonUtils.copyFile(VideoPlayActivity.this, videoUrl, savePath);
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
                    .url(videoUrl)
                    .build()
                    .execute(new FileCallBack(savePath, fileName) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            showToast(R.string.saving_failed);
                            LogUtil.e("saveVideo", "onError :" + e.getMessage());
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
                                    FileHelper.saveVideoToAlbum(VideoPlayActivity.this,downloadFile.getAbsolutePath());
                                }
                            });
                            showToast(R.string.saving_successful);
                            LogUtil.e("saveVideo", "onResponse :" + downloadFile.getAbsolutePath());
                        }
                    });

        }
    }



    /**
     * 吐司
     *
     * @param resId
     */
    private void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(getBaseContext(), resId);
            }
        });
    }
}
