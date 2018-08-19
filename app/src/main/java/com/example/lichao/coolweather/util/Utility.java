package com.example.lichao.coolweather.util;

import android.text.TextUtils;

import com.example.lichao.coolweather.db.City;
import com.example.lichao.coolweather.db.Country;
import com.example.lichao.coolweather.db.Province;
import com.example.lichao.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    public static boolean handleProvinceResponce(String responce){
        if (!TextUtils.isEmpty(responce)){
            try {
                JSONArray allProvince = new JSONArray(responce);
                for (int i = 0;i<allProvince.length();i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvindeCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponce(String responce,int provinceId){
        if (!TextUtils.isEmpty(responce)){
            try {
                JSONArray allCities = new JSONArray(responce);
                for (int i = 0;i<allCities.length();i++){
                    JSONObject cityObject= allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountryResponce(String responce,int cityId){
        if (!TextUtils.isEmpty(responce)){
            try {
                JSONArray allCounties = new JSONArray(responce);
                for (int i = 0;i<allCounties.length();i++){
                    JSONObject countryObjecet = allCounties.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryName(countryObjecet.getString("name"));
                    country.setWeatherId(countryObjecet.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
