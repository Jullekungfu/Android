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
    protected boolean done;

    public TaskHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.task_name_string);
        this.date = (TextView) view.findViewById(R.id.task_deadline_string);
        this.dlt = (ImageView) view.findViewById(R.id.delete_btn);
        this.relativeLayout = (RelativeLayout) view.findViewById(R.id.task_layout);

/*

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(done){
                    relativeLayout.setBackgroundColor(relativeLayout.getResources().getColor(R.color.transparent));
                } else {
                    relativeLayout.setBackgroundColor(relativeLayout.getResources().getColor(R.color.material_grey_400));
                }
                done = !done;
            }
        });
*/

    }

}