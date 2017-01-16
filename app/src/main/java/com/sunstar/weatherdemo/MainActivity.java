package com.sunstar.weatherdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ChoseAreaFragment mChoseAreaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences =PreferenceManager.getDefaultSharedPreferences(this);
        String weather=sharedPreferences.getString("weather",null);
        if (weather!=null){
            WeatherActivity.stareMe(this);
        }

        mChoseAreaFragment = ChoseAreaFragment.newInstance("", "");
        //
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.id_frame_layout, mChoseAreaFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        //##super.onBackPressed();
        mChoseAreaFragment.callAtAtyOnBackPressed();
    }
}
