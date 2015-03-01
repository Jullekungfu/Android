package com.juliankrone.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.Normalizer;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void remind (Task task){

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("message", task.getName());
        alarmIntent.putExtra("title", "Upcoming deadline");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //TODO: For demo set after 5 seconds.
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5 * 1000, pendingIntent);

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
        Task task = datasource.createTask(nTaskName.trim().length()>0 ? nTaskName : "Example task", date.getTimeInMillis(), false);
        mAdapter.add(task);
        remind(task);
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean syncConnPref = sharedPref.getBoolean("pref_notifications", false);
        Toast.makeText(this, String.valueOf(syncConnPref), Toast.LENGTH_SHORT).show();
        datasource.close();
        super.onPause();
    }

    public static class TaskNameDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
        private View v;
        private EditText editName;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            this.v = inflater.inflate(R.layout.fragment_task_name, null);
            builder.setView(v);
            editName = (EditText) v.findViewById(R.id.fragment_task_name_input);

            return builder.setTitle("Task name").setPositiveButton("OK", this).setNegativeButton("CANCEL", null).create();
        }

        @Override
        public void onClick(DialogInterface dialog, int position) {

            if(position == DialogInterface.BUTTON_POSITIVE) {
                Log.d("edit", String.valueOf(editName));
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

