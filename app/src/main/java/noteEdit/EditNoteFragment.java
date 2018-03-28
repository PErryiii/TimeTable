package noteEdit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.timetable_1.R;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Created by PErry on 2018/2/27.
 */

public class EditNoteFragment extends Fragment implements EditNoteContract.View{

    private MaterialEditText editText_editNote;

    private EditNoteContract.Presenter presenter;

    public static EditNoteFragment getInstence(){
        return  new EditNoteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_note_fragment, container, false);
        editText_editNote = view.findViewById(R.id.editText_editNote);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*必须在onPause中保存数据，因为在返回NoteFragment时
        是先调用EditNoteFragment的onPause，然后调用NoteFragment的onStart,onResume，
        之后才调用EditNoteFragment的onStop,onDestroyView,onDestroy*/
        presenter.saveNote(editText_editNote.getText().toString());
    }

    @Override
    public void setPresenter(EditNoteContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showNoteList() {
        getActivity().finish();
    }

    @Override
    public void setNote(String note) {
        editText_editNote.setText(note + "");
        editText_editNote.requestFocus();
        editText_editNote.setSelection(editText_editNote.getText().length());
    }
}
