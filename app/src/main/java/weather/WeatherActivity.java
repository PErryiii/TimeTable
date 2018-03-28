package weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable_1.R;
import weather.gson.Forecast;
import weather.gson.Weather;
import weather.service.AutoUpdateService;
import weather.util.HttpUtil;
import weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weather_layout;

    private TextView title_title;

    private TextView title_update_time;

    private TextView degree_text;

    private TextView weather_info_text;

    private LinearLayout forecast_layout;

    private TextView aqi_text;

    private TextView pm25_text;

    private TextView comfort_text;

    private TextView carwash_text;

    private TextView sport_text;

    public SwipeRefreshLayout swipeRefreshLayout;

    public String mWeatherId;

    public DrawerLayout drawerLayout;

    private Button btn_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("weatherActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        /*初始化各个控件*/
        weather_layout = findViewById(R.id.weather_layout);
        title_title = findViewById(R.id.title_title);
        title_update_time = findViewById(R.id.title_update_time);
        degree_text = findViewById(R.id.degree_text);
        weather_info_text = findViewById(R.id.weather_info_text);
        forecast_layout = findViewById(R.id.forecast_layout);
        aqi_text = findViewById(R.id.aqi_text);
        pm25_text = findViewById(R.id.pm25_text);
        comfort_text = findViewById(R.id.comfort_text);
        carwash_text = findViewById(R.id.comfort_text);
        sport_text = findViewById(R.id.sport_text);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        btn_title = findViewById(R.id.btn_title);
        /*获取本地缓存赋值于weatherString*/
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pref.getString("weather", null);
        /*若有缓存就直接解析天气数据*/
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weather_id;
            showWeatherInfo(weather);
        } else {
            /*无缓存时从服务器中获取天气数据*/
            mWeatherId = getIntent().getStringExtra("weather_id");
            weather_layout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefreshLayout.setColorSchemeColors(getColor(R.color.colorYellow300));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        btn_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /*处理并展示Weather实体类中的数据*/
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.cond.cityInfo;
        title_title.setText(cityName);
        title_update_time.setText(updateTime);
        degree_text.setText(degree);
        weather_info_text.setText(weatherInfo);
        forecast_layout.removeAllViews();
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast_layout,
                    false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infotext = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infotext.setText(forecast.cond.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecast_layout.addView(view);
        }
        if (weather.aqi != null){
            aqi_text.setText(weather.aqi.city.cityAqi);
            pm25_text.setText(weather.aqi.city.cityPm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.comfortInfo;
        String carWash = "洗车指数：" + weather.suggestion.carwash.carwashInfo;
        String sport = "运动建议：" + weather.suggestion.sport.sportInfo;
        comfort_text.setText(comfort);
        carwash_text.setText(carWash);
        sport_text.setText(sport);
        weather_layout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /*从服务器中获取天气数据*/
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=4c99fa78e88c4dc997661f061b9eef9f";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败1",
                                Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败2",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
}
