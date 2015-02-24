package com.juliankrone.todolist;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;

/**
 * Enhanced time picker dialog. Main difference from ancestor is that it calls
 * OnTimeSetListener only when when pressing OK button, and skips event when closing with
 * BACK key or by tapping outside a dialog.
 */
public class MyTimePickerDialog extends TimePickerDialog {

    public MyTimePickerDialog(final Context context, final TimePickerDialog.OnTimeSetListener callBack, final int hour, final int minute) {
        super(context, callBack, hour, minute, DateFormat.is24HourFormat(context));
    }

    public MyTimePickerDialog(final Context context, final int theme, final OnTimeSetListener callBack, final int hour, final int minute) {
        super(context, theme, callBack, hour, minute, DateFormat.is24HourFormat(context));
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        // Prevent calling onDateSet handler when clicking to dialog buttons other, then "OK"
        if (which == DialogInterface.BUTTON_POSITIVE)
            super.onClick(dialog, which);
    }

    @Override
    protected void onStop() {
        // prevent calling onDateSet handler when stopping dialog for whatever reason (because this includes
        // closing by BACK button or by tapping outside dialog, this is exactly what we try to avoid)

        //super.onStop();
    }
}