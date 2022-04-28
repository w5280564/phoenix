package com.zhengshuo.phoenix.ui.chat.weight.emojicon;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhengshuo.phoenix.R;

import java.util.List;



public class EmojiconGridAdapter extends BaseQuickAdapter<Emojicon, BaseViewHolder>{

    private Emojicon.Type emojiconType;




    public EmojiconGridAdapter(List<Emojicon> data, Emojicon.Type emojiconType) {
        super(R.layout.row_expression, data);
        this.emojiconType = emojiconType;
    }



    @Override
    protected void convert(@NonNull BaseViewHolder helper, Emojicon emojicon) {
        ImageView iv_emoji = helper.getView(R.id.iv_expression);
        TextView tv_emoji = helper.getView(R.id.tv_expression);


        if(SmileUtils.DELETE_KEY.equals(emojicon.getEmojiText())){
            iv_emoji.setVisibility(View.VISIBLE);
            tv_emoji.setVisibility(View.GONE);
            iv_emoji.setImageResource(R.mipmap.delete_expression);
        }else{
            iv_emoji.setVisibility(View.GONE);
            tv_emoji.setVisibility(View.VISIBLE);
            tv_emoji.setText(SmileUtils.getSmiledText(mContext, emojicon.getEmojiText()), TextView.BufferType.SPANNABLE);
        }

    }
}
