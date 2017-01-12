package com.sunstar.weatherdemo.db.javabean;

import org.litepal.crud.DataSupport;

/**
 * Created by louisgeek on 2017/1/11.
 */

public class DataSupportBean extends DataSupport{
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private  int id;
}
