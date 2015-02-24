package com.juliankrone.todolist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends ActionBarActivity {

    private TasksDataSource datasource;
    private String nTaskName;
    private int nYear;
    private int nMonth;
    private int nDay;
    private int nHour;
    private int nMin;

    private RecyclerView mRecyclerView;
    private MyCustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        datasource = new TasksDataSource(this);
        datasource.open();

        ArrayList<Task> values = datasource.getAllTasks();

        mAdapter = new MyCustomAdapter(values, datasource, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onClick(View view) {
        DialogFragment nameFragment = new TaskNameDialogFragment();
        nameFragment.show(getFragmentManager(), "taskName");
    }

    public void onNameSet(String name){
        this.nTaskName = name;

    }
    public void onDateSet(int year, int month, int day){
        this.nYear = year;
        this.nMonth = month;
        this.nDay = day;
    }

    public void onTimeSet(int hour, int minute){
        this.nHour = hour;
        this.nMin = minute;

        GregorianCalendar date = new GregorianCalendar(nYear, nMonth, nDay, nHour, nMin);
        Task task = datasource.createTask(nTaskName.trim().length()>0 ? nTaskName : "Example task", date.getTimeInMillis());
        mAdapter.add(task);
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    public static class TaskNameDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

        private EditText editName;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            editName = new EditText(getActivity());

            return new AlertDialog.Builder(getActivity())
                    .setTitle("Task name")
                    .setPositiveButton("OK", this)
                    .setNegativeButton("CANCEL", null)
                    .setView(editName)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int position) {

            if(position == DialogInterface.BUTTON_POSITIVE) {
                String value = editName.getText().toString();
                ((MainActivity)getActivity()).onNameSet(value);
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getFragmentManager(), "datePicker");
            }
            dialog.dismiss();
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {

            Calendar cal = Calendar.getInstance();
            MyDatePickerDialog dlg = new MyDatePickerDialog(getActivity(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            // Add Cancel button into dialog
            dlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (DialogInterface.OnClickListener) null);

            return dlg;
        }

        @Override
        public void onDateSet(final DatePicker view, final int year, final int month, final int day) {
            ((MainActivity)getActivity()).onDateSet(year, month, day);

            DialogFragment timeFragment = new TimePickerFragment();
            timeFragment.show(getFragmentManager(), "timePicker");
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {

            Calendar cal = Calendar.getInstance();
            MyTimePickerDialog dlg = new MyTimePickerDialog(getActivity(), this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

            // Add Cancel button into dialog
            dlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (DialogInterface.OnClickListener) null);

            return dlg;
        }

        @Override
        public void onTimeSet(final TimePicker view, final int hour, final int minute) {
            ((MainActivity)getActivity()).onTimeSet(hour, minute);
        }
    }

}

