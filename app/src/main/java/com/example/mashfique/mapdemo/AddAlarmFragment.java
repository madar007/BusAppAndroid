package com.example.mashfique.mapdemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAlarmFragment extends Fragment {

    private Toolbar toolbar;
    private String activityToolbarTitle;
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
        toolbar.setTitle(activityToolbarTitle);
        getActivity().getSupportFragmentManager().popBackStack();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_alarm, container, false);
        return rootView;
    }

}
