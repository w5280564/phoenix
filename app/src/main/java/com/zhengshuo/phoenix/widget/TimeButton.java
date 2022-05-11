package com.zhengshuo.phoenix.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.zhengshuo.phoenix.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimeButton extends AppCompatButton implements View.OnClickListener {
    private long lenght = 5 * 1000;// 倒计时长度,这里给了默认60秒
    private String textafter = "重新获取~";
    private String textbefore = "点击获取验证码~";
    private int colorAfter = R.color.white;
    private int colorBefore = R.drawable.gradualbtn_shape_grey;
    private final String TIME = "time";
    private final String CTIME = "ctime";
    private OnClickListener mOnclickListener;
    private Timer t;
    private TimerTask tt;
    private long time;


    Map<String, Long> map = new HashMap<>();

    public TimeButton(Context context) {
        super(context);
        setOnClickListener(this);
        startTime();
    }

    public TimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        startTime();
    }

    @SuppressLint("HandlerLeak")
    Handler han = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String updateTime = String.format(textafter + "（%1$ss）", time / 1000);
            TimeButton.this.setText(updateTime);
            TimeButton.this.setBackgroundResource(colorBefore);
            time -= 1000;
            if (time < 0) {
                TimeButton.this.setEnabled(true);
                TimeButton.this.setText(textbefore);
                TimeButton.this.setBackgroundResource(colorAfter);
                clearTimer();
            }
        }
    };

    private void initTimer() {
        time = lenght;
        t = new Timer();
        tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("time", time / 1000 + "");
                han.sendEmptyMessage(0x01);//十六进制的数字1
            }
        };
    }

    private void clearTimer() {
        if (tt != null) {
            tt.cancel();
            tt = null;
        }
        if (t != null)
            t.cancel();
        t = null;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof TimeButton) {
            super.setOnClickListener(l);
        } else
            this.mOnclickListener = l;
    }

    @Override
    public void onClick(View v) {
        if (mOnclickListener != null)
            mOnclickListener.onClick(v);
//        initTimer();
//        String updateTime = String.format(textafter + "(%1$ss)", time / 1000);
//        this.setText(updateTime);
//        this.setEnabled(false);
//        t.schedule(tt, 0, 1000);

        // t.scheduleAtFixedRate(task, delay, period);
    }

    /**
     * 开始倒计时
     */
    private void startTime() {
        initTimer();
        String updateTime = String.format(textafter + "（%1$ss）", time / 1000);
        this.setText(updateTime);
        this.setBackgroundResource(colorBefore);
        this.setEnabled(false);
        t.schedule(tt, 0, 1000);
    }

    /**
     * 和activity的onDestroy()方法同步
     */
    public void onDestroy() {
        if (map == null)
            map = new HashMap<String, Long>();
        map.put(TIME, time);
        map.put(CTIME, System.currentTimeMillis());
        clearTimer();
        Log.e("time", "onDestroy");
    }

    /**
     * 和activity的onCreate()方法同步
     */
    public void onCreate(Bundle bundle) {
        Log.e("time:倒计时相关", map + "");
        if (map == null)
            return;
        if (map.size() <= 0)// 这里表示没有上次未完成的计时
            return;
        long time = System.currentTimeMillis() - map.get(CTIME) - map.get(TIME);
        map.clear();
        if (time > 0)
            return;
//        else
//        {
//            initTimer();
//            this.time = Math.abs(time);
//            t.schedule(tt, 0, 1000);
//            this.setText(time + textafter);
//            this.setEnabled(false);
//        }
    }

    /**
     * 设置计时时候显示的文本
     */
    public TimeButton setTextAfter(String text1) {
        this.textafter = text1;
        return this;
    }

    /**
     * 设置点击之前的文本
     */
    public TimeButton setTextBefore(String text0) {
        this.textbefore = text0;
        this.setText(textbefore);
        return this;
    }

    /**
     * 设置到计时长度
     *
     * @param lenght 时间 默认毫秒
     * @return
     */
    public TimeButton setLenght(long lenght) {
        this.lenght = lenght;
        time = lenght;
        return this;
    }

//    public TimeButton setBeforeColor(int color) {
//        this.colorBefore = color;
////        this.setBackgroundResource(color);
//        return this;
//    }

//    public TimeButton setAfterColor(int color) {
//        this.colorAfter = color;
////        this.setBackgroundResource(color);
//        return this;
//    }

    public void setColorBefore(int colorBefore) {
        this.colorBefore = colorBefore;
    }
    public void setColorAfter(int colorAfter) {
        this.colorAfter = colorAfter;
    }

}
