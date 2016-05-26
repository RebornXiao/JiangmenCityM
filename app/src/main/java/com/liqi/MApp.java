package com.liqi;

import android.app.Activity;
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baby on 2016/5/24.
 */
public class MApp extends Application {
    private List<SoftReference<Activity>> activitys = new ArrayList<SoftReference<Activity>>();
    public String latitude = "0.0";
    public String longitude = "0.0";
    public String curLocationSite = "广东省江门市";

    public double latitudeNaviStart = 22.553948;
    public double longitudeNaviStart = 113.935457;
    public String addressNaviStart = "广东省江门市";
    public double latitudeNaviEnd = 22.653948;
    public double longitudeNaviEnd =113.835457;
    public String addressNaviEnd= "广东省江门市";
    public String curCityCode = "00";
    public String addressNowCity = "江门市";
    public LatLng myLatlng = new LatLng(22.586595, 113.087878);
    public LatLng latlngSearch = new LatLng(22.586595, 113.087878);




    public void exitApp() {
//        closeBlueTooth();
        for (SoftReference<Activity> activity : activitys) {
            Activity temp;
            if ((temp = activity.get()) != null) {
                temp.finish();
            }
        }
//        stopService(new Intent(getBaseContext(), MyLocationService.class));

        onTerminate();
    }

    public void addActivates(Activity activity) {
        activitys.add(new SoftReference<>(activity));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }
}
