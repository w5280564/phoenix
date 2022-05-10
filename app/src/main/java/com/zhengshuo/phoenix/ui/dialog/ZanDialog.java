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
 * 1个按钮的提醒提示弹出框
 */
public class ZanDialog extends Dialog implements
		View.OnClickListener {

	private DialogConfirmListener listener;
	private String content,title, button_text;
	private Context mContext;
	private TextView tvBtn;

	public ZanDialog(Context context, String title, String content, String button_text, DialogConfirmListener confirmListener) {
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
		setContentView(R.layout.zan_dialog);
		getWindow().setDimAmount(0.26f);  //0-1 1是黑色
		setCanceledOnTouchOutside(true);
		initView();
		initEvent();
		
	}


	private void initView() {
		tvBtn = findViewById(R.id.tvBtn);
		TextView content_Tv = findViewById(R.id.content);
		ImageView zanImg = findViewById(R.id.zanImg);
		zanImg.setAnimation(shakeAnimation(3));
		tvBtn.setText(button_text);
		content_Tv.setText(content);
	}
	
	
	private void initEvent() {
		tvBtn.setOnClickListener(this);
	}

	/**
	 * 回调接口对象
	 */

	public interface DialogConfirmListener {

		void onConfirmClick();
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.tvBtn) {
			listener.onConfirmClick();
			dismiss();
		}
	}


	@Override
	public void onBackPressed() {

	}
	/**
	 * 晃动动画
	 * <p>
	 * 那么CycleInterpolator是干嘛用的？？
	 * Api demo里有它的用法，是个摇头效果！
	 *
	 * @param counts 1秒钟晃动多少下
	 * @return Animation
	 */
	public static Animation shakeAnimation(int counts) {
		Animation rotateAnimation = new RotateAnimation(0, 20, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setInterpolator(new CycleInterpolator(counts));
		rotateAnimation.setRepeatCount(-1);
		rotateAnimation.setDuration(3000);
		return rotateAnimation;
	}



}
