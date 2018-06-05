package tableEdit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.timetable_1.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import tableData.Course;
import tableData.TableDataSource;
import tableData.TableLocalDataSource;

public class EditTableActivity extends AppCompatActivity {

    private TableLocalDataSource tableLocalDataSource;

    private Toolbar toolbar_editTable_title;
    private ImageButton btn_editTable_delete;
    private MaterialEditText editText_course_name;
    private MaterialEditText editText_course_room;
    private MaterialEditText editText_course_teacher;
    private AppCompatSpinner spinner_editTable_classNum;
    private TextView text_course_time;
    private SwitchCompat switch_course_alarmAble;
    private SwitchCompat switch_course_notifyAble;
    private MaterialEditText editText_course_info;
    private FloatingActionButton fab_editTable_save;
    //时间
    public static final String COURSE_ID = "courseId";
    private String courseId;
    private Calendar calendar = Calendar.getInstance();
    //闹钟和通知
    private AlarmManager manager;
    private PendingIntent pendingIntent;

    Course course;

    /**
     * 课程表颜色
     * */
    private String RED = "#ef5350";
    private String PINK = "#ec407a";
    private String PURPLE = "#ab47bc";
    private String DEEP_PURPLE = "#7e57c2";
    private String INDIGO = "#ab47bc";
    private String BLUE = "#42a5f5";
    private String CYAN = "#26c6da";
    private String Teal = "#26a69a";
    private String GREEN = "#66bb6a";
    private String ORANGE = "#ffa726";
    private String DEEP_ORANGE = "#ff7043";
    private String BROWN = "#8d6e63";
    private String BLUE_GREY = "#78909c";
    private String[] colorList = {RED, PINK, PURPLE, DEEP_PURPLE, INDIGO, BLUE, CYAN,
            Teal, GREEN, ORANGE, DEEP_ORANGE, BROWN, BLUE_GREY};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_table_activity);
        bindView();
        tableLocalDataSource = TableLocalDataSource.getINSTANCE(this);

        courseId = getIntent().getStringExtra(COURSE_ID);
        loadCourse();
    }

    private void bindView(){
        toolbar_editTable_title = findViewById(R.id.toolbar_editTable_title);
        setSupportActionBar(toolbar_editTable_title);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_36x36);
        }


        /**
         * 删除课程
         * */
        btn_editTable_delete = findViewById(R.id.btn_editTable_delete);
        btn_editTable_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Course course = new Course(courseId);
                tableLocalDataSource.updateCourse(course);
                //取消闹钟
                Intent intent = new Intent("tableEdit.RING");
                pendingIntent = PendingIntent.getBroadcast(EditTableActivity.this,
                        Integer.parseInt(courseId), intent, PendingIntent.FLAG_NO_CREATE);
                if (pendingIntent != null){
                    AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
                    alarm.cancel(pendingIntent);
                }
                finish();
            }
        });

        /**
         * 保存课程
         * */
        fab_editTable_save = findViewById(R.id.fab_editTable_save);
        fab_editTable_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCourse();
                finish();
            }
        });

        /**
         * 设置时间
         * */
        text_course_time = findViewById(R.id.text_course_time);
        text_course_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(EditTableActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        text_course_time.setText("时间      " + hourOfDay + ":" + minute);
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Toast.makeText(EditTableActivity.this,
                                "设置了闹钟或通知记得保存哦~", Toast.LENGTH_SHORT).show();
                    }
                }, hour, minute, true).show();
            }
        });



        editText_course_name = findViewById(R.id.editText_course_name);
        editText_course_room = findViewById(R.id.editText_course_room);
        editText_course_teacher = findViewById(R.id.editText_course_teacher);
        spinner_editTable_classNum = findViewById(R.id.spinner_editTable_classNum);
        switch_course_alarmAble = findViewById(R.id.switch_course_alarmAble);
        switch_course_notifyAble = findViewById(R.id.switch_course_notifyAble);
        editText_course_info = findViewById(R.id.editText_course_info);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
                default:
                    break;
        }
        return true;
    }

    private void saveCourse(){
        Random random = new Random();
        int colorNum = random.nextInt(13);
        String name = editText_course_name.getText().toString();
        String room = editText_course_room.getText().toString();
        String teacher = editText_course_teacher.getText().toString();
        int classNum = spinner_editTable_classNum.getSelectedItemPosition();
        long time = calendar.getTimeInMillis();
        boolean alarmAble = switch_course_alarmAble.isChecked();
        boolean notifyAble = switch_course_notifyAble.isChecked();
        String info = editText_course_info.getText().toString();
        String color = colorList[colorNum];

        /*处理多节课事件*/
        if (classNum > 0){
            for (int i = 0; i <= classNum; i++){
                int id = Integer.parseInt(courseId);
                id = id + i * 7;
                course = new Course(Integer.toString(id),
                        name, room, teacher, classNum-i, time, alarmAble,
                        notifyAble, info, color);
                tableLocalDataSource.updateCourse(course);
            }
        } else{
            course = new Course(courseId, name, room, teacher, classNum, time,
                    alarmAble, notifyAble, info, color);
            tableLocalDataSource.updateCourse(course);
        }

        /*存储课程信息*/
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(EditTableActivity.this)
                .edit();
        int realClassNum = classNum + 1;
        String courseInfo = name + "#" +  room + "#" + teacher + "#" + realClassNum;
        editor.putString(courseId, courseInfo);
        editor.apply();

        /*处理闹钟和通知*/
        if (notifyAble || alarmAble){
            //01.计算闹钟的时间
            //现在的时间
            final Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            calendar1.setTimeInMillis(System.currentTimeMillis());
            long currentHour = calendar1.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000;
            long currentMinute = calendar1.get(Calendar.MINUTE) * 60 * 1000;
            long currentTime = currentHour + currentMinute;
            //闹钟的小时和分钟
            long alarmHour = calendar.get(Calendar.HOUR_OF_DAY)  * 60 * 60 * 1000;
            long alarmMinute = calendar.get(Calendar.MINUTE) * 60 * 1000;
            long alarmTime = alarmHour + alarmMinute;
            //相差的天数的毫秒数
            int weekDay = calendar1.get(Calendar.DAY_OF_WEEK) - 1;   //现在周几
            int courseTime = Integer.parseInt(courseId) + 1;    //课程周几
            int day = courseTime%7 - weekDay;   //相差的天数
            long dayTime = 60 * 60 * 1000 * 24 * day;
            //相差时间的毫秒数
            long aWeek = 60 * 60 * 1000 * 24 * 7;
            //02.判断是否加一周
            long triggerTime = 0;
            if (day > 0){       //天数大于0，则直接加上天数
                triggerTime = alarmTime - currentTime + dayTime;
            } else {    //天数小于1，则判断是否需要加一周
                if (alarmTime > currentTime){
                    triggerTime = alarmTime - currentTime;
                } else {
                    triggerTime = alarmTime - currentTime + aWeek;
                }
            }
            Calendar calendar2 = Calendar.getInstance();        //用于将秒钟归零
            calendar2.setTimeInMillis(System.currentTimeMillis());
            calendar2.set(Calendar.SECOND, 0);      //归零
            long realTime = calendar2.getTimeInMillis() + triggerTime;
            //03.实例化闹钟
            manager =(AlarmManager) getSystemService(ALARM_SERVICE);
            //04.根据闹钟开关是否打开进行相应操作
            if (alarmAble){
                //2.时间一到，执行相应操作
                Intent intent = new Intent("tableEdit.RING");

                intent.putExtra("courseId", Integer.parseInt(courseId));
                pendingIntent = PendingIntent.getBroadcast(this,
                        Integer.parseInt(courseId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //1.设置闹钟
                if (Build.VERSION.SDK_INT >= 19){    //API19以上
                    manager.setExact(AlarmManager.RTC_WAKEUP, realTime, pendingIntent);
                    //处理显示的alarmInfo
                    String alarmInfo = "已将闹钟设置为从现在起";
                    double toastTime = triggerTime/ 60000;     //换成分钟
                    int toastDay;
                    int toastHour;
                    int toastMinute;
                    if (toastTime < 60){    //如果小于一小时，则直接显示分钟
                        alarmInfo += (int)toastTime + "分钟";
                    } else {    //如果大于一小时，则换算成小时+分钟
                        toastMinute = (int) toastTime % 60;   //分钟
                        toastHour = (int) toastTime / 60;     //小时
                        if (toastHour < 24){        //如果小时小于24小时，则显示小时+分钟
                            alarmInfo += toastHour + "小时" + toastMinute + "分钟";
                        } else {        //如果小时大于24，则显示天+小时+分钟
                            toastDay = toastHour / 24;      //天
                            toastHour = toastHour % 24;      //小时
                            alarmInfo += toastDay + "天" + toastHour + "小时" + toastMinute + "分钟";
                        }
                    }
                    alarmInfo += "后提醒。";
                    Toast.makeText(this, alarmInfo, Toast.LENGTH_SHORT).show();
                } else {    //API19以下
                    manager.set(AlarmManager.RTC_WAKEUP, realTime, pendingIntent);
                    String alarmInfo = "闹钟将在" + alarmTime +"分钟后响起！";
                    Toast.makeText(this, alarmInfo, Toast.LENGTH_SHORT).show();
                }
            } else if (!alarmAble){     //取消闹钟
                Intent intent = new Intent("tableEdit.RING");
                pendingIntent = PendingIntent.getBroadcast(this,
                        Integer.parseInt(courseId), intent, PendingIntent.FLAG_NO_CREATE);
                AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
                alarm.cancel(pendingIntent);
            }
        }
    }

    private void loadCourse(){
        tableLocalDataSource.getCourse(courseId, new TableDataSource.loadCourseCallback() {
            @Override
            public void loadSuccess(Course course) {
                editText_course_name.setText(course.name);
                editText_course_name.requestFocus();
                editText_course_name.setSelection(editText_course_name.getText().length());

                editText_course_room.setText(course.room);
                editText_course_teacher.setText(course.teacher);
                spinner_editTable_classNum.setSelection(course.classNum);

                if (course.time != 0){  //初始化时间
                    calendar.setTimeInMillis(course.time);
                } else {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                }
                text_course_time.setText("时间      " + calendar.get(Calendar.HOUR_OF_DAY)
                        + ":" + calendar.get(Calendar.MINUTE));
                switch_course_alarmAble.setChecked(course.alarmAble);
                switch_course_notifyAble.setChecked(course.notifyAble);
                editText_course_info.setText(course.info);
            }

            @Override
            public void loadFailed() {

            }
        });
    }

}
