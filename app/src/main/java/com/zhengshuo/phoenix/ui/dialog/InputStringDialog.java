package com.zhengshuo.phoenix.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhengshuo.phoenix.R;


/**
 * 输入内容
 */
public class InputStringDialog extends Dialog {
    private Context mContext;
    private String send_rate,send_count;
    private OnConfirmListener mOnConfirmListener;
    private EditText et_rate,et_count;
    private TextView tv_confirm;

    public InputStringDialog(Context context, String send_rate, String send_count,OnConfirmListener mOnConfirmListener) {
        super(context, R.style.CenterDialogStyle);
        this.mContext = context;
        this.send_rate = send_rate;
        this.send_count = send_count;
        this.mOnConfirmListener = mOnConfirmListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_input_string);
        setCanceledOnTouchOutside(true);
        initView();
        initEvent();
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private void initView() {
        LinearLayout llParent = findViewById(R.id.ll_parent);
        int screenWidth = getScreenWidth(mContext);//屏幕的宽度
        int parentWidth = (int) (screenWidth / 6f * 5);//弹出框的宽度
        ViewGroup.LayoutParams layoutParams = llParent.getLayoutParams();
        layoutParams.width = parentWidth;
        llParent.setLayoutParams(layoutParams);

        et_rate = findViewById(R.id.et_rate);
        et_rate.setText(send_rate);
        et_count = findViewById(R.id.et_count);
        et_count.setText(send_count);
        tv_confirm = findViewById(R.id.tv_confirm);


    }

    private void initEvent() {
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String send_rate = et_rate.getText().toString();

                if (TextUtils.isEmpty(send_rate)) {
                    Toast.makeText(mContext, "请设置发送频率", Toast.LENGTH_SHORT).show();
                    return;
                }


                String send_count = et_count.getText().toString();

                if (TextUtils.isEmpty(send_count)) {
                    Toast.makeText(mContext, "请设置发送条数", Toast.LENGTH_SHORT).show();
                    return;
                }

                mOnConfirmListener.onConfirm(send_rate,send_count);
                dismiss();
            }
        });

    }




    public interface OnConfirmListener{
        void onConfirm(String send_rate,String send_count);
    }
}
