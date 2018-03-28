package noteList;

import com.example.timetable_1.BasePresenter;
import com.example.timetable_1.BaseView;

import java.util.List;

import noteData.NoteBean;

/**
 * Created by PErry on 2018/2/23.
 */

public class NoteContract {

    interface Presenter extends BasePresenter {

        /**
         *
         * @param forceUpdate 是否是更新。
         *                    true则从数据源（服务器、数据库等）获取数据，false则从缓存中直接获取
         * @param showLoadingUI 是否需要显示加载框
         */

        void loadNotes(boolean forceUpdate,boolean showLoadingUI);

        void addNote(); //添加便笺

        void deleteNote(NoteBean bean); //删除便笺

        void openNoteDetail(NoteBean bean); //便笺详情

    }

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active); //显示、隐藏加载控件

        void showNotes(List<NoteBean> notes); //显示便笺

        void showLoadNotesError();//加载便笺失败

        void showAddNotesUi();    //显示创建便笺界面

        void showNoteDetailUi(String noteId); //显示编辑便笺界面

        void showNoteDeleted(); //删除了一个便笺后

        boolean isActive(); //用于判断当前界面是否还在前台
    }

}
