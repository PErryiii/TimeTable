package tableList;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.timetable_1.R;
import java.util.List;
import tableData.Course;

/**
 * Created by PErry on 2018/4/4.
 */

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private List<Course> tableData;
    private onTableItemOnClickListener listener;


    public TableAdapter(List<Course> courseList, onTableItemOnClickListener listener){
        tableData = courseList;
        this.listener = listener;
    }

    public void updateData(List<Course> courseList) {       //更换数据
        this.tableData = courseList;
        notifyDataSetChanged();
    }

    /**
     * 加载Item实例，创建ViewHolder实例
     * */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item, parent,
                false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * 改变数据
     * */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = tableData.get(position).name;
        String room = tableData.get(position).room;
        if (name != null || room != null){
            holder.textView.setText(tableData.get(position).name + "\n" +
                    tableData.get(position).room);
            holder.cardView.setCardBackgroundColor(Color.parseColor(tableData.get(position).color));
        }
        initListener(holder, position);
    }

    @Override
    public int getItemCount() {
        return 7*12;
    }

    /**
     * 重写ViewHolder
     * */
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_tableItem_content);
            cardView = itemView.findViewById(R.id.cardView_tableItem);
        }
    }

    /**
     * tableItem的点击事件实现
     * */
    private void initListener(final ViewHolder holder, final int position){
        if (listener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(tableData.get(position));
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return listener.onItemLongClick(v, tableData.get(position));
                }
            });
        }
    }

    /**
     * tableItem的点击事件回调
     * */
    interface onTableItemOnClickListener{
        void onItemClick(Course course);     //item点击回调
        boolean onItemLongClick(View view, Course course);     //item长按回调
    }
}
