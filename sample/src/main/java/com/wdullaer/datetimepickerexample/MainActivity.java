package com.wdullaer.datetimepickerexample;

import android.content.DialogInterface;
import android.text.method.KeyListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private CheckBox timeLimit;
    private LinearLayout timeLimitControlMin;
    private LinearLayout timeLimitControlMax;
    private CheckBox timeLimitMin;
    private CheckBox timeLimitMax;
    private EditText minHourEditText;
    private EditText minMinuteEditText;
    private EditText maxHourEditText;
    private EditText maxMinuteEditText;

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

        timeLimit = (CheckBox) findViewById(R.id.time_limit);
        timeLimitControlMin = (LinearLayout) findViewById(R.id.time_limit_data_min);
        timeLimitMin = (CheckBox) findViewById(R.id.time_limit_min);
        minHourEditText = (EditText) findViewById(R.id.min_time_hour);
        minMinuteEditText = (EditText) findViewById(R.id.min_time_minute);
        timeLimitControlMax = (LinearLayout) findViewById(R.id.time_limit_data_max);
        timeLimitMax = (CheckBox) findViewById(R.id.time_limit_max);
        maxHourEditText = (EditText) findViewById(R.id.max_time_hour);
        maxMinuteEditText = (EditText) findViewById(R.id.max_time_minute);

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
                if (modeCustomAccentTime.isChecked()) {
                    tpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface)
                    {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });

                if(timeLimit.isChecked())
                {
                    if(timeLimitMin.isChecked())
                        tpd.setMinTime(Integer.parseInt(minHourEditText.getText().toString()),
                                        Integer.parseInt(minMinuteEditText.getText().toString()));

                    if(timeLimitMax.isChecked())
                        tpd.setMaxTime(Integer.parseInt(maxHourEditText.getText().toString()),
                                        Integer.parseInt(maxMinuteEditText.getText().toString()));
                }

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
                if (modeCustomAccentDate.isChecked()) {
                    dpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        timeLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    timeLimitControlMin.setVisibility(View.VISIBLE);
                    timeLimitControlMax.setVisibility(View.VISIBLE);
                }
                else
                {
                    timeLimitControlMin.setVisibility(View.GONE);
                    timeLimitControlMax.setVisibility(View.GONE);
                }
            }
        });

        minHourEditText.setTag(minHourEditText.getKeyListener());
        minMinuteEditText.setTag(minMinuteEditText.getKeyListener());
        minHourEditText.setKeyListener(null);
        minMinuteEditText.setKeyListener(null);

        maxHourEditText.setTag(maxHourEditText.getKeyListener());
        maxMinuteEditText.setTag(maxMinuteEditText.getKeyListener());
        maxHourEditText.setKeyListener(null);
        maxMinuteEditText.setKeyListener(null);

        timeLimitMin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    minHourEditText.setKeyListener((KeyListener)minHourEditText.getTag());
                    minMinuteEditText.setKeyListener((KeyListener)minMinuteEditText.getTag());
                }
                else
                {
                    minHourEditText.setKeyListener(null);
                    minMinuteEditText.setKeyListener(null);
                }
            }
        });

        timeLimitMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    maxHourEditText.setKeyListener((KeyListener)maxHourEditText.getTag());
                    maxMinuteEditText.setKeyListener((KeyListener)maxMinuteEditText.getTag());
                }
                else
                {
                    maxHourEditText.setKeyListener(null);
                    maxMinuteEditText.setKeyListener(null);
                }
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
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String time = "You picked the following time: "+hourString+"h"+minuteString;
        timeTextView.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        dateTextView.setText(date);
    }
}
