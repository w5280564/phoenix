package com.zhengshuo.phoenix.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseApplication;


/**
 * Toast封装类
 *
 * @author ouyang
 */
public class ToastUtil {

    private static Context mContext;
    private static Toast toast;


    /**
     * 禁止实例化
     */
    private ToastUtil() {
    }

    /**
     * 显示短Toast
     *
     * @param text
     */
    public static void ss(int text) {
        s(text, Toast.LENGTH_SHORT);
    }

    /**
     * 取消吐司
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }

    /**
     * 显示短Toast
     *
     * @param text
     */
    public static void ss(CharSequence text) {
        s(text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短Toast
     *
     * @param context
     * @param text
     */
    public static void ss(Context context, int text) {
        s(context, text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短Toast
     *
     * @param context
     * @param text
     */
    public static void ss(Context context, CharSequence text) {
        s(context, text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示长Toast
     *
     * @param text
     */
    public static void sl(int text) {
        s(text, Toast.LENGTH_LONG);
    }

    /**
     * 显示长Toast
     *
     * @param text
     */
    public static void sl(CharSequence text) {
        s(text, Toast.LENGTH_LONG);
    }

    /**
     * 显示长Toast
     *
     * @param context
     * @param text
     */
    public static void sl(Context context, int text) {
        s(context, text, Toast.LENGTH_LONG);
    }

    /**
     * 显示长Toast
     *
     * @param context
     * @param text
     */
    public static void sl(Context context, CharSequence text) {
        s(context, text, Toast.LENGTH_LONG);
    }

    /**
     * 显示自定义时长Toast
     *
     * @param text
     * @param duration
     */
    public static void s(int text, int duration) {
        s(getContext(), text, duration);
    }

    /**
     * 显示自定义时长Toast
     *
     * @param text
     * @param duration
     */
    public static void s(CharSequence text, int duration) {
        s(getContext(), text, duration);
    }

    /**
     * 显示自定义时长Toast
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void s(Context context, int text, int duration) {
        s(context, getText(text), duration);
    }

    /**
     * 显示自定义时长Toast
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void s(Context context, CharSequence text, int duration) {
        mContext = context;
        makeToast(context, text, duration).show();
    }

    private static Context getContext() {
        if (mContext == null)
            mContext = BaseApplication.getInstance().getContext();
        return mContext;
    }

    private static String getText(int text) {
        return getContext().getResources().getString(text);
    }

    public static Toast makeToast(Context context, CharSequence text,
                                  int duration) {
        View mToastView = getToastView(context, text);
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(mToastView);
        toast.setDuration(duration);
        return toast;
    }

    /**
     * Toast View 的 LayoutParams
     */
    private static final ViewGroup.LayoutParams M_LAYOUT_PARAMS = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    /**
     * Toast View 的 TextColor
     */
    private static final int TEXT_COLOR = Color.rgb(250, 250, 250);

    /**
     * Toast View 的 TextSize;
     */
    private static final int TEXT_SIZE = 15;

    @SuppressWarnings("deprecation")
    private static View getToastView(Context context, CharSequence text) {
        int padding = dip2px(context, 10);
        TextView mTextView = new TextView(context);
        mTextView.setLayoutParams(M_LAYOUT_PARAMS);
        mTextView.setPadding(padding, padding, padding, padding);
        mTextView.setBackgroundDrawable(getViewGradientDrawable(padding / 2));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        mTextView.setTextColor(TEXT_COLOR);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setText(text);
        return mTextView;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private static GradientDrawable mGradientDrawable;

    private static GradientDrawable getViewGradientDrawable(int radius) {
        if (mGradientDrawable == null) {
            mGradientDrawable = new GradientDrawable();
            mGradientDrawable.setCornerRadius(radius);
            mGradientDrawable.setColor(Color.argb(160, 0, 0, 0));
        }
        return mGradientDrawable;
    }


    public static void showToastView(Context context) {
        Toast toast = new Toast(context.getApplicationContext());
        View view = LayoutInflater.from(context).inflate(R.layout.toast_view_layout, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

}
