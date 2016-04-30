package com.example.mashfique.mapdemo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;

public class AlarmsFragment extends Fragment {

    private AlarmSwipeAdapter mAlarmsAdapter;
    private FloatingActionButton fab;
    private ListView alarms;

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
}
