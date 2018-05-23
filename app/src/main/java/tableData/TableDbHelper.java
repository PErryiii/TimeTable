package tableData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by PErry on 2018/3/21.
 */

public class TableDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;    //版本号
    private static final String DATABASE_NAME = "Table.db";     //数据库名

    public static final String TABLE_NAME = "course";    //表名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_CLASSNUM = "classNum";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_ALARMABLE = "alarmAble";
    public static final String COLUMN_NOTIFYABLE = "notifyAble";
    public static final String COLUMN_INFO = "info";
    public static final String COLUMN_COLOR = "color";

    private static final String SQL_CREATE_ENTRIES =        //建表
            "CREATE TABLE "+TABLE_NAME+" ("+
                    COLUMN_ID+" TEXT PRIMARY KEY, "+
                    COLUMN_NAME+" TEXT, "+
                    COLUMN_ROOM+" TEXT, "+
                    COLUMN_TEACHER+" TEXT, "+
                    COLUMN_CLASSNUM+" INTEGER, "+
                    COLUMN_TIME+" INTEGER, "+
                    COLUMN_ALARMABLE+" BIT, "+
                    COLUMN_NOTIFYABLE+" BIT, "+
                    COLUMN_INFO+" TEXT, "+
                    COLUMN_COLOR+" TEXT);";

    public TableDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    private Context mcontext;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("tableDbHelper", "create success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
