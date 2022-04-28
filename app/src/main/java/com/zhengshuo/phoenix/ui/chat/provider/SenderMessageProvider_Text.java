package com.zhengshuo.phoenix.ui.chat.provider;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fastchat.sdk.model.HTMessage;
import com.fastchat.sdk.model.HTMessageTextBody;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.ui.chat.weight.emojicon.SmileUtils;
import com.zhengshuo.phoenix.ui.chat.adapter.ChatAdapter;
import com.zhengshuo.phoenix.util.ImgLoader;
import com.zhengshuo.phoenix.util.LinkifySpannableUtils;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.widget.RoundImageView;
import com.fastchat.sdk.model.ReferenceMessage;

/**
 * 发送文本消息
 */
public class SenderMessageProvider_Text extends BaseMessageProvider_Sender {

    @Override
    public int viewType() {
        return ChatAdapter.MESSAGE_TEXT_SEND;
    }

    @Override
    public int layout() {
        return R.layout.item_chat_sender_text;
    }

    @Override
    public void convert(BaseViewHolder helper, HTMessage item, int position) {
        super.convert(helper, item, position);
        TextView tv_content = helper.getView(R.id.tv_content);
        LinearLayout ll_reference = helper.getView(R.id.ll_reference);
        TextView tv_reference = helper.getView(R.id.tv_reference);
        RelativeLayout rl_reference_image_video = helper.getView(R.id.rl_reference_image_video);
        RoundImageView iv_reference = helper.getView(R.id.iv_reference);
        ImageView iv_play_label = helper.getView(R.id.iv_play_label);


        HTMessageTextBody htMessageBody = (HTMessageTextBody)item.getBody();
        String content = htMessageBody.getContent();
        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(SmileUtils.getSmiledText(mContext, content), TextView.BufferType.SPANNABLE);
            LinkifySpannableUtils.getInstance().setSpan(mContext, tv_content, 99999);
        }


        ReferenceMessage reference = htMessageBody.getReference();
        if (reference!=null) {
            ll_reference.setVisibility(View.VISIBLE);
            String reference_nick = reference.getName();
            if (StringUtil.isBlank(reference.getMessageId())) {
                rl_reference_image_video.setVisibility(View.GONE);
                tv_reference.setText(String.format("%s：%s", reference_nick,"引用内容已删除"));
            }else{
                String reference_content = "";
                int reference_message_type = reference.getMsgType();
                if (reference_message_type==2001) {//文本消息
                    rl_reference_image_video.setVisibility(View.GONE);
                    JSONObject body_jsonObject_big = htMessageBody.getBodyJson();
                    JSONObject reference_jsonObject = body_jsonObject_big.getJSONObject("reference");
                    JSONObject body_jsonObject_little = reference_jsonObject.getJSONObject("body");
                    reference_content = body_jsonObject_little.getString("content");
                    tv_reference.setText(String.format("%s：%s", reference_nick,reference_content));
                }else if(reference_message_type==2002){//图片消息
                    rl_reference_image_video.setVisibility(View.VISIBLE);
                    iv_play_label.setVisibility(View.GONE);
                    JSONObject body_jsonObject_big = htMessageBody.getBodyJson();
                    JSONObject reference_jsonObject = body_jsonObject_big.getJSONObject("reference");
                    JSONObject body_jsonObject_little = reference_jsonObject.getJSONObject("body");
                    String remotePath = body_jsonObject_little.getString("remotePath");
                    showReferenceImageView(remotePath,iv_reference);
                    tv_reference.setText(String.format("%s：", reference_nick));
                }else if(reference_message_type==2004){//视频消息
                    rl_reference_image_video.setVisibility(View.VISIBLE);
                    iv_play_label.setVisibility(View.VISIBLE);
                    JSONObject body_jsonObject_big = htMessageBody.getBodyJson();
                    JSONObject reference_jsonObject = body_jsonObject_big.getJSONObject("reference");
                    JSONObject body_jsonObject_little = reference_jsonObject.getJSONObject("body");
                    String thumbnailUrl = body_jsonObject_little.getString("thumbnailRemotePath");
                    showReferenceImageView(thumbnailUrl,iv_reference);
                    tv_reference.setText(String.format("%s：", reference_nick));
                }

            }

        }else{
            ll_reference.setVisibility(View.GONE);
        }

    }



    private void showReferenceImageView(String remotePath, RoundImageView iv_reference) {
        ImgLoader.getInstance().displayCrop(mContext, iv_reference, remotePath, R.mipmap.error_image_placeholder);
    }


//    int msgType = 2001;
//    HTMessage.Type type = htMessage.getType();
//        if (type == HTMessage.Type.TEXT) {
//
//        msgType = 2001;
//    } else if (type == HTMessage.Type.IMAGE) {
//        msgType = 2002;
//    } else if (type == HTMessage.Type.VOICE) {
//        msgType = 2003;
//    } else if (type == HTMessage.Type.VIDEO) {
//        msgType = 2004;
//    } else if (type == HTMessage.Type.FILE) {
//        msgType = 2005;
//    } else if (type == HTMessage.Type.LOCATION) {
//        msgType = 2006;
//    }

}
