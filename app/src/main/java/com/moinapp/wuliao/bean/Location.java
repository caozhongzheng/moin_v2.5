package com.moinapp.wuliao.bean;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/5/18.
 */
public class Location implements Serializable {
    private String country;
    private String province;
    private String city;
    private String street;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
