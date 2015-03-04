package com.juliankrone.todolist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyCustomAdapter extends RecyclerView.Adapter<TaskHolder> {
    private ArrayList<Task> list = new ArrayList<Task>();
    private TasksDataSource dataSource;
    private Context context;

    public MyCustomAdapter(ArrayList<Task> list, TasksDataSource dataSource, Context context) {
        this.list = list;
        this.dataSource = dataSource;
        this.context = context;
    }

    public void add(Task task){
        int pos = findPos(task);
        list.add(pos, task);
        notifyItemInserted(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }

    private int findPos(Task task){
        long d = task.getDeadline();
        boolean done = task.getDone();
        for(int i = list.size()-1; i>= 0; i--){
            Task t = list.get(i);
            if(done){
                if(t.getDone() && t.getDeadline() < d || !t.getDone()){
                    return i+1;
                }
            } else {
                if(!t.getDone() && t.getDeadline() < d){
                    return i+1;
                }
            }
        }
        return 0;
    }

    public ArrayList<Task> getActiveTasks(){
        ArrayList<Task> ds = new ArrayList<>();
        for(Task t : list){
            if (!t.getDone()){
                ds.add(t);
            }
        }
        return ds;
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_button, parent, false);
        return new TaskHolder(v);
    }

    @Override
    public void onBindViewHolder(final TaskHolder holder, final int position) {
        Task task = list.get(position);
        holder.name.setText(task.getName());
        holder.done = task.getDone();
        if(holder.done) {
            holder.name.setAlpha(0.2f);
            holder.date.setAlpha(0.2f);
        } else {
            holder.name.setAlpha(0.9f);
            holder.date.setAlpha(0.8f);
        }

        Date date = new Date(task.getDeadline()*1000);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy, HH:mm");
        holder.date.setText(format.format(date));

        holder.dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task tsk = list.remove(position);
                dataSource.deleteTask(tsk);
                ((MainActivity)context).remove(tsk);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Task newTask = list.remove(position);
                dataSource.deleteTask(newTask);
                ((MainActivity)context).remove(newTask);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

                String name = newTask.getName();
                long date = newTask.getDeadline();
                boolean done = !newTask.getDone();
                Task nTask = dataSource.createTask(name, date*1000, done);
                add(nTask);
            }
        });
    }

    @Override
    public long getItemId(int pos) {
        return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
