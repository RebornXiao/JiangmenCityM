package com.liqi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liqi.R;
import com.liqi.activity.AboutActivity;
import com.liqi.activity.MyLocationActivity;
import com.liqi.activity.MySearchActivity;
import com.liqi.activity.PoiSearchActivity;
import com.liqi.activity.RoutePlanActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by baby on 2016/5/24.
 */
public class LeftFragment extends BaseFragment {

    public static MainListener listener;
    @Bind(R.id.tv_rl_left_main)
    TextView tvRlLeftMain;
    @Bind(R.id.rl_left_main)
    RelativeLayout rlLeftMain;
    @Bind(R.id.tv_rl_left_charge)
    TextView tvRlLeftCharge;
    @Bind(R.id.rl_left_charge)
    RelativeLayout rlLeftCharge;
    @Bind(R.id.rl_server_mycountmoney)
    RelativeLayout rlServerMycountmoney;
    @Bind(R.id.rl_left_xingcheng)
    RelativeLayout rlLeftXingcheng;
    @Bind(R.id.rl_menu_charge)
    RelativeLayout rlMenuCharge;
    @Bind(R.id.rl_menu_msg)
    RelativeLayout rlMenuMsg;
    @Bind(R.id.tv_ep_manager)
    TextView tvEpManager;
    @Bind(R.id.item_ep_manager)
    RelativeLayout itemEpManager;
    @Bind(R.id.item_mine_settings)
    RelativeLayout itemMineSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container,
                false);
        view.setOnClickListener(null);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     *
     */
    @OnClick(R.id.rl_left_main)
    void gotoMain() {
        listener.toMain();
    }


    /**
     * 我的位置
     */
    @OnClick(R.id.rl_left_charge)
    void gotoCharge() {
        activityUtil.jumpTo(MyLocationActivity.class);
    }

    /**
     * 搜索
     */
    @OnClick(R.id.rl_menu_msg)
    void message() {
        activityUtil.jumpTo(PoiSearchActivity.class);
    }

    /**
     * 查询周边
     */
    @OnClick(R.id.rl_server_mycountmoney)
    void gotoWallet() {
            activityUtil.jumpTo(MySearchActivity.class);
    }


    /**
     * 查询周边
     */
    @OnClick(R.id.rl_left_xingcheng)
    void addRoute() {
//        if (MainApplication.isLoginSuccess) {
//            activityUtil.jumpTo(OrderCarHistoryActivity.class);
//        } else {
//            activityUtil.jumpTo(LoginActivity.class);
//        }
    }

    /**
     * 导航
     */
    @OnClick(R.id.rl_menu_charge)
    void chongdian() {
    }

    /**
     * 路径规划
     */
    @OnClick(R.id.item_ep_manager)
    void managerep() {
            activityUtil.jumpTo(RoutePlanActivity.class);
    }

    /**
     * 关于
     */
    @OnClick(R.id.item_mine_settings)
    void settings() {
        activityUtil.jumpTo(AboutActivity.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface MainListener {
        void toMain();
    }
}
