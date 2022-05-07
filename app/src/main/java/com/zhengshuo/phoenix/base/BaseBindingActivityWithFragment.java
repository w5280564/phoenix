package com.zhengshuo.phoenix.base;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.dylanc.viewbinding.base.ViewBindingUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 含有Fragment的Activity的基类
 * @author ouyang
 *
 */
public abstract class BaseBindingActivityWithFragment extends BaseActivity {

	protected List<BaseBindingFragment> mFragmentList = new ArrayList<BaseBindingFragment>();
	private int cuurent = 0x001;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView(savedInstanceState);
	}

	/**
	 * init view
	 * @param savedInstanceState
	 */
	protected void initView(Bundle savedInstanceState) {

	}


	/** 添加fragment,必须继承BaseFragment */
	protected void addFragment(BaseBindingFragment fragment) {
		if (mFragmentList == null) {
			mFragmentList = new ArrayList<BaseBindingFragment>();
		}
		mFragmentList.add(fragment);
	}


	protected void showFragment(int index, int fragmentId) {
		if (cuurent != 0x001 && getCurrentFrl() == mFragmentList.get(index)) {
			return;
		}
		FragmentManager manage = getSupportFragmentManager();
		FragmentTransaction transaction = manage.beginTransaction();
		BaseBindingFragment frl = mFragmentList.get(index);
		if (frl.isAdded()) {
			frl.onResume();
		} else {
			transaction.add(fragmentId, frl);
		}

		for (int i = 0; i < mFragmentList.size(); i++) {
			Fragment fragment = mFragmentList.get(i);
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			if (index == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commitAllowingStateLoss();
		}
		transaction.commitAllowingStateLoss();
		cuurent = index;
	}

	protected BaseBindingFragment getCurrentFrl() {

		return mFragmentList.get(cuurent);
	}
}
