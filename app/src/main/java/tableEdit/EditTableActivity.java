package tableEdit;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.timetable_1.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import org.angmarch.views.NiceSpinner;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import tableData.Course;
import tableData.TableDataSource;
import tableData.TableLocalDataSource;

public class EditTableActivity extends AppCompatActivity {

    private TableLocalDataSource tableLocalDataSource;

    private Toolbar toolbar_editTable_title;
    private TextView text_editTable_title;
    private ImageButton btn_editTable_save;
    private MaterialEditText editText_course_name;
    private MaterialEditText editText_course_room;
    private MaterialEditText editText_course_teacher;
    private NiceSpinner spinner_course_day;
    private NiceSpinner spinner_course_start;
    private NiceSpinner spinner_course_classNum;

    private boolean isNewCourse;
    public static final String COURSE_ID = "courseId";
    private String courseId;

    Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_table_activity);
        bindView();
        tableLocalDataSource = TableLocalDataSource.getINSTANCE(this);

        courseId = getIntent().getStringExtra(COURSE_ID);
        if (TextUtils.isEmpty(courseId)){
            isNewCourse = true;
            text_editTable_title.setText("新建课程");
        } else {
            isNewCourse = false;
        }

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

        text_editTable_title = findViewById(R.id.text_editTable_title);


        /**
         * 返回table
         * */
        btn_editTable_save = findViewById(R.id.btn_editTable_save);
        btn_editTable_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCourse();
                finish();
            }
        });

        editText_course_name = findViewById(R.id.editText_course_name);
        editText_course_room = findViewById(R.id.editText_course_room);
        editText_course_teacher = findViewById(R.id.editText_course_teacher);

        spinner_course_day = findViewById(R.id.spinner_course_day);
        List<String> dataDay = new LinkedList<>(Arrays.asList("一", "二", "三", "四",
                "五", "六", "日"));
        spinner_course_day.attachDataSource(dataDay);

        spinner_course_start = findViewById(R.id.spinner_course_start);
        List<String> dataStart = new LinkedList<>(Arrays.asList("1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12"));
        spinner_course_start.attachDataSource(dataStart);

        spinner_course_classNum = findViewById(R.id.spinner_course_classNum);
        List<String> dataClassNum = new LinkedList<>(Arrays.asList("1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12"));
        spinner_course_classNum.attachDataSource(dataClassNum);
    }

    private void saveCourse(){
        String name = editText_course_name.getText().toString();
        String room = editText_course_room.getText().toString();
        String teacher = editText_course_teacher.getText().toString();
        int day = spinner_course_day.getSelectedIndex() + 1;
        int start = spinner_course_start.getSelectedIndex() + 1;
        int classNum = spinner_course_classNum.getSelectedIndex() + 1;

        if (isNewCourse){
            course = new Course(name, room, teacher, day, start, classNum);
            tableLocalDataSource.saveCourse(course);
        } else {
            course = new Course(courseId, name, room, teacher, day, start, classNum);
            tableLocalDataSource.updateCourse(course);
        }
    }

    private void loadCourse(){
        if (isNewCourse){
            return;
        }
        tableLocalDataSource.getCourse(courseId, new TableDataSource.loadCourseCallback() {
            @Override
            public void loadSuccess(Course course) {
                editText_course_name.setText(course.name);
                editText_course_room.setText(course.room);
                editText_course_teacher.setText(course.teacher);
                spinner_course_day.setSelectedIndex(course.day - 1);
                spinner_course_start.setSelectedIndex(course.start - 1);
                spinner_course_classNum.setSelectedIndex(course.classNum - 1);
            }

            @Override
            public void loadFailed() {

            }
        });
    }

}
