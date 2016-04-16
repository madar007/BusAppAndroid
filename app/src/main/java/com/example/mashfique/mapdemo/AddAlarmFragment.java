package com.example.mashfique.mapdemo;


import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAlarmFragment extends Fragment {

    private Toolbar toolbar;
    private String activityToolbarTitle;
    private EditText alarmName;
    private EditText busStop;
    private CheckBox[] days;
    private Button atTime;
    private Spinner beforeSpinner;
    private Button frequency;
    private Alarm newAlarm;

    public AddAlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_alarm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_alarm);
        activityToolbarTitle = toolbar.getTitle().toString();
        toolbar.setTitle("New alarm");
        newAlarm = new Alarm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_alarm, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initAtTimeButton(view);
        initBeforeSpinner(view);
        initCheckboxes(view);
        alarmName = (EditText) view.findViewById(R.id.alarm_name_field);
        busStop = (EditText) view.findViewById(R.id.alarm_bus_stop_search);
        frequency = (Button) view.findViewById(R.id.button_alarm_frequency);

    }

    private void initAtTimeButton(View rootView) {
        atTime = (Button) rootView.findViewById(R.id.button_alarm_at);
        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        String badMinuteHack = Integer.toString(minute);

        if (minute < 10) {
            badMinuteHack = "0" + minute;
        }

        if (hour > 12) {
            atTime.setText("At " + (hour - 12) + ":" + badMinuteHack + "PM");
        } else {
            atTime.setText("At " + hour + ":" + badMinuteHack + "AM");
        }

        atTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String period = "AM";
                        if (hourOfDay > 12) {
                            hourOfDay = hourOfDay - 12;
                            period = "PM";
                        }

                        if (minute < 10) {
                            atTime.setText("At " + hourOfDay + ":" + "0" + minute + period);
                        } else {
                            atTime.setText("At " + hourOfDay + ":" + minute + period);
                        }
                    }
                }, hour, minute, false);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });
    }

    private void initBeforeSpinner(View rootView) {
        beforeSpinner = (Spinner) rootView.findViewById(R.id.spinner_alarm_before);

        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 60; i+=5) {
            minutes.add(Integer.toString(i));
        }

        ArrayAdapter<String> spinnerValues = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.spinner_item_alarm, minutes);

        beforeSpinner.setAdapter(spinnerValues);
        beforeSpinner.setSelection(0);

        beforeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newAlarm.setBeforeTime(beforeSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initFrequencyButton() {
        frequency = (Button) getActivity().findViewById(R.id.button_alarm_frequency);
    }

    private void initCheckboxes(View rootView) {
        final int NUM_DAYS = 7;
        days = new CheckBox[NUM_DAYS];
        days[0] = (CheckBox) rootView.findViewById(R.id.checkbox_sunday);
        days[1] = (CheckBox) rootView.findViewById(R.id.checkbox_monday);
        days[2] = (CheckBox) rootView.findViewById(R.id.checkbox_tuesday);
        days[3] = (CheckBox) rootView.findViewById(R.id.checkbox_wednesday);
        days[4] = (CheckBox) rootView.findViewById(R.id.checkbox_thursday);
        days[5] = (CheckBox) rootView.findViewById(R.id.checkbox_friday);
        days[6] = (CheckBox) rootView.findViewById(R.id.checkbox_saturday);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.action_menu_done:
                addAlarm();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addAlarm() {
        newAlarm.setAlarmName(alarmName.getText().toString());
        System.out.println(newAlarm.toString());
        toolbar.setTitle(activityToolbarTitle);
        getActivity().getSupportFragmentManager().popBackStack();
    }


}
