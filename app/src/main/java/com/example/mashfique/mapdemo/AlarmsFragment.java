package com.example.mashfique.mapdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AlarmsFragment extends Fragment {

    private ArrayAdapter<String> mAlarmsAdapter;
    private String[] fakeAlarms = {"Coffman - MWF - 1:00pm - 10 minutes before",
            "Cybertron - F - 2:00pm - 10 days before",
            "Planet of the Apes - MT - 6:30pm - 5 minutes before"
    };
    FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initFab();
        super.onCreate(savedInstanceState);
    }

    private void initFab() {
        final Random rand = new Random();
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlarmsAdapter.add(fakeAlarms[rand.nextInt(3)]);
                mAlarmsAdapter.notifyDataSetChanged();
                Snackbar.make(view, "Alarm added!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarms, container, false);
        mAlarmsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        ListView listView = (ListView) rootView.findViewById(R.id.listview_alarm);
        listView.setAdapter(mAlarmsAdapter);
        return rootView;
    }



}
