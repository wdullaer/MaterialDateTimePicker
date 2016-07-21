package com.wdullaer.datetimepickerexample;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    private TextView timeTextView;
    private CheckBox mode24Hours;
    private CheckBox modeDarkTime;
    private CheckBox modeCustomAccentTime;
    private CheckBox vibrateTime;
    private CheckBox dismissTime;
    private CheckBox titleTime;
    private CheckBox enableSeconds;
    private CheckBox limitSelectableTimes;
    private CheckBox showVersion2;

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timepicker_layout, container, false);

        // Find our View instances
        timeTextView = (TextView) view.findViewById(R.id.time_textview);
        Button timeButton = (Button) view.findViewById(R.id.time_button);
        mode24Hours = (CheckBox) view.findViewById(R.id.mode_24_hours);
        modeDarkTime = (CheckBox) view.findViewById(R.id.mode_dark_time);
        modeCustomAccentTime = (CheckBox) view.findViewById(R.id.mode_custom_accent_time);
        vibrateTime = (CheckBox) view.findViewById(R.id.vibrate_time);
        dismissTime = (CheckBox) view.findViewById(R.id.dismiss_time);
        titleTime = (CheckBox) view.findViewById(R.id.title_time);
        enableSeconds = (CheckBox) view.findViewById(R.id.enable_seconds);
        limitSelectableTimes = (CheckBox) view.findViewById(R.id.limit_times);
        showVersion2 = (CheckBox) view.findViewById(R.id.show_version_2);

        // Show a timepicker when the timeButton is clicked
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        TimePickerFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        mode24Hours.isChecked()
                );
                tpd.setThemeDark(modeDarkTime.isChecked());
                tpd.vibrate(vibrateTime.isChecked());
                tpd.dismissOnPause(dismissTime.isChecked());
                tpd.enableSeconds(enableSeconds.isChecked());
                tpd.setVersion(showVersion2.isChecked() ? TimePickerDialog.Version.VERSION_2 : TimePickerDialog.Version.VERSION_1);
                if (modeCustomAccentTime.isChecked()) {
                    tpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                if (titleTime.isChecked()) {
                    tpd.setTitle("TimePicker Title");
                }
                if (limitSelectableTimes.isChecked()) {
                    tpd.setTimeInterval(3, 5, 10);
                }
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        if(tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        String time = "You picked the following time: "+hourString+"h"+minuteString+"m"+secondString+"s";
        timeTextView.setText(time);
    }
}
