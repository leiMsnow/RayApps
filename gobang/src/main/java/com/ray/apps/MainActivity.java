package com.ray.apps;

import com.gsty.corelibs.base.activity.BaseToolBarActivity;

public class MainActivity extends BaseToolBarActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        setSwipeBackEnable(false);
    }
}
