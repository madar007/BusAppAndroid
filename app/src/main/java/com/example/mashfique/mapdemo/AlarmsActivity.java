package com.example.mashfique.mapdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class AlarmsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container_alarm_activity, new AlarmsFragment()).commit();
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar_alarm);
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
}
