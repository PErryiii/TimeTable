package noteEdit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.timetable_1.R;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Created by PErry on 2018/2/27.
 */

public class EditNoteFragment extends Fragment implements EditNoteContract.View{

    private MaterialEditText editText_editNote;
    private FloatingActionButton fab_editNote_delete;
    private ImageButton btn_editNote_delete;
    private Toolbar toolbar_editNote_title;

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

        /**
         * 保存note
         * */
        fab_editNote_delete = view.findViewById(R.id.fab_editNote_delete);
        fab_editNote_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveNote(editText_editNote.getText().toString());
                getActivity().finish();
            }
        });

        /**
         * 删除note
         * */
        btn_editNote_delete = view.findViewById(R.id.btn_editNote_delete);
        btn_editNote_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.deleteNote();
                getActivity().finish();
            }
        });

        /**
         * toolbar
         * */
        toolbar_editNote_title = view.findViewById(R.id.toolbar_editNote_title);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar_editNote_title);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_36x36);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
                break;
            default:
                break;
        }
        return true;
    }

/*    @Override
    public void onPause() {
        super.onPause();
        *//*必须在onPause中保存数据，因为在返回NoteFragment时
        是先调用EditNoteFragment的onPause，然后调用NoteFragment的onStart,onResume，
        之后才调用EditNoteFragment的onStop,onDestroyView,onDestroy*//*
        presenter.saveNote(editText_editNote.getText().toString());
    }*/

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
