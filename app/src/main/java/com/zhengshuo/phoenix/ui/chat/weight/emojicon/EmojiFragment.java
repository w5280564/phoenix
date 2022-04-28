package com.zhengshuo.phoenix.ui.chat.weight.emojicon;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhengshuo.phoenix.common.GridDividerItemDecoration;
import com.zhengshuo.phoenix.util.DisplayUtil;
import com.google.android.material.tabs.TabLayout;
import com.zhengshuo.phoenix.R;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;


public class EmojiFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tableLayout;
    private List<Emojicon> emojicons;
    private int emojiconColumns = 7;
    private int emojiconRows = 3;
    private OnEmojiListener onEmojiListener;


    public void setOnEmojiListener(OnEmojiListener onEmojiListener) {
        this.onEmojiListener = onEmojiListener;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_emoji, container, false);
        viewPager = root.findViewById(R.id.viewpager_emoji);
        tableLayout = root.findViewById(R.id.tabLayout_dot);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final List<View> views = getViews();
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public Object instantiateItem(ViewGroup arg0, int arg1) {
                arg0.addView(views.get(arg1));
                return views.get(arg1);
            }


            @Override
            public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
                arg0.removeView(views.get(arg1));

            }
        });
        tableLayout.setupWithViewPager(viewPager);
        TabLayout.Tab[] tabs = new TabLayout.Tab[views.size()];

        for (int i = 0; i < views.size(); i++) {
            tabs[i] = tableLayout.getTabAt(i);
            ImageButton imageView = new ImageButton(getContext());
            imageView.setBackground(null);
            imageView.setImageResource(R.drawable.dot_emoji);
            tabs[i].setCustomView(imageView);

        }

    }

    private List<View> getViews() {
        emojicons = getArguments().getParcelableArrayList("emojicons");
        emojiconColumns = getArguments().getInt("emojiconColumns", 7);
        emojiconRows = getArguments().getInt("emojiconRows", 3);
        int itemSize = emojiconColumns * emojiconRows - 1;
        int totalSize = emojicons.size();
        Emojicon.Type emojiType = Emojicon.Type.NORMAL;
        if (totalSize != 0) {
            emojiType = emojicons.get(0).getType();
        }

        if (emojiType == Emojicon.Type.BIG_EXPRESSION) {
            itemSize = emojiconColumns * emojiconRows;
        }
        int pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < pageSize; i++) {
            View view = View.inflate(getContext(), R.layout.emoji_gridview, null);
            RecyclerView mRecyclerView = view.findViewById(R.id.mRecyclerView);
            List<Emojicon> list = new ArrayList<Emojicon>();
            if (i != pageSize - 1) {
                list.addAll(emojicons.subList(i * itemSize, (i + 1) * itemSize));
            } else {
                list.addAll(emojicons.subList(i * itemSize, totalSize));
            }
            if (emojiType != Emojicon.Type.BIG_EXPRESSION) {
                Emojicon deleteIcon = new Emojicon();
                deleteIcon.setEmojiText(SmileUtils.DELETE_KEY);
                list.add(deleteIcon);
            }
            initRecyclerView(mRecyclerView,list,emojiType);
            views.add(view);
        }
        return views;
    }

    public void initRecyclerView(RecyclerView mRecyclerView,List<Emojicon> list,Emojicon.Type emojiType) {
        GridLayoutManager mManager = new GridLayoutManager(getContext(),emojiconColumns);
        mRecyclerView.setLayoutManager(mManager);
        GridDividerItemDecoration mItemDecoration = new GridDividerItemDecoration(false, DisplayUtil.sp2px(getContext(),20), ContextCompat.getColor(getContext(),R.color.transparent));
        mRecyclerView.addItemDecoration(mItemDecoration);
        EmojiconGridAdapter gridAdapter = new EmojiconGridAdapter(list, emojiType);
        mRecyclerView.setAdapter(gridAdapter);
        gridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Emojicon emojicon = gridAdapter.getItem(position);
                if (onEmojiListener != null) {
                    String emojiText = emojicon.getEmojiText();
                    if (emojiText != null && emojiText.equals(SmileUtils.DELETE_KEY)) {
                        onEmojiListener.onDeleteImageClicked();
                    } else {
                        onEmojiListener.onExpressionClicked(emojicon);
                    }

                }
            }
        });
    }



    public interface OnEmojiListener {
        void onDeleteImageClicked();

        void onExpressionClicked(Emojicon emojicon);

    }
}
