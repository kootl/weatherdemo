package com.sunstar.weatherdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sunstar.weatherdemo.helper.JsonHelper;
import com.sunstar.weatherdemo.helper.SimpleOkHttpHelper;
import com.sunstar.weatherdemo.javabean.HeWeather;
import com.sunstar.weatherdemo.service.AutoUpdateService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class WeatherActivity extends AppCompatActivity {

    private ScrollView id_sv_weather_all_layout;

    private TextView id_tv_title;
    private TextView id_tv_right_time;

    private TextView id_tv_degree;
    private TextView id_tv_info;

    private LinearLayout id_ll_forecast;
    private TextView id_tv_aqi;
    private TextView id_tv_pm25;

    private TextView id_tv_comfort;
    private TextView id_tv_car_wash;
    private TextView id_tv_sport;

    private SwipeRefreshLayout id_srl_refresh;

    private ImageView id_iv_bg;

    private String weatheridStr = "CN101210102";

    private Button id_btn_home;

    private DrawerLayout id_drawer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        id_btn_home = (Button) findViewById(R.id.id_btn_home);
        id_drawer_layout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        //菜单
      /*getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.id_left_drawer_content, ChoseAreaFragment.newInstance("", ""))
                .commit();*/
        id_btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_drawer_layout.openDrawer(GravityCompat.START);
            }
        });
        //
        id_srl_refresh = (SwipeRefreshLayout) findViewById(R.id.id_srl_refresh);
        id_srl_refresh.setColorSchemeResources(R.color.colorPrimary);

        id_iv_bg = (ImageView) findViewById(R.id.id_iv_bg);

        id_sv_weather_all_layout = (ScrollView) findViewById(R.id.id_sv_weather_all_layout);
        id_sv_weather_all_layout.setVisibility(View.GONE);

        id_tv_title = (TextView) findViewById(R.id.id_tv_title);
        id_tv_right_time = (TextView) findViewById(R.id.id_tv_right_time);


        id_tv_degree = (TextView) findViewById(R.id.id_tv_degree);
        id_tv_info = (TextView) findViewById(R.id.id_tv_info);


        id_ll_forecast = (LinearLayout) findViewById(R.id.id_ll_forecast);


        id_tv_aqi = (TextView) findViewById(R.id.id_tv_aqi);
        id_tv_pm25 = (TextView) findViewById(R.id.id_tv_pm25);


        id_tv_comfort = (TextView) findViewById(R.id.id_tv_comfort);
        id_tv_car_wash = (TextView) findViewById(R.id.id_tv_car_wash);
        id_tv_sport = (TextView) findViewById(R.id.id_tv_sport);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        final String bing_pic = sharedPreferences.getString("bing_pic", null);
        if (bing_pic == null) {
            String reqImageUrlApi = "http://guolin.tech/api/bing_pic";
            SimpleOkHttpHelper.reqHttp(reqImageUrlApi, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(WeatherActivity.this, "获取图片数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String imageUrl = response.body().string();

                    SharedPreferences sharedPreferences = getDefaultSharedPreferences(WeatherActivity.this);
                    sharedPreferences.edit().putString("bing_pic", imageUrl).commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(imageUrl).into(id_iv_bg);
                        }
                    });

                }
            });
        } else {
            Glide.with(this).load(bing_pic).into(id_iv_bg);

        }


        String cacheWeatherJson = sharedPreferences.getString("weather", null);


        if (cacheWeatherJson == null) {

            if (getIntent() != null) {
                weatheridStr = getIntent().getStringExtra("weatherid");
            }

            reqWeather();

        } else {
            //有缓存

            HeWeather heWeather = JsonHelper.parseWeather(cacheWeatherJson);
            HeWeather.HeWeatherBean hwb = heWeather.getHeWeather().get(0);
            //赋值
            weatheridStr = hwb.getBasic().getId();

            configAllView(hwb);
        }

        id_srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reqWeather();
            }
        });

    }

    private void reqWeather() {
        id_srl_refresh.setRefreshing(true);
        //
        String url = String.format("http://guolin.tech/api/weather?cityid=%s&key=0fb8fe4a211a4b6cac088aba556b3375", weatheridStr);

        SimpleOkHttpHelper.reqHttp(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        id_srl_refresh.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                final HeWeather hw = JsonHelper.parseWeather(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        id_srl_refresh.setRefreshing(false);
                        if (hw != null) {
                            HeWeather.HeWeatherBean hwb = hw.getHeWeather().get(0);
                            if (hwb != null && hwb.getStatus().equals("ok")) {
                                //缓存下来
                                SharedPreferences sp = getDefaultSharedPreferences(WeatherActivity.this);
                                sp.edit().putString("weather", result).commit();
                                //
                                configAllView(hwb);

                                //
                                AutoUpdateService.stareMe(WeatherActivity.this);
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void configAllView(HeWeather.HeWeatherBean heWeatherBean) {


        id_tv_title.setText(heWeatherBean.getBasic().getCity());
        id_tv_right_time.setText(heWeatherBean.getBasic().getUpdate().getLoc());


        id_tv_degree.setText(heWeatherBean.getNow().getTmp() + "℃");
        id_tv_info.setText(heWeatherBean.getNow().getCond().getTxt());


        if (heWeatherBean.getAqi()!=null&&heWeatherBean.getAqi().getCity()!=null) {
            id_tv_aqi.setText(heWeatherBean.getAqi().getCity().getAqi());
            id_tv_pm25.setText(heWeatherBean.getAqi().getCity().getPm25());
        }


        id_tv_comfort.setText(heWeatherBean.getSuggestion().getComf().getTxt());
        id_tv_car_wash.setText(heWeatherBean.getSuggestion().getCw().getTxt());
        id_tv_sport.setText(heWeatherBean.getSuggestion().getSport().getTxt());


        id_ll_forecast.removeAllViews();
        for (int i = 0; i < heWeatherBean.getDaily_forecast().size(); i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.forecast_item, null, false);
            TextView id_tv_data = (TextView) itemView.findViewById(R.id.id_tv_data);
            TextView id_tv_info_2 = (TextView) itemView.findViewById(R.id.id_tv_info_2);
            TextView id_tv_max = (TextView) itemView.findViewById(R.id.id_tv_max);
            TextView id_tv_min = (TextView) itemView.findViewById(R.id.id_tv_min);

            id_tv_data.setText(heWeatherBean.getDaily_forecast().get(i).getDate());
            id_tv_info_2.setText(heWeatherBean.getDaily_forecast().get(i).getCond().getTxt_d());
            id_tv_max.setText(heWeatherBean.getDaily_forecast().get(i).getTmp().getMax());
            id_tv_min.setText(heWeatherBean.getDaily_forecast().get(i).getTmp().getMin());
            id_ll_forecast.addView(itemView);
        }

        id_sv_weather_all_layout.setVisibility(View.VISIBLE);
    }

    public static void stareMe(Context context, String weatherid) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra("weatherid", weatherid);
        context.startActivity(intent);
    }

    public static void stareMe(Context context) {
        Intent intent = new Intent(context, WeatherActivity.class);
        context.startActivity(intent);
    }

    public void changeArea(String weatherid) {
        id_drawer_layout.closeDrawers();
        weatheridStr = weatherid;//更新赋值
        reqWeather();
    }
}
