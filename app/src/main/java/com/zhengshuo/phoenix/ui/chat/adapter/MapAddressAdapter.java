package com.zhengshuo.phoenix.ui.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.zhengshuo.phoenix.R;

import lombok.SneakyThrows;

/**
 * Created by Admin on
 */
public class MapAddressAdapter extends RecyclerView.Adapter<MapAddressAdapter.MyViewHolder> {
    PoiResult otherList;
    Context context;
    private int myposition;

    public MapAddressAdapter(Context context, PoiResult poiResult) {
        this.otherList = poiResult;
        this.context = context;
    }

    public void addMoreData(PoiResult otherList) {
//        this.otherList = otherList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myview = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.map_adress_adapter, null));
        return myview;
    }

    @SneakyThrows
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });

        }

        holder.iv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClickLitener.onClick(v,position);
            }
        });


        if (otherList.getPois().isEmpty()) {
            return;
        }
        PoiItem oneData = otherList.getPois().get(position);
        holder.adress_Txt.setText(oneData.toString());

        String content = oneData.getProvinceName() + oneData.getCityName() + oneData.getAdName() + oneData.getSnippet();
        holder.content_txt.setText(content);

    }

    @Override
    public int getItemCount() {
//        return 10;
        return (otherList == null) ? 0 : otherList.getPois().size();//数据加一项
    }

    @Override
    public int getItemViewType(int position) {
        return myposition = position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_check;
        private TextView adress_Txt, content_txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            adress_Txt = itemView.findViewById(R.id.adress_Txt);
            content_txt = itemView.findViewById(R.id.content_txt);
            iv_check = itemView.findViewById(R.id.iv_check);

        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener listener) {
        mOnItemClickLitener = listener;
    }

    public interface OnSendClickLitener {
        void onClick(View v, int pos);
    }

    private OnSendClickLitener onSendClickLitener;

    public void setOnSendClickLitener(OnSendClickLitener listener) {
        onSendClickLitener = listener;

    }


}
