package com.juliankrone.todolist;

public class Task {
    private long id;
    private String name;
    private long deadline;
    private boolean done;

    public Task(long id, String name, long deadline, boolean done){
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.done = done;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public boolean getDone(){
        return done;
    }

    public String getName(){
        return name;
    }

    public long getDeadline(){
        return deadline;
    }

    @Override
    public String toString() {
        return name + " " + deadline;
    }

    public long getId() {
        return id;
    }
}
