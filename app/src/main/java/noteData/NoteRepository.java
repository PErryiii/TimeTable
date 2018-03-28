package noteData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PErry on 2018/2/23.
 * MVP之Model实现类
 * 管理数据处理
 * 单例
 */

public class NoteRepository implements NoteDataSource {

    private static NoteRepository INSTANCE = null;
    private Map<String, NoteBean> noteCache;    //数据缓存，key为id
    private boolean cacheEnable = false;    //是否能从缓存中获取数据

    private NoteDataSource noteDataSource;//从数据库获取数据的Model

    private NoteRepository(NoteDataSource noteDataSource){
        this.noteDataSource = noteDataSource;
    }

    public static NoteRepository getINSTANCE(NoteDataSource noteDataSource){        //单例
        if (INSTANCE == null){
            INSTANCE = new NoteRepository(noteDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void getNote(final String noteId, final loadNoteCallback callback) {
        if (noteCache != null && cacheEnable){  //直接从缓存中获取note
            NoteBean note = getNoteFromCacheById(noteId);
            if (note != null){
                callback.loadSuccess(noteCache.get(noteId));
                return;
            }
        }
        noteDataSource.getNote(noteId, new loadNoteCallback() {    //从数据库中获取note
            @Override
            public void loadSuccess(NoteBean note) {
                if (noteCache == null){
                    noteCache = new LinkedHashMap<>();
                }
                noteCache.put(noteId, note);
                callback.loadSuccess(note);
            }

            @Override
            public void loadFailed() {
                callback.loadFailed();
            }
        });
    }

    @Override
    public void getNotes(final loadNotesCallback callback) {

        if (noteCache != null && cacheEnable){
            callback.loadSuccess(new ArrayList<>(noteCache.values()));
            return;
        }
        noteDataSource.getNotes(new loadNotesCallback() {
            @Override
            public void loadSuccess(List<NoteBean> noteList) {
                refreshCache(noteList);
                callback.loadSuccess(noteList);
            }

            @Override
            public void loadFailed() {
                callback.loadFailed();
            }
        });

    }

    @Override
    public void saveNote(NoteBean note) {
        noteDataSource.saveNote(note);
        if (noteCache == null){
            noteCache = new LinkedHashMap<>();
        }
        noteCache.put(note.id, note);
    }

    @Override
    public void updateNote(NoteBean note) {
        noteDataSource.updateNote(note);
        if (noteCache == null){
            noteCache = new LinkedHashMap<>();
        }
        NoteBean newNote = new NoteBean(note.id, note.content);
        noteCache.put(newNote.id, newNote);
    }

    @Override
    public void deleteNote(String noteId) {
        noteDataSource.deleteNote(noteId);
        if (noteCache == null){
            noteCache = new LinkedHashMap<>();
        }
        noteCache.remove(noteId);
    }

    @Override
    public void cacheEnable(boolean enable) {
        this.cacheEnable = enable;
    }

    /**
     * 从缓存中获取note
     * @param noteId
     * @return NoteBean
     * */
    private NoteBean getNoteFromCacheById(String noteId){
        if (noteCache == null || noteCache.isEmpty()){
            return null;
        }else {
            return noteCache.get(noteId);
        }
    }

    /**
     * 从数据库中获取notes,并缓存数据
     * @return
     * */
    private void refreshCache(List<NoteBean> notes){
        if (noteCache == null){
            noteCache = new LinkedHashMap<>();
        }
        noteCache.clear();
        for (NoteBean note : notes){
            noteCache.put(note.id, note);
        }
        cacheEnable = true;
    }
}
