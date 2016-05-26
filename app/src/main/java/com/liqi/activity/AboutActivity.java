package com.liqi.activity;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.liqi.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {

    @Bind(R.id.btn_go_back)
    ImageView btnGoBack;
    @Bind(R.id.tv_go_back)
    TextView tvGoBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private int count = 0;
    private PackageInfo pi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }


    /**
     * 回退
     */
    @OnClick({R.id.tv_go_back, R.id.btn_go_back})
    void back() {
        activityUtil.jumpBackTo(MyLocationActivity.class);
        finish();
    }

    /**
     * 回退
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activityUtil.jumpBackTo(MyLocationActivity.class);
        finish();
    }
}
