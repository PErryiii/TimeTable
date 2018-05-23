package tableData;

/**
 * Created by PErry on 2018/3/21.
 */

public class Course {

    public String id;
    public String name;
    public String room;
    public String teacher;
    public int classNum;
    public long time;
    public boolean alarmAble;
    public boolean notifyAble;
    public String info;
    public String color;

    public Course(String id){
        this.id = id;
    }

    public Course(String id, String name, String room, String teacher,int classNum,
                  long time, boolean alarmAble, boolean notifyAble, String info,
                  String color){
        this.id = id;
        this.name = name;
        this.room = room;
        this.teacher = teacher;
        this.classNum = classNum;
        this.time = time;
        this.alarmAble = alarmAble;
        this.notifyAble = notifyAble;
        this.info = info;
        this.color = color;
    }

}
