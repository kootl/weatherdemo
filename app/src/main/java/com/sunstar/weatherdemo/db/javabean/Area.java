package com.sunstar.weatherdemo.db.javabean;

import org.litepal.crud.DataSupport;

/**
 * Created by louisgeek on 2017/1/11.
 */

public class Area extends DataSupport{


    /**
     * code : 999
     * name : 杭州
     * weather_id : CN101210101
     */

    private int id;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;
    private String name;
    private String weather_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }
}
