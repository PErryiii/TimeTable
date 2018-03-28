package noteList;

import java.util.ArrayList;
import java.util.List;

import noteData.NoteBean;
import noteData.NoteDataSource;
import noteData.NoteRepository;

/**
 * Created by PErry on 2018/2/23.
 */

public class NotePresenter implements NoteContract.Presenter {

    private NoteContract.View notesView;    //Presenter持有View
    private NoteRepository noteRepository;      //MVP的Model，管理数据处理
    private boolean isFirstLoad = true;

    public NotePresenter(NoteRepository noteRepository, NoteContract.View notesView){
        this.notesView = notesView;
        this.noteRepository = noteRepository;
        notesView.setPresenter(this);
    }

    /**
     * 以下为MainContract.Presenter接口实现
     * */
    @Override
    public void start() {
        if (isFirstLoad) {
            loadNotes(true, true);  //第一次打开界面时从数据源获取数据
            isFirstLoad = false;
        } else {
            loadNotes(false, true);
        }
    }



    @Override
    public void loadNotes(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            notesView.setLoadingIndicator(true);
        }
        noteRepository.cacheEnable(forceUpdate);

        noteRepository.getNotes(new NoteDataSource.loadNotesCallback() {
            @Override
            public void loadSuccess(List<NoteBean> noteList) {
                if (showLoadingUI){
                    notesView.setLoadingIndicator(false);
                }
                List<NoteBean> notesToShow = new ArrayList<>();
                for (NoteBean note : noteList){
                    notesToShow.add(note);
                }
                notesView.showNotes(notesToShow);
            }


            @Override
            public void loadFailed() {
                if (showLoadingUI) {
                    notesView.setLoadingIndicator(false);
                }
                notesView.showLoadNotesError();
            }
        });
    }

    @Override
    public void addNote() {
        notesView.showAddNotesUi();
    }

    @Override
    public void deleteNote(NoteBean bean) {
        noteRepository.deleteNote(bean.id);
        notesView.showNoteDeleted();
        loadNotes(false,false);
    }

    @Override
    public void openNoteDetail(NoteBean bean) {
        notesView.showNoteDetailUi(bean.id);
    }
}
