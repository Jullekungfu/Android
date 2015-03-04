package com.juliankrone.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
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
import java.util.Date;
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

    private boolean notifications;


    public static final String KEY_PREF_NOTIFICATIONS = "pref_notifications";
    public static final String KEY_PREF_NOTIFICATION_TIME = "pref_notification_time";

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefsListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key)
                {
                    settingsChanged();
                }

            };



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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        notifications = sharedPref.getBoolean(KEY_PREF_NOTIFICATIONS, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //5min</item><item name="30m">30min</item><item name="1h">1h</item><item name="6h">6h</item><item name="24h">24h
    private void settingsChanged() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notifyPref = sharedPref.getBoolean(KEY_PREF_NOTIFICATIONS, false);
        for(Task t : mAdapter.getActiveTasks()){
            remove(t);
        }
        if(notifyPref){
            for(Task t : mAdapter.getActiveTasks()){
                remind(t);
            }
        }
    }

    private long getDelay() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String notifyTimePref = sharedPref.getString(KEY_PREF_NOTIFICATION_TIME, "");
        long timeDiff;
        switch (notifyTimePref) {
            case "5min":
                timeDiff = 5 * 60 * 1000;
                break;
            case "30min":
                timeDiff = 30 * 60 * 1000;
                break;
            case "1h":
                timeDiff = 1 * 60 * 60 * 1000;
                break;
            case "6h":
                timeDiff = 6 * 60 * 60 * 1000;
                break;
            case "24h":
                timeDiff = 24 * 60 * 60 * 1000;
                break;
            default:
                timeDiff = 0;

        }
        return timeDiff;
    }

    /**
     * Create future notification when:
     * a task is created more than [notifyTime] in the future
     * notifications are activated and task deadline is more than [notifyTime] in the future
     * @param task the current task that should attach a notification
     */

    public void remind (Task task){
        long nTime = (task.getDeadline()*1000)-getDelay();
        if(notifications && nTime > System.currentTimeMillis()) {

            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            alarmIntent.putExtra("message", task.getName());
            alarmIntent.putExtra("title", "Upcoming deadline");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) task.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, nTime, pendingIntent);

            Log.d("Alarm set at", new Date(nTime).toString());
        }

    }

    public void remove (Task task){
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(this, (int) task.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
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

        if(notifications)
            remind(task);
    }

    @Override
    protected void onResume() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mPrefsListener);
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mPrefsListener);
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

