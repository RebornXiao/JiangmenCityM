package com.liqi.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.liqi.R;
import com.liqi.utils.MLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主页
 */
public class MyLocationActivity extends BaseActivity implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener, BaiduMap.OnMapClickListener {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    @Bind(R.id.btn_go_back)
    ImageView btnGoBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_main_title)
    RelativeLayout rlMainTitle;
    @Bind(R.id.tv_position)
    TextView tvPosition;
    @Bind(R.id.tv_search)
    TextView tvSearch;
    @Bind(R.id.tv_nearby)
    TextView tvNearby;
    @Bind(R.id.tv_navi)
    TextView tvNavi;
    @Bind(R.id.rg_main)
    RadioGroup rgMain;
    @Bind(R.id.searchkey)
    AutoCompleteTextView keyWorldsView;
    @Bind(R.id.ll_city)
    LinearLayout llCity;
    @Bind(R.id.search)
    Button search;
    @Bind(R.id.map_next_data)
    Button mapNextData;
    @Bind(R.id.ll_data)
    LinearLayout llData;
    @Bind(R.id.rl_search_all)
    RelativeLayout rlSearchAll;
    @Bind(R.id.rl_route_all)
    LinearLayout rlRouteAll;
    @Bind(R.id.bmapView)
    MapView mMapView;
    @Bind(R.id.textView1)
    TextView textView1;
    @Bind(R.id.start)
    EditText start;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.end)
    EditText end;
    @Bind(R.id.tv_go_navi)
    TextView tvGoNavi;
    @Bind(R.id.rl_navi_choose)
    RelativeLayout rlNaviChoose;
    @Bind(R.id.gv_home)
    GridView gvHome;
    private MyLocationConfiguration.LocationMode mCurrentMode;

    BaiduMap mBaiduMap;
    boolean isFirstLoc = true; // 是否首次定位
    private GeoCoder mSearch;
    private TextView popupText = null; // 泡泡view

    // 搜索相关
    RoutePlanSearch mSearchPlan = null;    // 搜索模块，也可去掉地图模块独立使用

    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    //    private BaiduMap mBaiduMapSea = null;
    private List<String> suggest;
    /**
     * 搜索关键字输入窗口
     */
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;

    /**
     * 路径规划
     * @param savedInstanceState
     */
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private static final String[] names = {"公园", "博物馆", "洗浴", "足疗",
            "按摩", "医院", "公交站", "酒店", "宾馆", "银行", "药店", "ATM", "地铁站",
            "美容", "美发", "花店", "照相馆", "火车站", "邮局", "宠物店", "加油站", "饭店",
            "小吃", "火锅", "快餐", "公寓", "写字楼","快递"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
        ButterKnife.bind(this);
        /**
         * 定位初始化
         */
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
//        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(60 * 1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        /**
         * 搜索服务
         */
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
//        mBaiduMapSea = ((SupportMapFragment) (getSupportFragmentManager()
//                .findFragmentById(R.id.map))).getBaiduMap();
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString()).city("江门"));
            }
        });

        /**
         * 路径规划初始化
         */
        // 地图点击事件处理
        mBaiduMap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
//        mSearchPlan = RoutePlanSearch.newInstance();
//        mSearchPlan.setOnGetRoutePlanResultListener(this);
        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("title", names[position]);
                activityUtil.jumpTo(NearbyActivity.class, bundle);
            }
        });
    }


    @OnClick(R.id.btn_go_back)
    void back() {
        activityUtil.jumpTo(AboutActivity.class);
    }

    /**
     * 我的位置
     */
    @OnClick(R.id.tv_position)
    void myLocation() {
        toMyPosition();
        mMapView.setVisibility(View.VISIBLE);
        rlSearchAll.setVisibility(View.GONE);
        rlRouteAll.setVisibility(View.GONE);
        rlNaviChoose.setVisibility(View.GONE);
        tvPosition.setTextColor(0xff1b1b1b);
        tvPosition.setBackgroundColor(0xfff2503a);
        tvNearby.setTextColor(0xfff3f3f3);
        tvNearby.setBackgroundColor(0xff1b1b1b);
        tvNavi.setTextColor(0xfff3f3f3);
        tvNavi.setBackgroundColor(0xff1b1b1b);
        tvSearch.setTextColor(0xfff3f3f3);
        tvSearch.setBackgroundColor(0xff1b1b1b);
    }

    /**
     * 附近
     */
    @OnClick(R.id.tv_nearby)
    void tv_nearby() {
        toMyPosition();
        mMapView.setVisibility(View.GONE);
        rlRouteAll.setVisibility(View.VISIBLE);
        rlSearchAll.setVisibility(View.GONE);
        rlNaviChoose.setVisibility(View.GONE);
        tvPosition.setTextColor(0xfff3f3f3);
        tvPosition.setBackgroundColor(0xff1b1b1b);
        tvNearby.setTextColor(0xff1b1b1b);
        tvNearby.setBackgroundColor(0xfff2503a);
        tvNavi.setTextColor(0xfff3f3f3);
        tvNavi.setBackgroundColor(0xff1b1b1b);
        tvSearch.setTextColor(0xfff3f3f3);
        tvSearch.setBackgroundColor(0xff1b1b1b);
    }

    /**
     * 导航
     */
    @OnClick(R.id.tv_navi)
    void tv_navi() {
//        activityUtil.jumpTo(BNDemoMainActivity.class);
        toMyPosition();
        mMapView.setVisibility(View.GONE);
        rlRouteAll.setVisibility(View.GONE);
        rlSearchAll.setVisibility(View.GONE);
        rlNaviChoose.setVisibility(View.VISIBLE);
        tvPosition.setTextColor(0xfff3f3f3);
        tvPosition.setBackgroundColor(0xff1b1b1b);
        tvNearby.setTextColor(0xfff3f3f3);
        tvNearby.setBackgroundColor(0xff1b1b1b);
        tvNavi.setTextColor(0xff1b1b1b);
        tvNavi.setBackgroundColor(0xfff2503a);
        tvSearch.setTextColor(0xfff3f3f3);
        tvSearch.setBackgroundColor(0xff1b1b1b);
    }

    /**
     * 搜索
     */
    @OnClick(R.id.tv_search)
    void tv_search() {
        toMyPosition();
        rlSearchAll.setVisibility(View.VISIBLE);
        mMapView.setVisibility(View.VISIBLE);
        rlRouteAll.setVisibility(View.GONE);
        rlNaviChoose.setVisibility(View.GONE);
        tvPosition.setTextColor(0xfff3f3f3);
        tvPosition.setBackgroundColor(0xff1b1b1b);
        tvNearby.setTextColor(0xfff3f3f3);
        tvNearby.setBackgroundColor(0xff1b1b1b);
        tvNavi.setTextColor(0xfff3f3f3);
        tvNavi.setBackgroundColor(0xff1b1b1b);
        tvSearch.setTextColor(0xff1b1b1b);
        tvSearch.setBackgroundColor(0xfff2503a);
    }

    /**
     * 导航
     */
    @OnClick(R.id.tv_go_navi)
    void navi() {
        String startStr = start.getText().toString().trim();
        if (TextUtils.isEmpty(startStr)) {
            Toast.makeText(MyLocationActivity.this, "请填写出发地点", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        app.addressNaviStart = startStr;
        String endStr = end.getText().toString().trim();
        if (TextUtils.isEmpty(startStr)) {
            Toast.makeText(MyLocationActivity.this, "请填写目的地点", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        app.addressNaviEnd = endStr;
        activityUtil.jumpTo(PoiSearchActivity.class);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            app.addressNowCity = location.getCity();
            app.curLocationSite = location.getAddrStr();
            app.latitude = locData.latitude + "";
            app.longitude = locData.longitude + "";
            app.myLatlng = new LatLng(locData.latitude, locData.longitude);
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(getGeoCoderResultListener);
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(app.myLatlng));
            mBaiduMap.setMyLocationData(locData);
//            mBaiduMapSea.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//                mBaiduMapSea.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }
    }

    /**
     * 切换页面跳转进我的位置
     */
    public void toMyPosition() {
        mBaiduMap.clear();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(app.myLatlng).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     *地理编码查询结果回调
     */
    OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                return;
            }

            MLog.e("result=" + result.toString());
            //获取地理编码结果
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
            }
            MLog.e("result=" + result.toString());
            app.curLocationSite = result.getAddress();
            app.addressNowCity = result.getAddressDetail().city;
            tvTitle.setText(app.addressNowCity);
            if (isFirstLoc) {
                start.setText(app.curLocationSite);
            }
            MLog.e("location-------------------------->>>latitude=" + app.latitude + ",longitude="
                    + app.longitude + ",addressNowCity=" + app.addressNowCity + ",curLocationSite=" + app.curLocationSite);

            //获取反向地理编码结果
        }
    };

    /**
     * 影响搜索按钮点击事件
     *
     * @param v
     */
    public void searchButtonProcess_start(View v) {
        EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city("江门")
                .keyword(editSearchKey.getText().toString())
                .pageNum(loadIndex));
    }

    public void goToNextPage(View v) {
        loadIndex++;
        searchButtonProcess_start(null);
    }

    /**
     * 关键字查询结果
     * @param result
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(MyLocationActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(MyLocationActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * 关键字查询结果
     * @param result
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MyLocationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(MyLocationActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 关键字查询结果
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggest = new ArrayList<String>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }
        sugAdapter = new ArrayAdapter<String>(MyLocationActivity.this, android.R.layout.simple_dropdown_item_1line, suggest);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    /**
     * 关键字查询覆盖
     */
    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            Bundle bundle = new Bundle();
            bundle.putParcelable("PoiInfo", poi);
            activityUtil.jumpTo(AddressActivity.class, bundle);
            // }
            return true;
        }
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        super.onDestroy();
    }


    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        } else if (step instanceof BikingRouteLine.BikingStep) {
            nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
            nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(MyLocationActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(app.myLatlng)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */
    public void changeRouteIcon(View v) {
        if (routeOverlay == null) {
            return;
        }
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(this,
                    "将使用系统起终点图标",
                    Toast.LENGTH_SHORT).show();

        } else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(this,
                    "将使用自定义起终点图标",
                    Toast.LENGTH_SHORT).show();

        }
        useDefaultIcon = !useDefaultIcon;
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
        MLog.e("点击了=" + point);

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            exitapplication();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return names.length;
        }

        // 返回每个位置对应的view对象。
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MyLocationActivity.this,
                    R.layout.list_home_item, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_home_item);
//            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_home_item);
            tv_name.setText(names[position]);
            if (position % 4 == 1) {
                tv_name.setTextColor(0xff2d5e66);
            } else if (position % 4 == 3) {
                tv_name.setTextColor(0xff35e227);
            }
//            iv_icon.setImageResource(icons[position]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
