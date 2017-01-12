package com.sunstar.weatherdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    ChoseAreaFragment mChoseAreaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
