package com.juliankrone.todolist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyDatePickerDialog extends DatePickerDialog {

    public MyDatePickerDialog(final Context context, final OnDateSetListener callBack, final int year, final int monthOfYear, final int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    public MyDatePickerDialog(final Context context, final int theme, final OnDateSetListener callBack, final int year, final int monthOfYear, final int dayOfMonth) {
        super(context, theme, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        if (which == DialogInterface.BUTTON_POSITIVE)
            super.onClick(dialog, which);
    }

    @Override
    protected void onStop() {
        // prevent calling onDateSet
        //super.onStop();
    }
}