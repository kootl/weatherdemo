package com.sunstar.weatherdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ChoseAreaFragment mChoseAreaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherJson = sharedPreferences.getString("weather", null);
        if (weatherJson != null) {
            //有缓存
            WeatherActivity.stareMe(this);
        } else {
            mChoseAreaFragment = ChoseAreaFragment.newInstance("", "");
            //
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.id_frame_layout, mChoseAreaFragment)
                    .commit();
        }


    }

    @Override
    public void onBackPressed() {
        //##super.onBackPressed();
        if (mChoseAreaFragment != null) {
            mChoseAreaFragment.callAtAtyOnBackPressed();
        }
    }
}
