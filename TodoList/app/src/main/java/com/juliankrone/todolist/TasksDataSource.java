package com.juliankrone.todolist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TasksDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TASK_NAME, MySQLiteHelper.COLUMN_TASK_DEADLINE, MySQLiteHelper.COLUMN_TASK_DONE };

    public TasksDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Task createTask(String name, long deadline, boolean done) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TASK_NAME, name);
        values.put(MySQLiteHelper.COLUMN_TASK_DEADLINE, deadline/1000);
        values.put(MySQLiteHelper.COLUMN_TASK_DONE, done ? 1 : 0);
        long insertId = database.insert(MySQLiteHelper.TABLE_TASKS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TASKS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        Log.i("task", newTask.toString());
        cursor.close();
        return newTask;
    }

    public void deleteTask(Task task) {
        long id = task.getId();
        System.out.println("Task deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_TASKS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TASKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        Collections.sort(tasks, new CustomComparator());
        return tasks;
    }

    private Task cursorToTask(Cursor cursor) {
        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        int deadline = cursor.getInt(2);
        int done = cursor.getInt(3);
        Task task = new Task(id, name, deadline, done==1);
        return task;
    }
}

