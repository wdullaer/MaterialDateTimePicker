package com.wdullaer.materialdatetimepicker.date;

import android.app.FragmentManager;
import android.os.Build;

import java.util.Calendar;

public class DatePicker {

    /**
     * Determine appropriate picker dialog to be shown
     * Option for horizontal scroll only available for Android API >=7
     *
     * @param  manager Fragment manager of activity where picker dialog will be shown.
     * @param  events Contain details of events to be / not displayed on picker dialog.
     *                Pass null if there is no events to be displayed.
     * @param  tag Picker dialog's tag.
     * @param  scrollDirection Determine picker dialog scroll direction,
     *                         use LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL,
     *                         by default, it is LinearLayoutManager.VERTICAL.
     */
    public static void displayDialog(FragmentManager manager, String tag, DatePickerDialog.OnDateSetListener listener,
                                     Events events, int scrollDirection){

        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerFragment;

        if(Build.VERSION.SDK_INT >= 7){
            datePickerFragment = DatePickerDialog.newInstance(listener, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), events, scrollDirection);
        }else{
            if (events!=null) datePickerFragment = DatePickerDialog.newInstance (listener,cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),events);
            else datePickerFragment = DatePickerDialog.newInstance (listener,cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }
        datePickerFragment.show(manager, tag);
    }
}
