package com.juliankrone.todolist;

import java.util.Comparator;

public class CustomComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        return (int) (t1.getDeadline() - t2.getDeadline());
    }
}