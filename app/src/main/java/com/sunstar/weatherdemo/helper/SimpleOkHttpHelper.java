package com.sunstar.weatherdemo.helper;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Created by louisgeek on 2017/1/11.
 */

public class SimpleOkHttpHelper {
    public static void reqHttp(String url, Callback okCallback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request okRequest = new Request.Builder().url(url).build();
        okHttpClient.newCall(okRequest).enqueue(okCallback);
    }
}
