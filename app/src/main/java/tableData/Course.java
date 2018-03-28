package tableData;

import java.util.UUID;

/**
 * Created by PErry on 2018/3/21.
 */

public class Course {

    /**
     * @param String 课程名
     * @param String 教室
     * @param String 老师
     * @param int 开始上课的节数
     * @param int 上课节数
     * @param int 周几上课
     * @param int 课程颜色
     */
    public String id;
    public String name;
    public String room;
    public String teacher;
    public int start;
    public int classNum;
    public int day;
    public int color;

    public Course(String name, String room, String teacher, int day, int classNum, int start){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.room = room;
        this.teacher = teacher;
        this.start = start;
        this.classNum = classNum;
        this.day = day;
    }

    public Course(String id, String name, String room, String teacher, int day, int classNum, int start){
        this.id = id;
        this.name = name;
        this.room = room;
        this.teacher = teacher;
        this.start = start;
        this.classNum = classNum;
        this.day = day;
    }

}
