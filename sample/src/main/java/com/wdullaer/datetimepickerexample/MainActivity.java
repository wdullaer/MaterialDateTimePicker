package com.wdullaer.datetimepickerexample;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
    TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener
{
    private TextView timeTextView;
    private TextView dateTextView;
    private CheckBox mode24Hours;
    private CheckBox modeDarkTime;
    private CheckBox modeDarkDate;
    private CheckBox modeCustomAccentTime;
    private CheckBox modeCustomAccentDate;
    private CheckBox vibrateTime;
    private CheckBox vibrateDate;
    private CheckBox dismissTime;
    private CheckBox dismissDate;
    private CheckBox titleTime;
    private CheckBox titleDate;
    private CheckBox showYearFirst;
    private CheckBox enableSeconds;
    private CheckBox limitTimes;
    private CheckBox limitDates;
    private CheckBox highlightDates;
    private CheckBox hourOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find our View instances
        timeTextView = (TextView)findViewById(R.id.time_textview);
        dateTextView = (TextView)findViewById(R.id.date_textview);
        Button timeButton = (Button)findViewById(R.id.time_button);
        Button dateButton = (Button)findViewById(R.id.date_button);
        mode24Hours = (CheckBox)findViewById(R.id.mode_24_hours);
        modeDarkTime = (CheckBox)findViewById(R.id.mode_dark_time);
        modeDarkDate = (CheckBox)findViewById(R.id.mode_dark_date);
        modeCustomAccentTime = (CheckBox) findViewById(R.id.mode_custom_accent_time);
        modeCustomAccentDate = (CheckBox) findViewById(R.id.mode_custom_accent_date);
        vibrateTime = (CheckBox) findViewById(R.id.vibrate_time);
        vibrateDate = (CheckBox) findViewById(R.id.vibrate_date);
        dismissTime = (CheckBox) findViewById(R.id.dismiss_time);
        dismissDate = (CheckBox) findViewById(R.id.dismiss_date);
        titleTime = (CheckBox) findViewById(R.id.title_time);
        titleDate = (CheckBox) findViewById(R.id.title_date);
        showYearFirst = (CheckBox) findViewById(R.id.show_year_first);
        enableSeconds = (CheckBox) findViewById(R.id.enable_seconds);
        limitTimes = (CheckBox) findViewById(R.id.limit_times);
        limitDates = (CheckBox) findViewById(R.id.limit_dates);
        highlightDates = (CheckBox) findViewById(R.id.highlight_dates);
        hourOnly = (CheckBox) findViewById(R.id.hour_only_picker);

        // check if picker mode is specified in Style.xml
        modeDarkTime.setChecked(Utils.isDarkTheme(this, modeDarkTime.isChecked()));
        modeDarkDate.setChecked(Utils.isDarkTheme(this, modeDarkDate.isChecked()));

        // Show a timepicker when the timeButton is clicked
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        mode24Hours.isChecked()
                );
                tpd.setThemeDark(modeDarkTime.isChecked());
                tpd.vibrate(vibrateTime.isChecked());
                tpd.dismissOnPause(dismissTime.isChecked());
                tpd.enableSeconds(enableSeconds.isChecked());
                tpd.setHourOnlyPicker(hourOnly.isChecked());
                if (modeCustomAccentTime.isChecked()) {
                    tpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                if (titleTime.isChecked()) {
                    tpd.setTitle("TimePicker Title");
                }
                if (limitTimes.isChecked()) {
                    tpd.setTimeInterval(2, 5, 10);
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

        // Show a datepicker when the dateButton is clicked
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(modeDarkDate.isChecked());
                dpd.vibrate(vibrateDate.isChecked());
                dpd.dismissOnPause(dismissDate.isChecked());
                dpd.showYearPickerFirst(showYearFirst.isChecked());
                if (modeCustomAccentDate.isChecked()) {
                    dpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                if (titleDate.isChecked()) {
                    dpd.setTitle("DatePicker Title");
                }
                if (limitDates.isChecked()) {
                    Calendar[] dates = new Calendar[13];
                    for(int i = -6; i <= 6; i++) {
                        Calendar date = Calendar.getInstance();
                        date.add(Calendar.MONTH, i);
                        dates[i+6] = date;
                    }
                    dpd.setSelectableDays(dates);
                }
                if (highlightDates.isChecked()) {
                    Calendar[] dates = new Calendar[13];
                    for(int i = -6; i <= 6; i++) {
                        Calendar date = Calendar.getInstance();
                        date.add(Calendar.WEEK_OF_YEAR, i);
                        dates[i+6] = date;
                    }
                    dpd.setHighlightedDays(dates);
                }
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");

        if(tpd != null) tpd.setOnTimeSetListener(this);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        String time = "You picked the following time: "+hourString+"h"+minuteString+"m"+secondString+"s";
        timeTextView.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        dateTextView.setText(date);
    }
}
