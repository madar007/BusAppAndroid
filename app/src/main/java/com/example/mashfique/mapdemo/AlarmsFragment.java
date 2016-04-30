package com.example.mashfique.mapdemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

public class AlarmsFragment extends Fragment {

    private static AlarmSwipeAdapter mAlarmsAdapter;
    private FloatingActionButton fab;
    private ListView alarms;

    private static Alarm editedAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initFab();
        super.onCreate(savedInstanceState);
    }

    private void initFab() {
        //final Random rand = new Random();
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_alarm_activity, new AddAlarmFragment(), null)
                        .addToBackStack(null)
                        .commit();
                fab.hide();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarms, container, false);
        initListAlarms(rootView);
        fab.show();
        return rootView;
    }

    public void addAlarm(Alarm newAlarm) {
        Toast.makeText(getContext(), "Alarm added!", Toast.LENGTH_SHORT).show();
        mAlarmsAdapter.add(newAlarm);
        mAlarmsAdapter.notifyDataSetChanged();
    }

    private void initListAlarms(View view) {
        if (mAlarmsAdapter == null) {
            mAlarmsAdapter = new AlarmSwipeAdapter(getContext(), new ArrayList<Alarm>());
        }
        alarms = (ListView) view.findViewById(R.id.listview_alarm);
        alarms.setAdapter(mAlarmsAdapter);

        alarms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                editedAlarm = (Alarm) mAlarmsAdapter.getItem(position);
                DialogFragment editAlarmDialog = new EditAlarmDialog();

                editAlarmDialog.show(getActivity().getFragmentManager(), "edit");
                return false;
            }
        });
    }

    public static class AlarmSwipeAdapter extends BaseSwipeAdapter {

        private Context mContext;
        private List<Alarm> mAlarms;

        public AlarmSwipeAdapter(Context context, List<Alarm> alarms) {
            mContext = context;
            mAlarms = alarms;
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.swipe_alarm;
        }

        @Override
        public View generateView(int i, ViewGroup viewGroup) {
            return LayoutInflater.from(mContext).inflate(R.layout.alarms_list_item, null);
        }

        @Override
        public void fillValues(final int position, View view) {
            Alarm currentAlarm = mAlarms.get(position);
            TextView textView = (TextView) view.findViewById(R.id.text_data);
            textView.setText(currentAlarm.toString());

            view.findViewById(R.id.button_alarm_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlarms.remove(position);
                    closeAllItems();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            return mAlarms.size();
        }

        @Override
        public Object getItem(int position) {
            return mAlarms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void add(Alarm newAlarm) {
            mAlarms.add(newAlarm);
            closeItem(getCount()-1);
            notifyDataSetChanged();
        }
    }

    public static class EditAlarmDialog extends DialogFragment {

        private ToggleButton onOffToggle;
        private CheckBox[] days;
        private Button atTime;
        private Spinner beforeSpinner;
        private Spinner frequencySpinner;
        private Button cancel;
        private Button done;
        private int editedAtHour;
        private int editedAtMin;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View view = inflater.inflate(R.layout.dialog_edit_alarm, null);
            builder.setView(view);

            ((TextView) view.findViewById(R.id.dialog_edit_alarm_title)).setText(
                    "Editing " + editedAlarm.getAlarmName() + "\n"
                            + "Bus stop: " + editedAlarm.getBusStop()
            );

            initToggleButton(view);
            initEditDays(view);
            initEditAtTime(view);
            initEditBeforeSpinner(view);
            initEditFrequencySpinner(view);
            initCancel(view);
            initDone(view);

            return builder.create();
        }

        private void initToggleButton(View view) {
            onOffToggle = (ToggleButton) view.findViewById(R.id.dialog_edit_onoff);
            if (editedAlarm.isOn()) {
                onOffToggle.setChecked(true);
            } else {
                onOffToggle.setChecked(false);
            }
        }

        private void initEditDays(View view) {
            initCheckboxes(view);
            boolean[] previousDays = editedAlarm.getDays();
            for (int i = 0; i < previousDays.length; i++) {
                if (previousDays[i]) {
                    days[i].setChecked(true);
                }
            }
        }

        private void initEditBeforeSpinner(View view) {
            beforeSpinner = (Spinner) view.findViewById(R.id.dialog_edit_spinner_alarm_before);

            List<String> minutes = new ArrayList<>();
            for (int i = 0; i < 60; i+=5) {
                minutes.add(Integer.toString(i));
            }

            ArrayAdapter<String> spinnerValues = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                    R.layout.spinner_item_alarm, minutes);

            beforeSpinner.setAdapter(spinnerValues);
            for (int i = 0; i < spinnerValues.getCount(); i++) {
                if (spinnerValues.getItem(i).equals(editedAlarm.getBeforeTime())) {
                    beforeSpinner.setSelection(i);
                    break;
                }
            }
        }

        private void initEditAtTime(View view) {
            atTime = (Button) view.findViewById(R.id.dialog_edit_button_alarm_at);

            final int hour = editedAlarm.getAtHour();
            final int minute = editedAlarm.getAtMin();
            atTime.setText(editedAlarm.getAtTime());

            atTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePicker;
                    timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String chosenTime = UnitsConverter.militaryTo12Hour(hourOfDay, minute);
                            atTime.setText("At " + chosenTime);
                            editedAtHour = hourOfDay;
                            editedAtMin = minute;
                        }
                    }, hour, minute, false);
                    timePicker.setTitle("Select Time");
                    timePicker.show();
                }
            });
        }

        private void initCheckboxes(View view) {
            final int NUM_DAYS = 7;
            days = new CheckBox[NUM_DAYS];
            days[0] = (CheckBox) view.findViewById(R.id.dialog_edit_checkbox_sunday);
            days[1] = (CheckBox) view.findViewById(R.id.dialog_edit_checkbox_monday);
            days[2] = (CheckBox) view.findViewById(R.id.dialog_edit_checkbox_tuesday);
            days[3] = (CheckBox) view.findViewById(R.id.dialog_edit_checkbox_wednesday);
            days[4] = (CheckBox) view.findViewById(R.id.dialog_edit_checkbox_thursday);
            days[5] = (CheckBox) view.findViewById(R.id.dialog_edit_checkbox_friday);
            days[6] = (CheckBox) view.findViewById(R.id.dialog_edit_checkbox_saturday);
        }

        private void initEditFrequencySpinner(View view) {
            frequencySpinner = (Spinner) view.findViewById(R.id.dialog_edit_spinner_alarm_frequency);
            List<String> freqValues = new ArrayList<>();
            freqValues.add("One-time alarm");
            freqValues.add("Every week");
            freqValues.add("Every 2 weeks");
            freqValues.add("Every 3 weeks");

            ArrayAdapter<String> spinnerValues = new ArrayAdapter<>(getActivity().getApplicationContext(),
                    R.layout.spinner_item_alarm, freqValues);

            frequencySpinner.setAdapter(spinnerValues);
            frequencySpinner.setSelection(freqValues.indexOf(editedAlarm.getFrequency()));
        }

        private void initCancel(View view) {
            cancel = (Button) view.findViewById(R.id.dialog_edit_alarm_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        private void initDone(View view) {
            done = (Button) view.findViewById(R.id.dialog_edit_alarm_done);

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeAlarm();
                    mAlarmsAdapter.notifyDataSetChanged();
                    dismiss();
                }
            });
        }

        private void changeAlarm() {
            for (int i = 0; i < days.length; i++) {
                if (days[i].isChecked()) {
                    editedAlarm.addDay(i);
                } else {
                    editedAlarm.removeDay(i);
                }
            }

            if (onOffToggle.isChecked()) {
                editedAlarm.turnOn();
            } else {
                editedAlarm.turnOff();
            }

            editedAlarm.setAtHour(editedAtHour);
            editedAlarm.setAtMin(editedAtMin);
            editedAlarm.setBeforeTime(beforeSpinner.getSelectedItem().toString());
            editedAlarm.setAtTime(atTime.getText().toString());
            editedAlarm.setFrequency(frequencySpinner.getSelectedItem().toString());
        }


    }
}
