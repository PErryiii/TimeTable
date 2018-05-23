package com.example.timetable_1;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tableData.Course;
import tableData.TableDataSource;
import tableData.TableLocalDataSource;
import tableList.Table;
import tableList.TableAdapter;
import weather.db.City;
import weather.db.Country;
import weather.db.Province;
import weather.gson.Weather;
import weather.util.HttpUtil;
import weather.util.Utility;

public class MainActivity extends BaseActivity {

    private TableLocalDataSource tableLocalDataSource;
    private TableAdapter adapter;

    private TabLayout tabLayout_main_mainTitle;
    private ViewPager viewPager_main_mainContent;
    private DrawerLayout drawerLayout_main;
    private NavigationView navigationView_main;
    private android.support.v7.widget.Toolbar toolbar_table_title;
    private TextView text_main_title;
    private LinearLayout layout_nav_weather;
    private TextView text_nav_degree;
    private TextView text_nav_info;
    private CircleImageView head;

    /*各个地级的列表*/
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    /*各个地级的Code*/
    private int provinceCode;
    private int cityCode;
    private String weatherId;
    /*各个地级的地址*/
    private String province;
    private String city;
    private String country;
    /*头像*/
    private Uri imageUri;
    private String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLocalDataSource = TableLocalDataSource.getINSTANCE(this);

        bindView();

        /*获取本地缓存赋值于weatherString*/
        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString1 = pref1.getString("weather", null);
        if (weatherString1 != null){
            Weather weather = Utility.handleWeatherResponse(weatherString1);
            weatherId = weather.basic.weather_id;}
        requestWeather(weatherId);


        initTabLayout();
        initViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置nav天气信息
        /*获取本地缓存赋值于weatherString*/
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pref.getString("weather", null);
        /*若有缓存就直接解析天气数据*/
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weather_id;
            showWeatherInfo(weather);
        } else {
            /*无缓存时从服务器中获取天气数据*/
            requestWeather(weatherId);
        }
    }

    /*初始化控件*/
    private void bindView(){
        toolbar_table_title = findViewById(R.id.toolbar_table_title);
        text_main_title = findViewById(R.id.text_main_title);
        drawerLayout_main = findViewById(R.id.drawerLayout_main);
        navigationView_main = findViewById(R.id.navigationView_main);
        layout_nav_weather = findViewById(R.id.layout_nav_weather);
        text_nav_degree = findViewById(R.id.text_nav_degree);
        text_nav_info = findViewById(R.id.text_nav_info);
        head = findViewById(R.id.head);

        //点击打开天气详情
        layout_nav_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        weather.MainActivity.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar_table_title);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.menu_black_36x36);
        }

        /**
         * 设置头像
         * */
        View headerView = navigationView_main.getHeaderView(0);
        head = headerView.findViewById(R.id.head);
        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        String uri = pref1.getString("imagePath", null);
        displayImage(uri);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (ContextCompat.checkSelfPermission(MainActivity.this,
                       Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                   ActivityCompat.requestPermissions(MainActivity.this,
                           new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,1);
               } else {
                   Intent intent = new Intent("android.intent.action.GET_CONTENT");
                   intent.setType("image/*");
                   startActivityForResult(intent, 111);
               }
            }
        });
    }

    /*初始化TabLayout*/
    private void initTabLayout(){
        tabLayout_main_mainTitle = findViewById(R.id.tabLayout_main_mainTitle);
        /*添加Tab*/
        tabLayout_main_mainTitle.addTab(tabLayout_main_mainTitle.newTab().setText("课表"));
        tabLayout_main_mainTitle.addTab(tabLayout_main_mainTitle.newTab().setText("笔记"));

        /*Tab的状态逻辑*/
        tabLayout_main_mainTitle.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /*与ViewPager对应页面进行连接*/
                viewPager_main_mainContent.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        text_main_title.setText("课程表");
                        break;
                    case 1:
                        text_main_title.setText("笔记");
                        break;
                        default:
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /*初始化ViewPager*/
    private void initViewPager(){
        viewPager_main_mainContent = findViewById(R.id.viewPager_main_mainContent);
        /*设置Adapter*/
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager_main_mainContent.setAdapter(adapter);
        /*处理ViewPager的状态逻辑*/
        viewPager_main_mainContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*与Tab进行连接*/
                tabLayout_main_mainTitle.getTabAt(position).select();
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * 用来根据ViewPager显示页面控制Menu显示的Item
     * Demo里我用的是ViewPager滑动来控制，可以根据自己需求，不过大多数都是判断ViewPager吧。
     * */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        switch (viewPager_main_mainContent.getCurrentItem()){
            case 0:
                menu.findItem(R.id.menu_table_deleteTable).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.menu_table_deleteTable).setVisible(false);
                break;
                default:
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout_main.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_table_deleteTable:
                final AlertDialog alertDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("警告！");
                builder.setMessage("是否删除课程表！");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*删除所有的课程*/
                        final List<Course> tableList = new ArrayList<>();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 耗时的操作，在子线程中进行。
                                try {
                                    for (int i = 0; i < 84; i++){
                                        Course course = new Course(Integer.toString(i));
                                        tableLocalDataSource.updateCourse(course);
                                    }
                                    tableLocalDataSource.getTable(new TableDataSource.loadTableCallback() {
                                        @Override
                                        public void loadSuccess(List<Course> courseList) {
                                            for (Course course : courseList){
                                                tableList.add(course);
                                            }
                                        }

                                        @Override
                                        public void loadFailed() {

                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
                break;
                default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 111:
                if (resultCode == RESULT_OK){
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
                default:
                    break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)){
            //若是document类型的Uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.download.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri
                        .parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方法处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putString("imagePath", imagePath);
        editor.apply();
        displayImage(imagePath);    //根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putString("imagePath", imagePath);
        editor.apply();
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection,
                null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            head.setImageBitmap(bitmap);
        }
    }

    /**
     * 获取weatherId
     * @param province
     * @param city
     * @param country
     * */
    public void getWeatherId(String province, String city, String country){
        //获取省级Code
        queryProvinceCode(province);
        queryCityCode(city);
        queryWeatherId(country);
    }


    /**
     * 获取省级Code
     * @param province
     * */
    private void queryProvinceCode(String province){
        /*检索数据库中所有省级数据*/
        provinceList = DataSupport.where("provinceName = ?", province)
                .find(Province.class);
        if (provinceList.size() > 0){
            /*优先从数据库检索省级Code*/
            for (Province province1 : provinceList){
                provinceCode = province1.getProvinceCode();
            }
        } else{
            /*从服务器获取省级数据*/
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 获取市级Code
     * @param city
     * */
    private void queryCityCode(String city){
        //检索数据库中所有的市级数据
        cityList = DataSupport.where("cityName = ?", city).find(City.class);
        if (cityList.size() > 0){
            //优先从数据库中检索市级Code
            for (City city1 : cityList){
                cityCode = city1.getCityCode();
            }
        } else {
            //从服务器获取市级数据
            String address = "http://guolin.tech/api/china" + "/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 获取weatherId
     * @param country
     * */
    private void queryWeatherId(String country){
        //检索数据库中所有的镇级数据
        countryList = DataSupport.where("countryName = ?", country).find(Country.class);
        if (countryList.size() > 0){
            //优先从数据库中检索镇级Code
            for (Country country1 : countryList){
                weatherId = country1.getWeatherId();
            }
        } else {
            //从服务器获取镇级数据
            String address = "http://guolin.tech/api/china" + "/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "country");
        }
    }


    /**
     * 从服务器获取数据
     * @param address
     * @param type
     * */
    public void queryFromServer(String address, final String type){
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

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
                    result = Utility.handleCityResponse(responseText , provinceCode);
                } else if ("country".equals(type)){
                    result = Utility.handleCountryResponse(responseText, cityCode);
                }
                if (result){
                    if ("province".equals(type)){
                        queryProvinceCode(province);
                    } else if ("city".equals(type)){
                        queryCityCode(city);
                    } else if ("country".equals(type)){
                        queryWeatherId(country);
                    }
                }
            }
        });
    }

    /**
     * 显示天气信息
     * @param weather
     * */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature + "℃";
        String info = cityName + "  " + weather.now.cond.cityInfo;
        text_nav_degree.setText(degree);
        text_nav_info.setText(info);
    }

    /**
     * 从服务器中获取天气数据
     * @param weatherId
     * */
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
                        Toast.makeText(MainActivity.this, "获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
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
                                    .getDefaultSharedPreferences(MainActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(MainActivity.this, "首次使用请点击天气设置位置",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
