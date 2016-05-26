package com.liqi.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.liqi.R;
import com.liqi.utils.MLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 地址确定
 */
public class AddressActivity extends BaseActivity {

    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_go)
    TextView tvGo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_go_back, R.id.btn_go_back})
    void back() {
        finish();
    }

    @OnClick({R.id.tv_go})
    void go() {
        activityUtil.jumpTo(RoutePlanActivity.class);
        finish();
    }

    /**
     * 接受从主页过来的数据
     */
    @Override
    protected void onStart() {
        super.onStart();
        PoiInfo poiInfo = getIntent().getParcelableExtra("PoiInfo");
        if (poiInfo != null && !TextUtils.isEmpty(poiInfo.name)) {
            tvName.setText(poiInfo.name);
            tvAddress.setText(poiInfo.address);
            MLog.e("获取数据=" + poiInfo.toString());
            app.latlngSearch = poiInfo.location;
        }
    }
}
