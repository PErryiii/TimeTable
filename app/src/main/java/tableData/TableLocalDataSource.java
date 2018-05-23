package tableData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tableList.Table;

/**
 * Created by PErry on 2018/3/22.
 */

public class TableLocalDataSource implements TableDataSource {

    private static TableLocalDataSource INSTANCE = null;
    private TableDbHelper dbHelper;

    private TableLocalDataSource(Context context){
        dbHelper = new TableDbHelper(context);
    }

    /**
     * 单例
     * */
    public static TableLocalDataSource getINSTANCE(Context context){
        if (INSTANCE == null){
            INSTANCE = new TableLocalDataSource(context);
        }
        return INSTANCE;
    }

    /**
     * 保存课程表
     * */
    @Override
    public void saveCourse(Course course) {
        if (course == null){
            return;
        }
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableDbHelper.COLUMN_ID, course.id);
        values.put(TableDbHelper.COLUMN_NAME, course.name);
        values.put(TableDbHelper.COLUMN_ROOM, course.room);
        values.put(TableDbHelper.COLUMN_TEACHER, course.teacher);
        values.put(TableDbHelper.COLUMN_CLASSNUM, course.classNum);
        values.put(TableDbHelper.COLUMN_TIME, course.time);
        values.put(TableDbHelper.COLUMN_ALARMABLE, course.alarmAble);
        values.put(TableDbHelper.COLUMN_NOTIFYABLE, course.notifyAble);
        values.put(TableDbHelper.COLUMN_INFO, course.info);
        values.put(TableDbHelper.COLUMN_COLOR, course.color);

        database.insert(TableDbHelper.TABLE_NAME, null, values);
        database.close();
    }

    /**
     * 根据id删除课程表
     * */
    @Override
    public void deleteCourse(String courseId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String selection = TableDbHelper.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {courseId};

        database.delete(TableDbHelper.TABLE_NAME, selection, selectionArgs);
        database.close();
    }

    /**
     * 删除课程表
     * */
    @Override
    public void deleteTable() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(TableDbHelper.TABLE_NAME, null, null);
        database.close();
    }

    /**
     * 根据id查看课程
     * */
    @Override
    public void getCourse(String courseId, loadCourseCallback loadCourseCallback) {
        Course course = null;
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String queryColums[] = new String[]{
                TableDbHelper.COLUMN_ID,
                TableDbHelper.COLUMN_NAME,
                TableDbHelper.COLUMN_ROOM,
                TableDbHelper.COLUMN_TEACHER,
                TableDbHelper.COLUMN_CLASSNUM,
                TableDbHelper.COLUMN_TIME,
                TableDbHelper.COLUMN_ALARMABLE,
                TableDbHelper.COLUMN_NOTIFYABLE,
                TableDbHelper.COLUMN_INFO,
                TableDbHelper.COLUMN_COLOR
        };
        String selection = TableDbHelper.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {courseId};

        Cursor cursor = null;
        try {
            cursor = database.query(TableDbHelper.TABLE_NAME, queryColums, selection, selectionArgs,
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();

                String id = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_NAME));
                String room = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_ROOM));
                String teacher = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_TEACHER));
                int classNum = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_CLASSNUM));
                long time = cursor.getLong(cursor.getColumnIndex(TableDbHelper.COLUMN_TIME));
                boolean alarmAble = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_ALARMABLE))>0;
                boolean notifyAble = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_NOTIFYABLE))>0;
                String info = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_INFO));
                String color = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_COLOR));

                course = new Course(id, name, room, teacher, classNum, time, alarmAble, notifyAble,
                        info, color);
            }
        }catch (Exception e){
            loadCourseCallback.loadFailed();
        } finally {
            if (cursor != null){
                cursor.close();
            }
            database.close();
        }

        if (course == null){
            loadCourseCallback.loadFailed();
        } else {
            loadCourseCallback.loadSuccess(course);
        }
    }


    /**
     * 查看所有的课程
     * */
    @Override
    public void getTable(loadTableCallback loadTableCallback) {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String queryColums[] = new String[]{
                TableDbHelper.COLUMN_ID,
                TableDbHelper.COLUMN_NAME,
                TableDbHelper.COLUMN_ROOM,
                TableDbHelper.COLUMN_TEACHER,
                TableDbHelper.COLUMN_CLASSNUM,
                TableDbHelper.COLUMN_TIME,
                TableDbHelper.COLUMN_ALARMABLE,
                TableDbHelper.COLUMN_NOTIFYABLE,
                TableDbHelper.COLUMN_INFO,
                TableDbHelper.COLUMN_COLOR
        };

        Cursor cursor = null;
        try {
            cursor = database.query(TableDbHelper.TABLE_NAME, queryColums,
                    null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_NAME));
                    String room = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_ROOM));
                    String teacher = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_TEACHER));
                    int classNum = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_CLASSNUM));
                    long time = cursor.getLong(cursor.getColumnIndex(TableDbHelper.COLUMN_TIME));
                    boolean alarmAble = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_ALARMABLE))>0;
                    boolean notifyAble = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_NOTIFYABLE))>0;
                    String info = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_INFO));
                    String color = cursor.getString(cursor.getColumnIndex(TableDbHelper.COLUMN_COLOR));
                    Course course = new Course(id, name, room, teacher, classNum, time, alarmAble,
                            notifyAble, info, color);
                    courseList.add(course);
                }
            }
        } catch (Exception e){
            loadTableCallback.loadFailed();
        }finally {
            if (cursor != null){
                cursor.close();
            }
            database.close();
        }
        loadTableCallback.loadSuccess(courseList);

    }


    /**
     * 根据id更新课程
     * */
    @Override
    public void updateCourse(Course course) {
        if (course == null){
            return;
        }
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableDbHelper.COLUMN_ID, course.id);
        values.put(TableDbHelper.COLUMN_NAME, course.name);
        values.put(TableDbHelper.COLUMN_ROOM, course.room);
        values.put(TableDbHelper.COLUMN_TEACHER, course.teacher);
        values.put(TableDbHelper.COLUMN_CLASSNUM, course.classNum);
        values.put(TableDbHelper.COLUMN_TIME, course.time);
        values.put(TableDbHelper.COLUMN_ALARMABLE, course.alarmAble);
        values.put(TableDbHelper.COLUMN_NOTIFYABLE, course.notifyAble);
        values.put(TableDbHelper.COLUMN_INFO, course.info);
        values.put(TableDbHelper.COLUMN_COLOR, course.color);

        String selection = TableDbHelper.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {course.id};

        database.update(TableDbHelper.TABLE_NAME, values, selection, selectionArgs);
        database.close();
    }

    @Override
    public void cacheEnable(boolean enable) {

    }
}
