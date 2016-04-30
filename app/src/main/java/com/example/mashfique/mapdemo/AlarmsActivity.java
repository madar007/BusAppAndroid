package com.example.mashfique.mapdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class AlarmsActivity extends AppCompatActivity
    implements AddAlarmFragment.OnNewAlarmCreationListener{

    private Toolbar toolbar;
    private static AlarmsFragment alarmsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);
        if (savedInstanceState == null) {
            alarmsFragment = new AlarmsFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container_alarm_activity, alarmsFragment).commit();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_alarm);
        toolbar.setTitle("Alarms");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        switch (menuItem) {
            case android.R.id.home:
                if (backStackCount > 0) {
                    toolbar.setTitle("Alarms");
                    getSupportFragmentManager().popBackStack();
                    return true;
                } else {
                    return super.onOptionsItemSelected(item);
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewAlarmCreation(Alarm newAlarm) {
        alarmsFragment.addAlarm(newAlarm);
    }
}
