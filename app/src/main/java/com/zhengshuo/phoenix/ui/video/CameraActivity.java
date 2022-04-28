package com.zhengshuo.phoenix.ui.video;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseApplication;
import com.zhengshuo.phoenix.util.FileHelper;
import com.zhengshuo.phoenix.util.LogUtil;

/**
 * @Description: 录制视频和拍照
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class CameraActivity extends AppCompatActivity {
    public static final int RESULT_CODE_RETURN_PHOTO = 101;
    public static final int RESULT_CODE_RETURN_VIDEO = 102;
    public static final int RESULT_CODE_PERMISSION_REJECT = 103;
    private JCameraView jCameraView;
    private boolean onlyPhotograph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        //根据需求是否添加以下代码
        initViewAndData();
        setSaveVideoPath();
        initCameraConfig();
        initEvent();
    }

    private void initViewAndData() {
        Intent intent = getIntent();
        if (intent != null){
            onlyPhotograph = intent.getBooleanExtra("onlyPhotograph", false);
        }
        jCameraView = findViewById(R.id.jcameraview);
    }

    private void initCameraConfig() {
        //是否只允许拍照
        if (onlyPhotograph){
            jCameraView.setTip("轻触拍照");
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);//只给拍照
        }else {
            jCameraView.setTip("轻触拍照，长按摄像");
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);//同时拍照和摄像
        }
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);//设置视频质量
    }

    private void initEvent() {
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听，失败时回调
                LogUtil.i( "JCameraView","camera error");
                Intent intent = new Intent();
                setResult(RESULT_CODE_PERMISSION_REJECT, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(CameraActivity.this, "请检查是否开启录音权限", Toast.LENGTH_SHORT).show();
            }
        });


        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取拍照图片bitmap
                String path = FileHelper.saveBitmap(CameraActivity.this,"FTCamera", bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FileHelper.saveImgToAlbum(CameraActivity.this,path);
                    }
                });
                LogUtil.i("JCameraView", "path = " + path);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(RESULT_CODE_RETURN_PHOTO, intent);
                finish();
            }

            @Override
            public void recordSuccess(String videoPath, Bitmap firstFrame) {//视频路径，首帧图
                //获取视频首帧图并转成路径
                String first_picture_path = FileHelper.saveBitmap(CameraActivity.this,"FTCamera", firstFrame);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FileHelper.saveVideoToAlbum(CameraActivity.this,videoPath);
                    }
                });
                LogUtil.i("JCameraView", "url = " + videoPath + ", Bitmap = " + videoPath);
                Intent intent = new Intent();
                intent.putExtra("path", first_picture_path);
                intent.putExtra("videoPath", videoPath);
                setResult(RESULT_CODE_RETURN_VIDEO, intent);
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraActivity.this.finish();
            }
        });
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(CameraActivity.this,"Right",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设置视频保存路径
     */
    private void setSaveVideoPath() {
        String saveVideoPath = BaseApplication.getInstance().getVideoPath();
        //设置视频保存路径
        jCameraView.setSaveVideoPath(saveVideoPath);
    }



    //JCameraView生命周期
    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }
}
