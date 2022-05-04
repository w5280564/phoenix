package com.zhengshuo.phoenix.ui.start;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseBindingActivity;
import com.zhengshuo.phoenix.databinding.ActivityGuideBinding;

/**
 * 引导页
 */
public class GuideActivity extends BaseBindingActivity<ActivityGuideBinding> {
    /**
     * 存放图片数组
     */
    private int mPageList[] = {R.mipmap.guide_one, R.mipmap.guide_two, R.mipmap.guide_three};

    @Override
    protected void initView() {
        getBinding().mViewPager.setAdapter(new GuideViewPagerAdapter(mPageList));
    }


    /*我们需要使用适配器来帮助我们更方便的使用ViewPager*/
    public class GuideViewPagerAdapter extends PagerAdapter {
        private int[] mPageList;

        public GuideViewPagerAdapter(int[] mPageList) {
            super();
            this.mPageList = mPageList;
        }

        @Override
        public int getCount() {
            if (mPageList != null) {
                return mPageList.length;
            }
            return 0;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View inflate = LayoutInflater.from(mActivity).inflate(R.layout.item_guidepage, null);
            ImageView image = inflate.findViewById(R.id.image);
//            Glide.with(mActivity).load(mPageList[position]).into(image);
            image.setBackgroundResource(mPageList[position]);
            container.addView(inflate);
            return inflate;
        }
    }


}