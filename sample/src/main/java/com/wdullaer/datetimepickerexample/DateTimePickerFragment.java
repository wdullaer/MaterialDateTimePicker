package com.wdullaer.datetimepickerexample;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.datetime.DateTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DateTimePickerFragment extends Fragment implements DateTimePickerDialog.OnDateTimeListener {
    TextView datetimeTextView;
    CheckBox mode24Hours;
    CheckBox modeDark;
    CheckBox modeCustomAccent;
    CheckBox vibrateDatetime;
    CheckBox dismissDatetime;
    CheckBox titleDatetime;
    CheckBox enableSeconds;
    CheckBox showYearFirst;

    public DateTimePickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.datetimepicker_layout, container, false);

        // Find our View instances
        datetimeTextView = (TextView) view.findViewById(R.id.datetime_textview);
        Button datetimeButton = (Button) view.findViewById(R.id.datetime_button);
        mode24Hours = (CheckBox) view.findViewById(R.id.mode_24_hours);
        modeDark = (CheckBox) view.findViewById(R.id.mode_dark_datetime);
        modeCustomAccent = (CheckBox) view.findViewById(R.id.mode_custom_accent_datetime);
        vibrateDatetime = (CheckBox) view.findViewById(R.id.vibrate_datetime);
        dismissDatetime = (CheckBox) view.findViewById(R.id.dismiss_datetime);
        titleDatetime = (CheckBox) view.findViewById(R.id.title_datetime);
        enableSeconds = (CheckBox) view.findViewById(R.id.enable_seconds);
        showYearFirst = (CheckBox) view.findViewById(R.id.show_year_first);

        // Show a timepicker when the timeButton is clicked
        datetimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DateTimePickerDialog dtpd = DateTimePickerDialog.newInstance(
                        DateTimePickerFragment.this,
                        now,
                        mode24Hours.isChecked()
                );
                dtpd.setThemeDark(modeDark.isChecked());
                dtpd.vibrate(vibrateDatetime.isChecked());
                dtpd.dismissOnPause(dismissDatetime.isChecked());
                dtpd.enableSeconds(enableSeconds.isChecked());
                dtpd.showYearPickerFirst(showYearFirst.isChecked());
                if (modeCustomAccent.isChecked()) {
                    dtpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                if (titleDatetime.isChecked()) {
                    dtpd.setTitle("TimePicker Title");
                }
                dtpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                dtpd.show(getFragmentManager(), "Datetimepickerdialog");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DateTimePickerDialog dtpd = (DateTimePickerDialog) getFragmentManager().findFragmentByTag("Datetimepickerdialog");
        if(dtpd != null) dtpd.setOnDateTimeListener(this);
    }

    @Override
    public void onDateTimeSet(DateTimePickerDialog view, Calendar selection) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        String datetime = "You picked the following date and time: "+formatter.format(selection.getTime());
        datetimeTextView.setText(datetime);
    }
}
