package com.zhengshuo.phoenix.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengshuo.phoenix.R;

import java.util.List;

/**
 * Created by ouyang on 2016/7/3.\
 * QQ:84543217
 */
public class HTAlertDialog {
    private Context context;
    private String title;
    private String[] items;

    public HTAlertDialog(Context context, String title, String[] items) {
        this.context = context;
        this.title = title;
        this.items = items;
    }
    public void init(final OnItemClickListner onItemClickListner) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_alert);
        if (title != null) {
            window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);
            TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
            tv_title.setText(title);

        }
        //默认是2个item--最多支持5个，如需增加，请修改布局文件
        LinearLayout[] linearLayouts=new LinearLayout[]{
                (LinearLayout) window.findViewById(R.id.ll_content1),
                (LinearLayout) window.findViewById(R.id.ll_content2),
                (LinearLayout) window.findViewById(R.id.ll_content3),
                (LinearLayout) window.findViewById(R.id.ll_content4),
                (LinearLayout) window.findViewById(R.id.ll_content5)
        };
        TextView[] textViews=new TextView[]{
                (TextView) window.findViewById(R.id.tv_content1),
                (TextView) window.findViewById(R.id.tv_content2),
                (TextView) window.findViewById(R.id.tv_content3),
                (TextView) window.findViewById(R.id.tv_content4),
                (TextView) window.findViewById(R.id.tv_content5),
        };
        for(int i=0;i<items.length;i++){
            linearLayouts[i].setVisibility(View.VISIBLE);
            textViews[i].setText(items[i]);
            final int finalI = i;
            linearLayouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListner.onClick(finalI);
                    dlg.cancel();
                }
            });
        }
    }



    public  interface  OnItemClickListner{
        void onClick(int position);
    }

    public  interface  OnItem2ClickListner{
        void onClick(String string);
    }


    private List<String> list;

    public HTAlertDialog(Context context, String title, List<String> list) {
        this.context = context;
        this.title = title;
        this.list = list;
    }

    public void setOnItemClickListner(final OnItem2ClickListner onItemClickListner){
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_alert);
        if (title != null) {
            window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);
            TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
            tv_title.setText(title);

        }
        //默认是2个item--最多支持5个，如需增加，请修改布局文件
        LinearLayout[] linearLayouts=new LinearLayout[]{
                (LinearLayout) window.findViewById(R.id.ll_content1),
                (LinearLayout) window.findViewById(R.id.ll_content2),
                (LinearLayout) window.findViewById(R.id.ll_content3),
                (LinearLayout) window.findViewById(R.id.ll_content4),
                (LinearLayout) window.findViewById(R.id.ll_content5)
        };
        TextView[] textViews=new TextView[]{
                (TextView) window.findViewById(R.id.tv_content1),
                (TextView) window.findViewById(R.id.tv_content2),
                (TextView) window.findViewById(R.id.tv_content3),
                (TextView) window.findViewById(R.id.tv_content4),
                (TextView) window.findViewById(R.id.tv_content5),
        };
        for(int i=0;i<list.size();i++){
            linearLayouts[i].setVisibility(View.VISIBLE);
            textViews[i].setText(list.get(i));
             final int finalI1 = i;
            linearLayouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListner.onClick(list.get(finalI1));
                    dlg.cancel();
                }
            });
        }
    }





}
