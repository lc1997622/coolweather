package com.example.lichao.coolweather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int provindeCode;
    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvindeCode() {
        return provindeCode;
    }

    public void setProvindeCode(int provindeCode) {
        this.provindeCode = provindeCode;
    }
}
