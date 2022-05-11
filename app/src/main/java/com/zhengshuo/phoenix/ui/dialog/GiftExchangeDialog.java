package com.zhengshuo.phoenix.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengshuo.phoenix.R;


/**
 * 礼物兑换弹出框
 */
public class GiftExchangeDialog extends Dialog implements View.OnClickListener {
	private DialogConfirmListener listener;
	private String content,title, button_text;
	private Context mContext;
	private TextView sure_Tv;

	public GiftExchangeDialog(Context context, String title, String content, String button_text, DialogConfirmListener confirmListener) {
		super(context, R.style.CenterDialogStyle);
		this.listener = confirmListener;
		this.title = title;
		this.content = content;
		this.button_text = button_text;
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.giftexchange_dialog);
		getWindow().setDimAmount(0.7f);  //0-1 1是黑色
		setCanceledOnTouchOutside(true);
		initView();
		initEvent();
	}

	private void initView() {
		sure_Tv = findViewById(R.id.sure_Tv);
//		TextView content_Tv = findViewById(R.id.content);
//		ImageView zanImg = findViewById(R.id.zanImg);
		sure_Tv.setText(button_text);
//		content_Tv.setText(content);
	}
	
	
	private void initEvent() {
		sure_Tv.setOnClickListener(this);
	}

	/**
	 * 回调接口对象
	 */
	public interface DialogConfirmListener {
		void onConfirmClick();
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.sure_Tv) {
			listener.onConfirmClick();
			dismiss();
		}
	}

	@Override
	public void onBackPressed() {

	}


}
