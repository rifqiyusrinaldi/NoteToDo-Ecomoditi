package com.rifqi.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListTaskAdapter extends RecyclerView.Adapter<ListTaskAdapter.ListViewHolder> {
    private List<TaskData> taskList;
    private Activity context;
    private RoomDB database;

    public ListTaskAdapter (Context context, List<TaskData> taskList){
        this.taskList = taskList;
        this.context = (Activity) context;
        notifyDataSetChanged();
    }
    public void updateData(List<TaskData> listData){
        this.taskList = listData;
        notifyDataSetChanged();
    }
    @NonNull
    @NotNull
    @Override
    public ListTaskAdapter.ListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_list_todo, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull ListTaskAdapter.ListViewHolder holder, int position) {
        final TaskData taskData = taskList.get(position);
        database = RoomDB.getInstance(context);
        holder.task.setText(taskData.getText());
        holder.date.setText(taskData.getDate());
        holder.cek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    database.taskDao().updateTask(1, taskData.getID());
                    holder.task.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }else if(!b) {
                    database.taskDao().updateTask(0, taskData.getID());
                    holder.task.setPaintFlags(0);
                }
            }
        });

//        if (holder.cek.isChecked() == true){
//            database.taskDao().updateTask(1, taskData.getID());
//        }else if(holder.cek.isChecked() == false){
//            database.taskDao().updateTask(0, taskData.getID());
//        }
        if (database.taskDao().selectTask(taskData.getID()).getStatus() == 1){
            holder.cek.setChecked(true);
            holder.task.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        CheckBox cek;
        TextView task, date;
        public ListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cek = itemView.findViewById(R.id.checkbox_item);
            task = itemView.findViewById(R.id.tv_task);
            date = itemView.findViewById(R.id.tv_date);
        }
    }
}
