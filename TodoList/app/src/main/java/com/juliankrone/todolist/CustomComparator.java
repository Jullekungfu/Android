package com.juliankrone.todolist;

import java.util.Comparator;

public class CustomComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        boolean b1 = t1.getDone();
        boolean b2 = t2.getDone();
        long d1 = t1.getDeadline();
        long d2 = t2.getDeadline();
        if(b1 == b2){
            return (int) (t1.getDeadline() - t2.getDeadline());
        } else {
            if(b1){
                return 1;
            } else {
                return -1;
            }
        }

    }
}