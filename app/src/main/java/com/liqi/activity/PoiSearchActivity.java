package com.liqi.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.liqi.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by baby on 2016/5/24.
 */
public class PoiSearchActivity extends BaseActivity implements //OnGetPoiSearchResultListener,
        OnGetSuggestionResultListener {

    @Bind(R.id.searchkey)
    AutoCompleteTextView keyWorldsView;
    @Bind(R.id.search)
    Button search;
    @Bind(R.id.map_next_data)
    Button mapNextData;
    @Bind(R.id.btn_go_back)
    ImageView btnGoBack;
    @Bind(R.id.tv_go_back)
    TextView tvGoBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_my_search)
    RelativeLayout rlMySearch;
    @Bind(R.id.ll_city)
    LinearLayout llCity;
    @Bind(R.id.ll_data)
    LinearLayout llData;
    @Bind(R.id.tv_start_position)
    TextView tvStartPosition;
    @Bind(R.id.tv_start_position_gps)
    TextView tvStartPositionGps;
    @Bind(R.id.tv_end_position)
    TextView tvEndPosition;
    @Bind(R.id.tv_end_position_gps)
    TextView tvEndPositionGps;
    @Bind(R.id.tv_start_navi)
    TextView tvStartNavi;
    private SuggestionSearch mSuggestionSearch = null;
    private BaiduMap mBaiduMap = null;
    private List<String> suggest;
    /**
     * 搜索关键字输入窗口
     */
    private ArrayAdapter<String> sugAdapter = null;
    private int searchCount = 1;
    private boolean isNavi = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisearch);
        ButterKnife.bind(this);
//        // 初始化搜索模块，注册搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map))).getBaiduMap();

        tvStartPosition.setText(app.addressNaviStart);

        searchCount = 1;
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword(app.addressNaviStart).city("江门"));

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mSuggestionSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            if (searchCount == 1) {
                isNavi = false;
                Toast.makeText(PoiSearchActivity.this, "抱歉，起点未找到", Toast.LENGTH_SHORT)
                        .show();
            } else if (searchCount == 2) {
                isNavi = false;
                Toast.makeText(PoiSearchActivity.this, "抱歉，终点未找到", Toast.LENGTH_SHORT)
                        .show();
            }
            return;
        }
        if (searchCount == 1) {
            app.longitudeNaviStart = res.getAllSuggestions().get(0).pt.longitude;
            app.latitudeNaviStart = res.getAllSuggestions().get(0).pt.latitude;
            searchCount = 2;
            tvStartPositionGps.setText(app.latitudeNaviStart + "," + app.longitudeNaviStart);
        } else if (searchCount == 2) {
            app.longitudeNaviEnd = res.getAllSuggestions().get(0).pt.longitude;
            app.latitudeNaviEnd = res.getAllSuggestions().get(0).pt.latitude;
            searchCount = 1;
            tvEndPositionGps.setText(app.latitudeNaviEnd + "," + app.longitudeNaviEnd);
            tvEndPosition.setText(app.addressNaviEnd);
            return;
        }
        if (searchCount == 2) {
            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(app.addressNaviEnd).city("江门"));
        }
        return;

    }
    /**
     * 返回
     */
    @OnClick({R.id.tv_go_back, R.id.btn_go_back})
    void back() {
        activityUtil.jumpBackTo(MyLocationActivity.class);
        finish();
    }
    /**
     * 开始导航
     */
    @OnClick({R.id.tv_start_navi})
    void startNavi() {
        activityUtil.jumpBackTo(BNDemoMainActivity.class);
        finish();
    }
}

