package noteList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.timetable_1.R;

import noteData.Injection;


/**
 * Created by PErry on 2018/2/7.
 * note主界面
 */

public class Note extends android.support.v4.app.Fragment {

    public Note(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note, container, false);

        //创建noteContentFragment  (V)
        NoteFragment noteFragment = (NoteFragment) getChildFragmentManager().
                findFragmentById(R.id.frameLayout_note_fragment);
        if (noteFragment == null){
            noteFragment = NoteFragment.newInstence();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout_note_fragment, noteFragment).commit();
        }
        //创建notePresenter  （P）
        NotePresenter notePresenter = new NotePresenter(Injection.provideRepository(getActivity()),
                noteFragment);

        return view;
    }
}
