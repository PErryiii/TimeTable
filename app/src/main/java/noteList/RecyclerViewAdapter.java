package noteList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.timetable_1.R;

import java.util.List;

import noteData.NoteBean;

/**
 * Created by PErry on 2018/2/13.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<NoteBean> noteData;  //note的数据，根据数据库类型更改
    private onNoteItemClickListener listener;

    /*构造函数传入noteData*/
    public RecyclerViewAdapter(List<NoteBean> noteData, onNoteItemClickListener listener){
        this.noteData = noteData;
        this.listener = listener;
    }

    public void updateData(List<NoteBean> noteData) {       //更换数据
        this.noteData = noteData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*实例化展示的view*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent,
                false);
        // 实例化viewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /*为note绑定数据noteData*/
        NoteBean note = noteData.get(position);
        holder.text_noteItem_content.setText(note.content + "");
        initListener(holder, position);
    }

    private void initListener(final ViewHolder viewHolder, final int position){
        if (listener != null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {     //note的点击事件
                @Override
                public void onClick(View v) {
                    listener.onNoteClick(noteData.get(position));
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() { //note的长按事件
                @Override
                public boolean onLongClick(View v) {
                    return listener.onLongClick(v, noteData.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return noteData == null ? 0 : noteData.size();
    }

    /*初始化note的控件*/
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text_noteItem_content;

        public ViewHolder(View itemView) {
            super(itemView);
            text_noteItem_content = itemView.findViewById(R.id.text_noteItem_content);
        }
    }


    interface onNoteItemClickListener{      //recyclerView的点击事件回调
        void onNoteClick(NoteBean note);        //item点击回调
        boolean onLongClick(View view, NoteBean note);      //item长按回调
    }

}
