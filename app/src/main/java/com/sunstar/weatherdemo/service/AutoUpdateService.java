package com.sunstar.weatherdemo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.sunstar.weatherdemo.WeatherActivity;
import com.sunstar.weatherdemo.helper.JsonHelper;
import com.sunstar.weatherdemo.helper.SimpleOkHttpHelper;
import com.sunstar.weatherdemo.javabean.HeWeather;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class AutoUpdateService extends Service {


    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeatherInfo();
        updatePingPic();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hour8mi = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + hour8mi;
        // long triggerAtTime = System.currentTimeMillis() + hour8mi;
        Intent intent4PendingIntent = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent4PendingIntent, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);

    }

    private void updatePingPic() {
        String reqImageUrlApi = "http://guolin.tech/api/bing_pic";
        SimpleOkHttpHelper.reqHttp(reqImageUrlApi, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imageUrl = response.body().string();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                sharedPreferences.edit().putString("bing_pic", imageUrl).commit();


            }
        });
    }

    private void updateWeatherInfo() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherJson = sharedPreferences.getString("weather", null);
        if (weatherJson != null) {
            //
            //单纯使用上一次缓存的id
            HeWeather heWeather = JsonHelper.parseWeather(weatherJson);
            HeWeather.HeWeatherBean hwb = heWeather.getHeWeather().get(0);

            String weatherid = hwb.getBasic().getId();
            String url = String.format("http://guolin.tech/api/weather?cityid=%s&key=0fb8fe4a211a4b6cac088aba556b3375", weatherid);

            SimpleOkHttpHelper.reqHttp(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    final HeWeather hw = JsonHelper.parseWeather(result);
                    if (hw != null) {
                        HeWeather.HeWeatherBean hwb = hw.getHeWeather().get(0);
                        if (hwb != null && hwb.getStatus().equals("ok")) {
                            //缓存下来
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                            sp.edit().putString("weather", result).commit();

                        }
                    }

                }
            });
        }
    }

    public static void stareMe(Context context) {
        Intent intent = new Intent(context, AutoUpdateService.class);
        context.startService(intent);
    }
}

