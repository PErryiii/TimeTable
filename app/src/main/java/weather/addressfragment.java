package weather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable_1.R;
import weather.db.City;
import weather.db.Country;
import weather.db.Province;
import weather.util.HttpUtil;
import weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by PErry on 2018/1/12.
 */

public class addressfragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTRY = 2;

    private TextView textView_address_title;

    private Button btn_address_title;

    private ListView listView_address_content;

    private ProgressDialog progressDialog;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /*各个地级的列表*/
    private List<Province> provinceList;

    private List<City> cityList;

    private List<Country> countryList;


    /*选中的地级*/
    private Province selectProvince;

    private City selectCity;

    /*当前地级的级别*/
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.address, container, false);
        textView_address_title = view.findViewById(R.id.text_address_title);
        btn_address_title = view.findViewById(R.id.btn_address_title);
        listView_address_content = view.findViewById(R.id.listView_address_content);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                dataList);
        listView_address_content.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView_address_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectProvince = provinceList.get(i);
                    queryCity();
                } else if (currentLevel == LEVEL_CITY){
                    selectCity= cityList.get(i);
                    queryCountry();
                } else if (currentLevel == LEVEL_COUNTRY){
                    String weatherId = countryList.get(i).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mWeatherId = weatherId;
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        btn_address_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTRY){
                     queryCity();
                } else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    /*遍历省列表*/
    public void queryProvince(){
        textView_address_title.setText("中国");
        btn_address_title.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class); /*检索数据库中所有省级数据*/
        if (provinceList.size() > 0){
            /*优先从数据库检索省级数据*/
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged(); /*更改适配器内容*/
            listView_address_content.setSelection(0);   /*将选中项设为第一项*/
            currentLevel = LEVEL_PROVINCE;  /*初始化地级等级*/
        } else{
            /*从服务器获取省级数据*/
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /*遍历市列表*/
    public void queryCity(){
        textView_address_title.setText(selectProvince.getProvinceName());
        btn_address_title.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0){
            /*优先从数据库检索市级数据*/
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView_address_content.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else{
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china" + "/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /*遍历县列表*/
    public void queryCountry(){
        textView_address_title.setText(selectCity.getCityName());
        btn_address_title.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid = ?",
                String.valueOf(selectCity.getCityCode())).find(Country.class);
        if (countryList.size() > 0){
            dataList.clear();
            for (Country country : countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView_address_content.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        } else{
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/"
                    + cityCode;
            queryFromServer(address, "country");
        }
    }

    /*从服务器获取数据*/
    public void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText ,
                            selectProvince.getProvinceCode());
                } else if ("country".equals(type)){
                    result = Utility.handleCountryResponse(responseText,
                            selectCity.getCityCode());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            } else if ("city".equals(type)){
                                queryCity();
                            } else if ("country".equals(type)){
                                queryCountry();
                            }
                        }
                    });
                }
            }
        });
    }

    /*显示进度对话框*/
    public void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*关闭进度对话框*/
    public void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
