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
        for(int i = list.size()-1; i>= 0; i--){
            if(list.get(i).getDeadline()<d){
                return i+1;
            }
        }
        return 0;
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
        Date date = new Date(task.getDeadline()*1000);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM, HH:mm");
        holder.date.setText(format.format(date));

        holder.dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                dataSource.deleteTask(list.remove(position)); //or some other task
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
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

    //@Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    //@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.task_button, null);
        }

        //Handle TextView and display string from your list
        TextView taskNameText = (TextView)view.findViewById(R.id.task_name_string);
        taskNameText.setText(list.get(position).getName());

        Date date = new Date(list.get(position).getDeadline()*1000);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM, HH:mm");
        //Handle TextView and display string from your list
        TextView taskDeadlineText = (TextView)view.findViewById(R.id.task_deadline_string);
        taskDeadlineText.setText(format.format(date));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                dataSource.deleteTask(list.remove(position)); //or some other task
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
