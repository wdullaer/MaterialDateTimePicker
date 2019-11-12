package com.wdullaer.datetimepickerexample;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wdullaer.materialdatetimepicker.enums.CalendarType;
import com.wdullaer.materialdatetimepicker.enums.Version;
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

    private static Typeface font;
    private TimePickerDialog tpd;

    private CalendarType calendarType;

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        final Spinner spinner = view.findViewById(R.id.calendar_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.calendar_types_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        font = Typeface.createFromAsset(requireActivity().getAssets(), "IRANYekanMobileRegular.ttf");

        view.findViewById(R.id.original_button).setOnClickListener(view1 -> {
            Calendar now = Calendar.getInstance();
            new android.app.TimePickerDialog(
                    getActivity(),
                    (view11, hour, minute) -> Log.d("Original", "Got clicked"),
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    mode24Hours.isChecked()
            ).show();
        });

        // Show a timepicker when the timeButton is clicked
        timeButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();

            if (spinner.getSelectedItemPosition() == 0) {
                calendarType = CalendarType.JALALI;
            } else {
                calendarType = CalendarType.GREGORIAN;
            }

            /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */
            if (tpd == null) {
                tpd = TimePickerDialog.newInstance(
                        TimePickerFragment.this,
                        calendarType,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        mode24Hours.isChecked()
                );
            } else {
                tpd.initialize(
                        TimePickerFragment.this,
                        calendarType,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND),
                        mode24Hours.isChecked()
                );
            }

            switch (calendarType) {
                case JALALI:
                    tpd.setFont(font);
                    break;
                case GREGORIAN:
                default:
                    tpd.setFont(null);
                    break;
            }
            tpd.setThemeDark(modeDarkTime.isChecked());
            tpd.vibrate(vibrateTime.isChecked());
            tpd.dismissOnPause(dismissTime.isChecked());
            tpd.enableSeconds(enableSeconds.isChecked());
            tpd.setVersion(showVersion2.isChecked() ? Version.VERSION_2 : Version.VERSION_1);
            if (modeCustomAccentTime.isChecked()) {
                tpd.setAccentColor(Color.parseColor("#9C27B0"));
            }
            if (titleTime.isChecked()) {
                switch (calendarType) {
                    case JALALI:
                        tpd.setTitle("عنوان انتخابگر زمان");
                        break;
                    case GREGORIAN:
                    default:
                        tpd.setTitle("TimePicker Title");
                        break;
                }
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
            tpd.setOnCancelListener(dialogInterface -> {
                Log.d("TimePicker", "Dialog was cancelled");
                tpd = null;
            });
            tpd.show(requireFragmentManager(), "Timepickerdialog");
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tpd = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) requireFragmentManager().findFragmentByTag("Timepickerdialog");
        if (tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
        timeTextView.setText(time);
        tpd = null;
    }
}
