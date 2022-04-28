
package com.zhengshuo.phoenix.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhengshuo.phoenix.R;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageVoiceBody;

public class FastVoiceUtils {
	

	/**
	 * @param htMessage
	 * @return
	 */
	public static void showVoiceView(ImageView showVoiceView,TextView tv_length, HTMessage htMessage) {
		HTMessageVoiceBody htMessageVoiceBody = (HTMessageVoiceBody) htMessage.getBody();
		int len = htMessageVoiceBody.getAudioDuration();
		if (len > 0) {
			tv_length.setText(len + "\"");
			tv_length.setVisibility(View.VISIBLE);
		} else {
			tv_length.setVisibility(View.INVISIBLE);
		}
		if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
			showVoiceView.setImageResource(R.mipmap.yuyin3_receiver);
		} else {
			showVoiceView.setImageResource(R.mipmap.yuyin3_send);
		}
	}


}
