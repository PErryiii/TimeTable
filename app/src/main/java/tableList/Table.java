package tableList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.timetable_1.R;
import java.util.List;
import tableData.Course;
import tableData.TableDataSource;
import tableData.TableLocalDataSource;
import tableEdit.EditTableActivity;


/**
 * Created by PErry on 2018/2/7.
 */

public class Table extends android.support.v4.app.Fragment{
    private static String TGA = "Table";

    private TableLocalDataSource tableLocalDataSource;

    RelativeLayout layout_whichDay;
    View view;

    public Table(){

    }

    @Override
    public void onStart() {
        super.onStart();
        loadTable();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.table, container, false);

        tableLocalDataSource = TableLocalDataSource.getINSTANCE(getActivity());

        return view;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 创建课程
     * */
    private void createCourseView(final Course course){

        int height = dip2px(getActivity(), 60);      // CourseView的高度
        int day = course.day;      // 哪一天上课
        if (day < 1 || day > 7){
            Toast.makeText(getActivity(), "上课时间错误，请重新输入", Toast.LENGTH_SHORT).show();
        } else {
            switch (day){       // 获取上课那天的layout
                case 1:
                    layout_whichDay = view.findViewById(R.id.layout_table_monday);
                    break;
                case 2:
                    layout_whichDay = view.findViewById(R.id.layout_table_tuesday);
                    break;
                case 3:
                    layout_whichDay = view.findViewById(R.id.layout_table_wednesday);
                    break;
                case 4:
                    layout_whichDay = view.findViewById(R.id.layout_table_thursday);
                    break;
                case 5:
                    layout_whichDay = view.findViewById(R.id.layout_table_friday);
                    break;
                case 6:
                    layout_whichDay = view.findViewById(R.id.layout_table_saturday);
                    break;
                case 7:
                    layout_whichDay = view.findViewById(R.id.layout_table_sunday);
                    break;
                default:
                    break;
            }
            // 获取课程View
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.course_view,
                    null);
            // 设置课程开始的高度
            view.setY(height * (course.start - 1));
            // 设置课程布局高度 : 单个课程高度 * 课程节数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT,
                            height * course.classNum);
            view.setLayoutParams(params);
            // 设置课程View中的text : 课程名 + 教室
            TextView text = view.findViewById(R.id.text_table_course);
            text.setText(course.name + "\n" + course.room);
            layout_whichDay.addView(view);

            //设置课程View的点击事件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditTableActivity.class);
                    intent.putExtra(EditTableActivity.COURSE_ID, course.id);
                    startActivity(intent);
                }
            });
            // 设置课程View的长按事件
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog alertDialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("警告");
                    builder.setMessage("是否删除");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            view.setVisibility(View.GONE);      // 先隐藏
                            layout_whichDay.removeView(view);
                            tableLocalDataSource.deleteCourse(course.id);
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
                    return true;
                }
            });
        }
    }


    /*加载课程表*/
    private void loadTable(){
        tableLocalDataSource.getTable(new TableDataSource.loadTableCallback() {
            @Override
            public void loadSuccess(List<Course> courseList) {
                for (Course course : courseList){
                    createCourseView(course);
                }
            }

            @Override
            public void loadFailed() {

            }
        });
    }


}
