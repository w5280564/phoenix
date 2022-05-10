package com.zhengshuo.phoenix.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dylanc.viewbinding.base.ViewBindingUtil;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.model.BaseSplitIndexBean;
import com.zhengshuo.phoenix.util.ListUtil;
import com.zhengshuo.phoenix.util.StringUtil;
import com.zhengshuo.phoenix.widget.CustomLoadMoreView;

/**
 * 带分页的基类Activity
 */
public abstract class BaseRecyclerSplitBindingActivity<VB extends ViewBinding> extends BaseBindingActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter mAdapter;
    protected int page = 1;
    protected int limit = 12;


//    @Override
//    protected int getLayoutId() {
//        return 0;
//    }

    public VB binding;

    public VB getBinding() {
        return binding;
    }


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mActivity = this;
        binding = ViewBindingUtil.inflateWithGeneric(this, getLayoutInflater());
        setContentView(binding.getRoot());
        initLocalData(getIntent()); //初始化本地数据
        initView(); //view与数据绑定
        initView(arg0);
        initViewModel(); //初始化ViewModel
        initEvent();//设置监听
        //请求服务端接口数据
        setStatusBar();
    }

    protected void initSwipeRefreshLayoutAndAdapter(String emptyToastText, int emptyViewImgResource, boolean isHaveRefresh) {
        mAdapter.setEmptyView(addEmptyView(emptyToastText, emptyViewImgResource));
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        mAdapter.setOnLoadMoreListener(this,mRecyclerView);
        if (isHaveRefresh && mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mActivity, R.color.animal_color));
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }

    /**
     * 无数据 不展示空数据界面
     * @param isHaveRefresh
     */
    protected void initSwipeRefreshLayoutAndAdapter(boolean isHaveRefresh) {
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        mAdapter.setOnLoadMoreListener(this,mRecyclerView);
        if (isHaveRefresh && mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mActivity,R.color.animal_color));
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }



    /**
     * 全局处理分页的公共方法
     * @param obj  具体的分页对象  列表适配器
     * @param mAdapter
     */
    protected void handleSplitListData(BaseSplitIndexBean obj, BaseQuickAdapter mAdapter, int mPageSize) {
        if(obj!=null){
            int allCount = StringUtil.isBlank(obj.getCount())?0:Integer.parseInt(obj.getCount());
            int bigPage = 0;//最大页
            if(allCount%mPageSize!=0){
                bigPage=allCount/mPageSize+1;
            }else{
                bigPage=allCount/mPageSize;
            }
            if(page==bigPage){
                mAdapter.loadMoreEnd();//显示“没有更多数据”
            }

            boolean isRefresh = page==1?true:false;
            if(!ListUtil.isEmpty(obj.getList())){
                int size = obj.getList().size();

                if (isRefresh) {
                    mAdapter.setNewData(obj.getList());
                } else {
                    mAdapter.addData(obj.getList());
                }


                if (size < mPageSize) {
                    mAdapter.loadMoreEnd(isRefresh);//第一页的话隐藏“没有更多数据”，否则显示“没有更多数据”
                } else {
                    mAdapter.loadMoreComplete();
                }
            }else{

                if (isRefresh) {
                    mAdapter.setNewData(null);//刷新列表
                } else {
                    mAdapter.loadMoreEnd(false);//显示“没有更多数据”
                }
            }
        }
    }


    @Override
    public void onLoadMoreRequested() {
        onLoadMoreData();
    }

    @Override
    public void onRefresh() {
        onRefreshData();
    }


    /**
     * 下拉刷新
     */
    protected abstract void onRefreshData();


    /**
     * 上拉分页
     */
    protected abstract void onLoadMoreData();

    protected View addEmptyView(String text, int imgResource) {
        View emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_view_layout, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView iv_noData = emptyView.findViewById(R.id.iv_noData);

        if (!StringUtil.isBlank(text)) {
            TextView tv_noData = emptyView.findViewById(R.id.tv_noData);
            tv_noData.setText(text);
        }

        if (imgResource != 0) {
            iv_noData.setImageResource(imgResource);
        }
//        else {
//            iv_noData.setImageResource(R.mipmap.icon_no_date);
//        }

        return emptyView;
    }
}
