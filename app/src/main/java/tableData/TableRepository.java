package tableData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PErry on 2018/3/23.
 */

public class TableRepository implements TableDataSource {

    private static TableRepository INSTANCE = null;
    private Map<String, Course> tableCache;      //数据缓存，key为id
    private boolean cacheEnable = false;        //是否可以缓存

    private TableDataSource tableDataSource;

    private TableRepository(TableDataSource tableDataSource){
        this.tableDataSource = tableDataSource;
    }

    /**
     * 单例
     * */
    public static TableRepository getINSTANCE(TableDataSource tableDataSource){
        if (INSTANCE == null){
            INSTANCE = new TableRepository(tableDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void saveCourse(Course course) {
        tableDataSource.saveCourse(course);
        if (tableCache == null){
            tableCache = new LinkedHashMap<>();
        }
        tableCache.put(course.id, course);
    }

    @Override
    public void deleteCourse(String courseId) {
        tableDataSource.deleteCourse(courseId);
        if (tableCache == null){
            tableCache = new LinkedHashMap<>();
        }
        tableCache.remove(courseId);
    }

    @Override
    public void getCourse(final String courseId, final loadCourseCallback loadCourseCallback) {
        if (tableCache != null && cacheEnable){     // 直接从缓存中获取
            Course course = getCourseFromCache(courseId);
            if (course != null){
                loadCourseCallback.loadSuccess(course);
                return;
            }
        }
        tableDataSource.getCourse(courseId, new loadCourseCallback() {      // 从数据库中获取
            @Override
            public void loadSuccess(Course course) {
                if (tableCache == null){
                    tableCache = new LinkedHashMap<>();
                }
                tableCache.put(courseId, course);
                loadCourseCallback.loadSuccess(course);
            }

            @Override
            public void loadFailed() {
                loadCourseCallback.loadFailed();
            }
        });
    }

    @Override
    public void getTable(final loadTableCallback loadTableCallback) {
        if (tableCache != null && cacheEnable){
            loadTableCallback.loadSuccess(new ArrayList<>(tableCache.values()));
            return;
        }
        tableDataSource.getTable(new loadTableCallback() {
            @Override
            public void loadSuccess(List<Course> courseList) {
                refreshTable(courseList);
                loadTableCallback.loadSuccess(courseList);
            }

            @Override
            public void loadFailed() {
                loadTableCallback.loadFailed();
            }
        });
    }

    @Override
    public void updateCourse(Course course) {
        tableDataSource.updateCourse(course);
        if (tableCache == null){
            tableCache = new LinkedHashMap<>();
        }
        Course newCourse = new Course(course.id, course.name, course.room, course.teacher,
                course.day, course.start, course.classNum);
        tableCache.put(newCourse.id, newCourse);
    }

    @Override
    public void cacheEnable(boolean enable) {
        this.cacheEnable = enable;
    }

    /**
     * 从缓存中读取课程
     * @param courseId
     * @return course
     * */
    private Course getCourseFromCache(String courseId){
        if (tableCache == null || tableCache.isEmpty()){
            return null;
        }else {
            return tableCache.get(courseId);
        }
    }

    /**
     * 从缓存中读取全部课程
     * */
    private void refreshTable(List<Course> courseList){
        if (tableCache == null){
            tableCache = new LinkedHashMap<>();
        }
        tableCache.clear();
        for (Course course : courseList){
            tableCache.put(course.id, course);
        }
        cacheEnable = true;
    }
}
