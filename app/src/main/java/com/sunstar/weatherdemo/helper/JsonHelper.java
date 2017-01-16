package com.sunstar.weatherdemo.helper;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sunstar.weatherdemo.db.bean.Area;
import com.sunstar.weatherdemo.db.bean.City;
import com.sunstar.weatherdemo.db.bean.Province;
import com.sunstar.weatherdemo.javabean.HeWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;

/**
 * Created by louisgeek on 2017/1/11.
 */

public class JsonHelper {
    public static boolean parseProvince(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray jsonArrayProvinces = new JSONArray(json);

                for (int i = 0; i < jsonArrayProvinces.length(); i++) {
                    JSONObject jsonObject = jsonArrayProvinces.getJSONObject(i);
                    int code = jsonObject.getInt("id");
                    //int code =jsonObject.getInt("code");
                    String name = jsonObject.getString("name");
                    //
                    Province p = new Province();
                    p.setId(id);
                    p.setCode(code);
                    p.setName(name);
                    //
                    p.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean parseCity(String json, int nowProvinceId) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray jsonArrayProvinces = new JSONArray(json);

                for (int i = 0; i < jsonArrayProvinces.length(); i++) {
                    JSONObject jsonObject = jsonArrayProvinces.getJSONObject(i);
                    int code = jsonObject.getInt("id");
                    // int code = jsonObject.getInt("code");
                    String name = jsonObject.getString("name");
                    //
                    City c = new City();
                    c.setId(id);
                    c.setCode(code);
                    c.setProvinceId(nowProvinceId);
                    c.setName(name);
                    //
                    c.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean parseArea(String json, int nowCityId) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray jsonArrayProvinces = new JSONArray(json);

                for (int i = 0; i < jsonArrayProvinces.length(); i++) {
                    JSONObject jsonObject = jsonArrayProvinces.getJSONObject(i);
                    int code = jsonObject.getInt("id");
                    //  int code = jsonObject.getInt("code");
                    String name = jsonObject.getString("name");
                    String weather_id = jsonObject.getString("weather_id");
                    //
                    Area a = new Area();
                    a.setId(id);
                    a.setCode(code);
                    a.setCityId(nowCityId);
                    a.setWeather_id(weather_id);
                    a.setName(name);
                    //
                    a.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static HeWeather parseWeather(String json) {
        HeWeather heWeather = new Gson().fromJson(json, HeWeather.class);
        return heWeather;
    }
}
