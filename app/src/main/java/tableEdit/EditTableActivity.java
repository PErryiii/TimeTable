package tableEdit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    public static final String COURSE_ID = "courseId";
    private String courseId;
    private Calendar calendar = Calendar.getInstance();

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
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        text_course_time.setText("时间      " + hourOfDay + ":" + minute);
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
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
        if (notifyAble){
            //01.计算闹钟的时间
            //现在的时间
            final Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            calendar1.setTimeInMillis(System.currentTimeMillis());
            int currentHour = calendar1.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000;
            int currentMinute = calendar1.get(Calendar.MINUTE) * 60 * 1000;
            int currentTime = currentHour + currentMinute;
            //闹钟的小时和分钟
            int alarmHour = calendar.get(Calendar.HOUR_OF_DAY)  * 60 * 60 * 1000;
            int alarmMinute = calendar.get(Calendar.MINUTE) * 60 * 1000;
            int alarmTime = alarmHour + alarmMinute;
            //相差的天数的毫秒数
            int weekDay = calendar1.get(Calendar.DAY_OF_WEEK) - 1;   //现在周几
            int courseTime = Integer.parseInt(courseId) + 1;    //课程周几
            int day = courseTime%7 - weekDay;   //相差的天数
            int dayTime = 60 * 60 * 1000 * 24 * day;
            //相差时间的毫秒数
            int aWeek = 60 * 60 * 1000 * 24 * 7;
            //02.判断是否加一周
            int triggerTime = 0;
            if (dayTime > 0){
                triggerTime = alarmTime - currentTime + dayTime;
            } else if (dayTime < 0){
                triggerTime = alarmTime - currentTime + dayTime + aWeek;
            } else {
                if (alarmTime > currentTime){
                    triggerTime = alarmTime - currentTime;
                } else {
                    triggerTime = alarmTime - currentTime + aWeek;
                }
            }
            long realTime = System.currentTimeMillis() + triggerTime;
            /*设置服务*/
            //03.实例化闹钟
            manager =(AlarmManager) getSystemService(ALARM_SERVICE);
            //05.时间一到，执行相应操作
            Intent intent = new Intent();
            intent.putExtra("courseId", Integer.parseInt(courseId));
            intent.setAction("tableEdit.RING");
            pendingIntent = PendingIntent.getBroadcast(this,
                    Integer.parseInt(courseId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //04.设置闹钟
            manager.setExact(AlarmManager.RTC_WAKEUP, realTime, pendingIntent);
        } else if (!notifyAble){
            manager.cancel(pendingIntent);
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
