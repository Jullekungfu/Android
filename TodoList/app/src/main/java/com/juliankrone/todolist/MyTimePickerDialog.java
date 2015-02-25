package com.juliankrone.todolist;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;

public class MyTimePickerDialog extends TimePickerDialog {

    public MyTimePickerDialog(final Context context, final TimePickerDialog.OnTimeSetListener callBack, final int hour, final int minute) {
        super(context, callBack, hour, minute, DateFormat.is24HourFormat(context));
    }

    public MyTimePickerDialog(final Context context, final int theme, final OnTimeSetListener callBack, final int hour, final int minute) {
        super(context, theme, callBack, hour, minute, DateFormat.is24HourFormat(context));
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        if (which == DialogInterface.BUTTON_POSITIVE)
            super.onClick(dialog, which);
    }

    @Override
    protected void onStop() {
        // prevent calling onTimeSet
        //super.onStop();
    }
}