package com.sunstar.weatherdemo.db.javabean;

import org.litepal.crud.DataSupport;

/**
 * Created by louisgeek on 2017/1/11.
 */

public class Province extends DataSupport {


    /**
     * code : 1
     * name : 北京
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
}
