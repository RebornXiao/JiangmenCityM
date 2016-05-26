package com.liqi.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.liqi.MApp;
import com.liqi.utils.ActivityStackControlUtil;

/**
 * 基类
 */
public class BaseActivity extends FragmentActivity{
    public MApp app;
    public Context mContext;
    public Handler uiHandler;
    public ActivityStackControlUtil activityUtil;
    private boolean isOnKeyBack;
    /**
     * 退出提示Toast
     */
    private Toast mExitToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        app = (MApp) getApplication();
        activityUtil = ActivityStackControlUtil.getInstance();
        activityUtil.onCreate(this);
        app.addActivates(this);
        uiHandler = new Handler(mContext.getMainLooper());

    }

    public void exitapplication() {
        if (isOnKeyBack) {
            uiHandler.removeCallbacks(onBackExitRunnable);
            if (mExitToast != null) {
                mExitToast.cancel();
            }
            /**直接退出*/
            app.exitApp();
        } else {
            isOnKeyBack = true;
            if (mExitToast == null) {
                mExitToast = Toast.makeText(mContext, "再按一次返回键退出应用", Toast.LENGTH_SHORT);
            }
            mExitToast.show();
            uiHandler.postDelayed(onBackExitRunnable, 2000);
        }
    }

    public Runnable onBackExitRunnable = new Runnable() {

        @Override
        public void run() {
            isOnKeyBack = false;
            if (mExitToast != null) {
                mExitToast.cancel();
            }
        }
    };
}
