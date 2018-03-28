package noteList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.timetable_1.R;

import java.util.ArrayList;
import java.util.List;

import noteData.NoteBean;
import noteEdit.EditNoteActivity;

/**
 * Created by PErry on 2018/2/23.
 */

public class NoteFragment extends Fragment implements NoteContract.View{

    private NoteContract.Presenter presenter;  //View持有Presenter

    private FloatingActionButton fab_notesFragment_addNote;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<NoteBean> data = new ArrayList<>();

    public static NoteFragment newInstence(){
        return new NoteFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_fragment, container, false);

        //初始化view
        /**
         * fab--addNote
         * */
        fab_notesFragment_addNote = getActivity().findViewById(R.id.fab_notesFragment_addNote);
        fab_notesFragment_addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addNote();
            }
        });

        /**
         * swipe
         * */
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        final SwipeRefreshLayout.OnRefreshListener refreshListener = new
                SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadNotes(true,true);
            }
        };
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        /**
         * recyclerView
         * */
        recyclerView = view.findViewById(R.id.recyclerView_noteFragment_content);
        adapter = new RecyclerViewAdapter(data, onNoteItemClickListener);
        recyclerView.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    /**
     * recyclerView的itemView点击事件
     * */
    RecyclerViewAdapter.onNoteItemClickListener onNoteItemClickListener = new RecyclerViewAdapter
            .onNoteItemClickListener() {
        @Override
        public void onNoteClick(NoteBean note) {
            presenter.openNoteDetail(note);
        }

        @Override
        public boolean onLongClick(View view, final NoteBean note) {
            final AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("是否删除？");
            builder.setTitle("警告！");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.deleteNote(note);
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
            return true;
        }
    };


    /**
     * noteContract的接口实现
     */
    @Override
    public void setPresenter(NoteContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if(getView() == null){
            return;
        }
        //用post可以保证swipeRefreshLayout已布局完成
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showNotes(List<NoteBean> notes) {
        adapter.updateData(notes);
    }

    @Override
    public void showLoadNotesError() {
        Snackbar.make(getView(),"加载数据失败",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showAddNotesUi() {
        //todo:新建note
        Intent i = new Intent(getActivity(), EditNoteActivity.class);
        startActivity(i);
    }

    @Override
    public void showNoteDetailUi(String noteId) {
        //todo:编辑note
        Intent i = new Intent(getActivity(),EditNoteActivity.class);
        i.putExtra(EditNoteActivity.NOTE_ID, noteId);
        startActivity(i);
    }

    @Override
    public void showNoteDeleted() {
        Snackbar.make(getView(),"成功删除该便笺",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();   //判断当前Fragment是否添加至Activity
    }
}
