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
import android.widget.TimePicker;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

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
    private CheckBox disableSpecificTimes;
    private CheckBox showVersion2;
    private TimePickerDialog tpd;

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timepicker_layout, container, false);

        // Find our View instances
        timeTextView = view.findViewById(R.id.time_textview);
        Button timeButton = view.findViewById(R.id.time_button);
        mode24Hours = view.findViewById(R.id.mode_24_hours);
        modeDarkTime = view.findViewById(R.id.mode_dark_time);
        modeCustomAccentTime = view.findViewById(R.id.mode_custom_accent_time);
        vibrateTime = view.findViewById(R.id.vibrate_time);
        dismissTime = view.findViewById(R.id.dismiss_time);
        titleTime = view.findViewById(R.id.title_time);
        enableSeconds = view.findViewById(R.id.enable_seconds);
        limitSelectableTimes = view.findViewById(R.id.limit_times);
        disableSpecificTimes = view.findViewById(R.id.disable_times);
        showVersion2 = view.findViewById(R.id.show_version_2);

        view.findViewById(R.id.original_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                new android.app.TimePickerDialog(
                        getActivity(),
                        new android.app.TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                Log.d("Original", "Got clicked");
                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        mode24Hours.isChecked()
                ).show();
            }
        });

        // Show a timepicker when the timeButton is clicked
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                /*
                It is recommended to always create a new instance whenever you need to show a Dialog.
                The sample app is reusing them because it is useful when looking for regressions
                during testing
                 */
                if (tpd == null) {
                    tpd = TimePickerDialog.newInstance(
                            TimePickerFragment.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            mode24Hours.isChecked()
                    );
                } else {
                    tpd.initialize(
                            TimePickerFragment.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            now.get(Calendar.SECOND),
                            mode24Hours.isChecked()
                    );
                }
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
                    if (enableSeconds.isChecked()) {
                        tpd.setTimeInterval(3, 5, 10);
                    } else {
                        tpd.setTimeInterval(3, 5, 60);
                    }
                }
                if (disableSpecificTimes.isChecked()) {
                    Timepoint[] disabledTimes = {
                            new Timepoint(10),
                            new Timepoint(10, 30),
                            new Timepoint(11),
                            new Timepoint(12, 30)
                    };
                    tpd.setDisabledTimes(disabledTimes);
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
