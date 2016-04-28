package com.example.mashfique.mapdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AlarmsFragment extends Fragment {

    private ArrayAdapter<Alarm> mAlarmsAdapter;
    FloatingActionButton fab;


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
        if (mAlarmsAdapter == null) {
            mAlarmsAdapter = new ArrayAdapter<>(getActivity(), R.layout.alarms_list_item, new ArrayList<Alarm>());
        } else {
            Snackbar.make(rootView, "Alarm added!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        ListView listView = (ListView) rootView.findViewById(R.id.listview_alarm);
        listView.setAdapter(mAlarmsAdapter);
        fab.show();
        return rootView;
    }

    public void addAlarm(Alarm newAlarm) {
        mAlarmsAdapter.add(newAlarm);
        mAlarmsAdapter.notifyDataSetChanged();
    }
}
