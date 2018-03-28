package noteData.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import noteData.NoteBean;
import noteData.NoteDataSource;

/**
 * Created by PErry on 2018/2/23.
 */

public class NoteLocalDataSource implements NoteDataSource {

    private static NoteLocalDataSource INSTANCE = null;
    private NoteDbHelper dbHelper;

    private NoteLocalDataSource(Context context) {
        dbHelper = new NoteDbHelper(context);
    }

    public static NoteLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NoteLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getNote(String noteId, loadNoteCallback callback) {
        NoteBean note = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String queryColums[] = new String[]{
                NoteDbHelper.COLUMN_ID,
                NoteDbHelper.COLUMN_CONTENT
        };
        String selection = NoteDbHelper.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {noteId};

        Cursor cursor = null;
        try {
            cursor = db.query(NoteDbHelper.TABLE_NAME, queryColums, selection, selectionArgs,
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                String id = cursor.getString(cursor.getColumnIndex(NoteDbHelper.COLUMN_ID));
                String content = cursor.getString(cursor.getColumnIndex(NoteDbHelper.COLUMN_CONTENT));
                note = new NoteBean(id, content);
            }
        } catch (Exception e){
            callback.loadFailed();
        } finally {
            if (cursor != null){
                cursor.close();
            }
            db.close();
        }

        if (note == null){
            callback.loadFailed();
        } else {
            callback.loadSuccess(note);
        }
    }

    @Override
    public void getNotes(loadNotesCallback callback) {

        List<NoteBean> noteList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String queryColums[] = {
                NoteDbHelper.COLUMN_ID,
                NoteDbHelper.COLUMN_CONTENT
        };

        Cursor cursor = null;
        try {
            cursor = db.query(NoteDbHelper.TABLE_NAME, queryColums,
                    null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex(NoteDbHelper.COLUMN_ID));
                    String content = cursor.getString(cursor.getColumnIndex(NoteDbHelper.COLUMN_CONTENT));
                    NoteBean note = new NoteBean(id, content);
                    noteList.add(note);
                }
            }
        } catch (Exception e){
            callback.loadFailed();
        } finally {
            if (cursor != null){
                cursor.close();
            }
            db.close();
        }
        callback.loadSuccess(noteList);
    }

    @Override
    public void saveNote(NoteBean note) {
        if (note == null){
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoteDbHelper.COLUMN_ID, note.id);
        values.put(NoteDbHelper.COLUMN_CONTENT, note.content);

        db.insert(NoteDbHelper.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void updateNote(NoteBean note) {
        if (note == null){
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoteDbHelper.COLUMN_ID, note.id);
        values.put(NoteDbHelper.COLUMN_CONTENT, note.content);

        String selection = NoteDbHelper.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {note.id};

        db.update(NoteDbHelper.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    @Override
    public void deleteNote(String noteId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = NoteDbHelper.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {noteId};

        db.delete(NoteDbHelper.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    @Override
    public void cacheEnable(boolean enable) {
        //无操作。改方法由NotesRepository管理
    }
}
