package com.sunstar.weatherdemo.custom;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by louisgeek on 2017/1/11.
 */

public class KooApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //
        LitePal.initialize(this);
    }
}
