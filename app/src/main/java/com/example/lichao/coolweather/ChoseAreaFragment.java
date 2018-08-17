package com.example.lichao.coolweather;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lichao.coolweather.db.City;
import com.example.lichao.coolweather.db.Country;
import com.example.lichao.coolweather.db.Province;
import com.example.lichao.coolweather.util.HttpUtil;
import com.example.lichao.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChoseAreaFragment extends Fragment {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUTRY = 2;
    private ProgressDialog progressDialog;
    private TextView titleView;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    private Province seclectedProvince;
    private City selectedCity;
    private int currentLevel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.choose_area,container,false);
       titleView = (TextView) view.findViewById(R.id.title_text);
       backButton = (Button)view.findViewById(R.id.back_button);
       listView=(ListView)view.findViewById(R.id.list_view);
       adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
       listView.setAdapter(adapter);
       return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    seclectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCunties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUTRY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces(){
        titleView.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel = LEVEL_PROVINCE;
            }
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    private void queryCities(){
        titleView.setText(seclectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        Log.d("MD","YES");
        cityList = DataSupport.where("provinceid = ?",String.valueOf(seclectedProvince.getId())).find(City.class);
        Log.d("MD","NO");
        if (cityList.size()>0){
            Log.d("MD","1");
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            Log.d("MD","0");
            int provinceCode = seclectedProvince.getProvindeCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    private void queryCunties(){
        titleView.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList =DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(Country.class);
        if (countryList.size()>0){
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUTRY;
        }else {
            int provinceCode = seclectedProvince.getProvindeCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"country");
        }
    }
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d("MM",responseText);
                boolean result = false;
                if ("province" .equals(type)){
                    result = Utility.handleProvinceResponce(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponce(responseText,seclectedProvince.getId());
                }
                else if ("country".equals(type)){
                    result = Utility.handleCountryResponce(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province" .equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }
                            else if ("country".equals(type)){
                               queryCunties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
