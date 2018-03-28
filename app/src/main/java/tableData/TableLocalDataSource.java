package tableData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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
        values.put(TableDbHelper.COLUMN_DAY, course.day);
        values.put(TableDbHelper.COLUMN_START, course.start);
        values.put(TableDbHelper.COLUMN_CLASSNUM, course.classNum);

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
                TableDbHelper.COLUMN_DAY,
                TableDbHelper.COLUMN_START,
                TableDbHelper.COLUMN_CLASSNUM
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
                int day = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_DAY));
                int start = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_START));
                int classNum = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_CLASSNUM));

                course = new Course(id, name, room, teacher, day, start, classNum);
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
                TableDbHelper.COLUMN_DAY,
                TableDbHelper.COLUMN_START,
                TableDbHelper.COLUMN_CLASSNUM
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
                    int day = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_DAY));
                    int start = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_START));
                    int classNum = cursor.getInt(cursor.getColumnIndex(TableDbHelper.COLUMN_CLASSNUM));
                    Course course = new Course(id, name, room, teacher, day, start, classNum);
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
        values.put(TableDbHelper.COLUMN_DAY, course.day);
        values.put(TableDbHelper.COLUMN_START, course.start);
        values.put(TableDbHelper.COLUMN_CLASSNUM, course.classNum);

        String selection = TableDbHelper.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {course.id};

        database.update(TableDbHelper.TABLE_NAME, values, selection, selectionArgs);
        database.close();
    }

    @Override
    public void cacheEnable(boolean enable) {

    }
}
