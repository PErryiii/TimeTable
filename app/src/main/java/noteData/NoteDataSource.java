package noteData;

import java.util.List;

/**
 * Created by PErry on 2018/2/15.
 */

public interface NoteDataSource {       //数据接口NoteDataSource

    interface loadNoteCallback{     //获取单个数据的回调
        void loadSuccess(NoteBean note);
        void loadFailed();
    }

    interface loadNotesCallback{        //获取全部数据的回调
        void loadSuccess(List<NoteBean> noteList);
        void loadFailed();
    }

    void getNote(String noteId, loadNoteCallback callback);    //获取指定id的数据

    void getNotes(loadNotesCallback callback);        //获取全部数据

    void saveNote(NoteBean note);       //保存数据

    void updateNote(NoteBean note);     //更新数据

    void deleteNote(String noteId);     //删除指定id的数据

    void cacheEnable(boolean enable); //缓存是否可用（如果有)
}
