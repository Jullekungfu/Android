package com.juliankrone.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskHolder extends RecyclerView.ViewHolder {
    protected TextView name;
    protected TextView date;
    protected ImageView dlt;
    protected RelativeLayout relativeLayout;

    public TaskHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.task_name_string);
        this.date = (TextView) view.findViewById(R.id.task_deadline_string);
        this.dlt = (ImageView) view.findViewById(R.id.delete_btn);
        this.relativeLayout = (RelativeLayout) view.findViewById(R.id.task_layout);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tsk = name.getText().toString();
                if(tsk.startsWith("DONE ")){
                    name.setText(tsk.substring(5));
                } else {
                    name.setText("DONE " + tsk);
                }

            }
        });

    }

}