package noteEdit;

import com.example.timetable_1.BasePresenter;
import com.example.timetable_1.BaseView;

/**
 * Created by PErry on 2018/2/27.
 */

public class EditNoteContract {

    interface Presenter extends BasePresenter{
        void loadNote();
        void saveNote(String note);
        void deleteNote();
    }

    interface View extends BaseView<Presenter>{
        void showNoteList();    //返回Note界面
        void setNote(String note);
    }
}
