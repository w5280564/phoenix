package com.zhengshuo.phoenix.ui.friend.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseFragment;
import com.zhengshuo.phoenix.model.SearchFriendInfoBean;
import com.zhengshuo.phoenix.ui.interfaces.OnSearchFriendItemClickListener;
import com.zhengshuo.phoenix.util.ImgLoader;
import com.zhengshuo.phoenix.widget.YRImageView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Description: 搜索结果页
 * @Author: ouyang
 * @CreateDate: 2022/3/14 0010
 */
public class SearchFriendResultFragment extends BaseFragment{
    private static final String TAG = "SearchFriendResultFragment";
    @BindView(R.id.search_header)
    YRImageView searchHeader;
    @BindView(R.id.search_name)
    TextView searchName;
    @BindView(R.id.search_id)
    TextView searchId;
    private SearchFriendInfoBean searchFriendInfoBean;
    private OnSearchFriendItemClickListener searchFriendItemClickListener;

    public void setData(
            OnSearchFriendItemClickListener listener, SearchFriendInfoBean searchFriendInfoBean) {
        this.searchFriendInfoBean = searchFriendInfoBean;
        searchFriendItemClickListener = listener;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.frament_search_friend_result;
    }

    @Override
    protected void initView(View mView) {
        if (searchFriendInfoBean !=null) {
            searchName.setText(searchFriendInfoBean.getNickname());
            String stAccount = searchFriendInfoBean.getImId();
            if (!TextUtils.isEmpty(stAccount)) {
                searchId.setText(String.format("IM 号:%s",stAccount));
            }
            String portraitUri = searchFriendInfoBean.getHeadImg();
            ImgLoader.getInstance().displayCrop(mContext,searchHeader,portraitUri,R.mipmap.error_image_placeholder);
        }

    }


    @OnClick(R.id.search_result)
    public void onClick() {
        if (searchFriendItemClickListener != null) {
            searchFriendItemClickListener.onSearchFriendItemClick(searchFriendInfoBean);
        }
    }
}
