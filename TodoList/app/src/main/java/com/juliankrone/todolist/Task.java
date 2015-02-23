package com.juliankrone.todolist;

public class Task {
    private long id;
    private String name;
    private long deadline;

    public Task(long id, String name, long deadline){
        this.id = id;
        this.name = name;
        this.deadline = deadline;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public void setDeadline(int newDeadline){
        this.deadline = deadline;
    }

    public String getName(){
        return name;
    }

    public long getDeadline(){
        return deadline;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name + " " + deadline;
    }

    public long getId() {
        return id;
    }
}
