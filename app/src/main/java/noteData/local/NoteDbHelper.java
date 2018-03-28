package noteData.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PErry on 2018/2/21.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;    //版本号
    private static final String DATABASE_NAME = "Notes.db";     //数据库名

    public static final String TABLE_NAME = "notes";    //表名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CONTENT = "content";

    private static final String SQL_CREATE_ENTRIES =        //建表
            "CREATE TABLE "+TABLE_NAME+"("+
                    COLUMN_ID+" TEXT PRIMARY KEY,"+
                    COLUMN_CONTENT+" TEXT"+
                    ");";

    public NoteDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
