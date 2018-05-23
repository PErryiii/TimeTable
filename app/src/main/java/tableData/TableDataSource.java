package tableData;

import java.util.List;

/**
 * Created by PErry on 2018/3/21.
 */

public interface TableDataSource {

    interface loadCourseCallback{
        void loadSuccess(Course course);
        void loadFailed();
    }

    interface loadTableCallback{
        void loadSuccess(List<Course> courseList);
        void loadFailed();
    }

    void saveCourse(Course course);
    void deleteCourse(String courseId);
    void deleteTable();
    void getCourse(String courseId, loadCourseCallback loadCourseCallback);
    void getTable(loadTableCallback loadTableCallback);
    void updateCourse(Course course);
    void cacheEnable(boolean enable);
}
