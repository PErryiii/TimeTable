package com.example.timetable_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import weather.gson.Weather;
import weather.util.Utility;


public class Notification extends BaseActivity {
    private TextView text_notification_course;
    private TextView text_notification_weather;
    private Button btn_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //01.获取courseId
        Intent intent = getIntent();
        int courseId = intent.getIntExtra("courseId", 0);
        //02.获取课程信息缓存
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String courseInfo = pref.getString(String.valueOf(courseId), null);
        //03.处理courseInfo
        String[] strs = courseInfo.split("#");
        //04.课程信息setText
        text_notification_course = findViewById(R.id.text_notification_course);
        text_notification_course.setText(
                "课程名：" + strs[0] + "\n" +
                "教室：" + strs[1] + "\n" +
                "老师：" + strs[2] + "\n" +
                "节数：" + strs[3]);
        //05.获取天气信息缓存
        String weatherInfo = pref.getString("weather", null);
        //06.处理weatherInfo
        Weather weather = Utility.handleWeatherResponse(weatherInfo);
        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature + "℃";
        String sportInfo = weather.suggestion.sport.sportInfo;
        //07.天气状况setText
        text_notification_weather = findViewById(R.id.text_notification_weather);
        text_notification_weather.setText(
                "城市：" + cityName + "\n" +
                "度数：" + degree + "\n" +
                "运动建议：" + sportInfo);

        btn_finish = findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
