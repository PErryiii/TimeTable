package com.example.timetable_1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import weather.gson.Weather;
import weather.util.Utility;


public class Notification extends BaseActivity {
    /*控件*/
    private TextView text_notification_course;
    private TextView text_notification_weather;
    private Button btn_finish;
    /*唤醒锁屏*/
    private PowerManager.WakeLock wakeLock;
    /*铃声*/
    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        showActivityOnLockScreen();     //让Activity显示在锁屏界面上
        playMusic();

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

    @Override
    protected void onResume() {
        super.onResume();
        wakeUpAndUnlock();      //唤醒屏幕
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();     //释放wakeLock
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone.isPlaying()){      //关闭闹钟
            ringtone.stop();
        }
    }

    /*唤醒屏幕*/
    private void wakeUpAndUnlock(){
        //01.获取电源管理器对象
        PowerManager powerManager = (PowerManager) getApplicationContext()
                .getSystemService(Context.POWER_SERVICE);
        //02.判断是否锁屏
        boolean isScreenOn = powerManager.isScreenOn();
        if (!isScreenOn){
            //03.获取PowerManager.WakeLock对象
            wakeLock = powerManager.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP|
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            //04.点亮屏幕
            wakeLock.acquire(10000);
        }
    }
    /*让Activity显示在锁屏界面上*/
    private void showActivityOnLockScreen(){
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        |WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
    /*释放锁屏*/
    private void releaseWakeLock(){
        if (wakeLock != null && wakeLock.isHeld()){
            wakeLock.release();
            wakeLock = null;
        }
    }
    /*播放闹钟铃声*/
    private void playMusic() {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();
    }
}
