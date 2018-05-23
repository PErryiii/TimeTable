package tableList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.timetable_1.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
    private TableAdapter adapter;
    List<Course> dataList = new ArrayList<>();

    private RecyclerView recyclerView_table_content;

    /*星期栏*/
    private TextView text_table_start;
    private TextView text_table_monday;
    private TextView text_table_tuesday;
    private TextView text_table_wednesday;
    private TextView text_table_thursday;
    private TextView text_table_friday;
    private TextView text_table_saturday;
    private TextView text_table_sunday;

    View view;

    public Table(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.table, container, false);

        tableLocalDataSource = TableLocalDataSource.getINSTANCE(getActivity());

        /**
         * 判断数据库是否为空，为空则创建一张空课表
         * */
        loadTable();
        if (dataList.size() == 0){
            for (int i = 0; i < 84; i++){
                Course course = new Course(Integer.toString(i));
                tableLocalDataSource.saveCourse(course);
            }
            Course course = new Course("24", "你好", "请点击添加", "",
                    0, 0, false, false,  "",
                    "#ffa726");
            tableLocalDataSource.updateCourse(course);
        }

        bindView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TGA,"onResume");
        loadTable();
        adapter.updateData(dataList);
    }

    /**
     * 配置recyclerView_table_content
     * */
    private void bindView(){
        recyclerView_table_content = view.findViewById(R.id.recyclerView_table_content);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(7,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView_table_content.setLayoutManager(layoutManager);
        loadTable();
        adapter = new TableAdapter(dataList, onTableItemOnClickListener);
        recyclerView_table_content.setAdapter(adapter);

        /*初始化星期栏控件*/
        text_table_start = view.findViewById(R.id.text_table_start);
        text_table_monday = view.findViewById(R.id.text_table_monday);
        text_table_tuesday = view.findViewById(R.id.text_table_tuesday);
        text_table_wednesday = view.findViewById(R.id.text_table_wednesday);
        text_table_thursday = view.findViewById(R.id.text_table_thursday);
        text_table_friday = view.findViewById(R.id.text_table_friday);
        text_table_saturday = view.findViewById(R.id.text_table_saturday);
        text_table_sunday = view.findViewById(R.id.text_table_sunday);

        /*设置星期栏时间*/
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);    //月份
        text_table_start.setText(month + "   月");

        int weekday = calendar.get(Calendar.DAY_OF_WEEK);    //周时间
        int day = calendar.get(Calendar.DAY_OF_MONTH);      //当前月份的日期号码
        /*前六天的日期*/
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int day_less_1 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int day_less_2 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int day_less_3 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int day_less_4 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int day_less_5 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int day_less_6 = calendar.get(Calendar.DAY_OF_MONTH);
        /*后六天的日期*/
        calendar.add(Calendar.DAY_OF_MONTH, +7);
        int day_plus_1 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        int day_plus_2 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        int day_plus_3 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        int day_plus_4 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        int day_plus_5 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        int day_plus_6 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, -6);        //恢复正常的日期
        switch (weekday){   //根据周时间设置当前月份的日期号码
            case 2:
                text_table_monday.setText("周一       " + day + "日");
                text_table_monday.setBackgroundResource(R.color.colorYellow300);
                text_table_tuesday.setText("周二       " + day_plus_1 + "日");
                text_table_wednesday.setText("周三       " + day_plus_2 + "日");
                text_table_thursday.setText("周四       " + day_plus_3 + "日");
                text_table_friday.setText("周五       " + day_plus_4 + "日");
                text_table_saturday.setText("周六       " + day_plus_5 + "日");
                text_table_sunday.setText("周日       " + day_plus_6 + "日");
                break;
            case 3:
                text_table_monday.setText("周一       " + day_less_1 + "日");
                text_table_tuesday.setText("周二       " + day + "日");
                text_table_tuesday.setBackgroundResource(R.color.colorYellow300);
                text_table_wednesday.setText("周三       " + day_plus_1 + "日");
                text_table_thursday.setText("周四       " + day_plus_2 + "日");
                text_table_friday.setText("周五       " + day_plus_3 + "日");
                text_table_saturday.setText("周六       " + day_plus_4 + "日");
                text_table_sunday.setText("周日       " + day_plus_5 + "日");
                break;
            case 4:
                text_table_monday.setText("周一       " + day_less_2 + "日");
                text_table_tuesday.setText("周二       " + day_less_1 + "日");
                text_table_wednesday.setText("周三       " + day + "日");
                text_table_wednesday.setBackgroundResource(R.color.colorYellow300);
                text_table_thursday.setText("周四       " + day_plus_1 + "日");
                text_table_friday.setText("周五       " + day_plus_2 + "日");
                text_table_saturday.setText("周六       " + day_plus_3 + "日");
                text_table_sunday.setText("周日       " + day_plus_4 + "日");
                break;
            case 5:
                text_table_monday.setText("周一       " + day_less_3 + "日");
                text_table_tuesday.setText("周二       " + day_less_2 + "日");
                text_table_wednesday.setText("周三       " + day_less_1 + "日");
                text_table_thursday.setText("周四       " + day + "日");
                text_table_thursday.setBackgroundResource(R.color.colorYellow300);
                text_table_friday.setText("周五       " + day_plus_1 + "日");
                text_table_saturday.setText("周六       " + day_plus_2 + "日");
                text_table_sunday.setText("周日       " + day_plus_3 + "日");
                break;
            case 6:
                text_table_monday.setText("周一       " + day_less_4 + "日");
                text_table_tuesday.setText("周二       " + day_less_3 + "日");
                text_table_wednesday.setText("周三       " + day_less_2 + "日");
                text_table_thursday.setText("周四       " + day_less_1 + "日");
                text_table_friday.setText("周五       " + day + "日");
                text_table_friday.setBackgroundResource(R.color.colorYellow300);
                text_table_saturday.setText("周六       " + day_plus_1 + "日");
                text_table_sunday.setText("周日       " + day_plus_2 + "日");
                break;
            case 7:
                text_table_monday.setText("周一       " + day_less_5 + "日");
                text_table_tuesday.setText("周二       " + day_less_4 + "日");
                text_table_wednesday.setText("周三       " + day_less_3 + "日");
                text_table_thursday.setText("周四       " + day_less_2 + "日");
                text_table_friday.setText("周五       " + day_less_1 + "日");
                text_table_saturday.setText("周六       " + day + "日");
                text_table_saturday.setBackgroundResource(R.color.colorYellow300);
                text_table_sunday.setText("周日       " + day_plus_1 + "日");
                break;
            case 1:
                text_table_monday.setText("周一       " + day_less_6 + "日");
                text_table_tuesday.setText("周二       " + day_less_5 + "日");
                text_table_wednesday.setText("周三       " + day_less_4 + "日");
                text_table_thursday.setText("周四       " + day_less_3 + "日");
                text_table_friday.setText("周五       " + day_less_2 + "日");
                text_table_saturday.setText("周六       " + day_less_1 + "日");
                text_table_sunday.setText("周日       " + day + "日");
                text_table_sunday.setBackgroundResource(R.color.colorYellow300);
                break;
        }
    }

    /**
     * tableItem的点击事件
     * */
    TableAdapter.onTableItemOnClickListener onTableItemOnClickListener = new
            TableAdapter.onTableItemOnClickListener() {
        @Override
        public void onItemClick(Course course) {
            Intent intent = new Intent(getActivity(), EditTableActivity.class);
            intent.putExtra(EditTableActivity.COURSE_ID, course.id);
            startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(View view, final Course course) {
            final AlertDialog alertDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("警告");
            builder.setMessage("是否删除");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Course course1 = new Course(course.id);
                                tableLocalDataSource.updateCourse(course1);
                                loadTable();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    adapter.updateData(dataList);
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
    };

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取课程表数据
     * */
    private void loadTable(){
        tableLocalDataSource.getTable(new TableDataSource.loadTableCallback() {
            @Override
            public void loadSuccess(List<Course> courseList) {
                dataList.clear();
                for (Course course : courseList){
                    dataList.add(course);
                }
            }

            @Override
            public void loadFailed() {

            }
        });
    }

}
