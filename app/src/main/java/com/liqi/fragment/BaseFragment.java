package com.liqi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.liqi.activity.BaseActivity;
import com.liqi.utils.ActivityStackControlUtil;

/**
 * Created by baby on 2016/5/24.
 */
public class BaseFragment extends Fragment{

    public ActivityStackControlUtil activityUtil;
    private Context mContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        activityUtil = ActivityStackControlUtil.getInstance();
        activityUtil.onCreate((BaseActivity) mContext);
    }
}
