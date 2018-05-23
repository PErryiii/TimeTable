package noteEdit;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import noteData.NoteBean;
import noteData.NoteDataSource;
import noteData.NoteRepository;

/**
 * Created by PErry on 2018/2/27.
 */

public class EditNotePresenter implements EditNoteContract.Presenter {

    private EditNoteContract.View editNoteView;
    private NoteRepository noteRepository;
    private String loadNoteId;
    private boolean isNewNote;

    public EditNotePresenter(NoteRepository noteRepository, EditNoteContract.View editNoteView,
                             @Nullable String noteId){
        this.noteRepository = noteRepository;
        this.editNoteView = editNoteView;
        this.loadNoteId = noteId;
        if (TextUtils.isEmpty(loadNoteId)){
            isNewNote = true;
        }
        this.editNoteView.setPresenter(this);
    }

    @Override
    public void start() {
        loadNote();
    }

    @Override
    public void loadNote() {
        if (isNewNote){
            return;
        }
        noteRepository.getNote(loadNoteId, new NoteDataSource.loadNoteCallback() {
            @Override
            public void loadSuccess(NoteBean note) {
                editNoteView.setNote(note.content);
            }

            @Override
            public void loadFailed() {

            }
        });
    }

    @Override
    public void saveNote(String note) {
        if (isNewNote){
            createNote(note);
        } else {
            updateNote(loadNoteId, note);
        }
    }

    @Override
    public void deleteNote() {
        noteRepository.deleteNote(loadNoteId);
    }

    private void createNote(String content){
        NoteBean note = new NoteBean(content);
        noteRepository.saveNote(note);
    }

    private void updateNote(String noteId, String content){
        NoteBean bean = new NoteBean(noteId, content);
        noteRepository.updateNote(bean);
    }
}
