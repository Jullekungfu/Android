package com.juliankrone.todolist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Enhanced date picker dialog. Main difference from ancestor is that it calls
 * OnDateSetListener only when when pressing OK button, and skips event when closing with
 * BACK key or by tapping outside a dialog.
 */
public class IBSDatePickerDialog extends DatePickerDialog {

    public IBSDatePickerDialog(final Context context, final OnDateSetListener callBack, final int year, final int monthOfYear, final int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    public IBSDatePickerDialog(final Context context, final int theme, final OnDateSetListener callBack, final int year, final int monthOfYear, final int dayOfMonth) {
        super(context, theme, callBack, year, monthOfYear, dayOfMonth);
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