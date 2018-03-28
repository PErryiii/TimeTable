package noteEdit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.timetable_1.BaseActivity;
import com.example.timetable_1.R;

import noteData.Injection;

/**
 * Created by PErry on 2018/2/27.
 */

public class EditNoteActivity extends AppCompatActivity{

    public static final String NOTE_ID = "noteId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_activity);

        String noteId = getIntent().getStringExtra(NOTE_ID); //为空则代表是新建的便笺

        //创建fragment    (V)
        EditNoteFragment editNoteFragment = (EditNoteFragment)getSupportFragmentManager()
                .findFragmentById(R.id.frameLayout_editNote_fragment);
        if (editNoteFragment == null){
            editNoteFragment = EditNoteFragment.getInstence();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout_editNote_fragment, editNoteFragment);
            transaction.commit();
        }

        //创建Presenter   (P)
        EditNotePresenter editNotePresenter = new EditNotePresenter(Injection
                .provideRepository(this), editNoteFragment, noteId);
    }
}
