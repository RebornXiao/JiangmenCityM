package com.liqi.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.liqi.R;

import java.io.File;

/**
 * 欢迎页面
 */
public class LoadActivity extends BaseActivity {
    private static final String APP_FOLDER_NAME = "Jiangmen";

    private Button mWgsNaviBtn = null;
    private Button mGcjNaviBtn = null;
    private Button mBdmcNaviBtn = null;
    private Button mDb06ll = null;
    private String mSDCardPath = null;

    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";

    private BNRoutePlanNode mBNRoutePlanNode = null;
    private BaiduNaviCommonModule mBaiduNaviCommonModule = null;
    /*
     * 对于导航模块有两种方式来实现发起导航。 1：使用通用接口来实现 2：使用传统接口来实现
     */
    // 是否使用通用接口
    private boolean useCommonInterface = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        if (initDirs()) {
            initNavi();
        }
        //跳转进引导页面
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activityUtil.jumpTo(GuideActivity.class);
                finish();
            }
        }, 1500);
    }

    /**
     * 导航初始化存储位置
     * @return
     */
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    String authinfo = null;

    /**
     * 导航初始化
     * @return
     */
    private void initNavi() {
        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                LoadActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                        Toast.makeText(LoadActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
//                Toast.makeText(LoadActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                initSetting();
            }

            public void initStart() {
//                Toast.makeText(LoadActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
//                Toast.makeText(LoadActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        }, null, ttsHandler, null);

    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
//                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
//                    showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };
    protected void onPause() {
        super.onPause();

        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onPause();
            }
        } else {
            BNRouteGuideManager.getInstance().onPause();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onDestroy();
            }
        } else {
            BNRouteGuideManager.getInstance().onDestroy();
        }
        BNDemoMainActivity.activityList.remove(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onStop();
            }
        } else {
            BNRouteGuideManager.getInstance().onStop();
        }
    }

    public void showToastMsg(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(LoadActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *导航相关设置
     */
    private void initSetting() {
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }
}
