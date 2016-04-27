package com.example.mashfique.mapdemo;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNav;

    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initDrawer();
        initFab();


//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        ImageView test = (ImageView) findViewById(R.id.offline_image);
//        if (mWifi.isConnected()) {
//            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_frame, new MapFragment()).commit();
//            }
//        } else {
//            mDrawerLayout.openDrawer(Gravity.LEFT);
//            test.setVisibility(View.VISIBLE);
//        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerNav = (NavigationView) findViewById(R.id.nav_view_main);
        mDrawerNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int navItem = item.getItemId();

                switch (navItem) {
                    case R.id.nav_alarms:
                        Intent alarmsIntent = new Intent(getApplicationContext(), AlarmsActivity.class);
                        startActivity(alarmsIntent);
                        break;
                    case R.id.nav_favorites:
                    case R.id.nav_offline_maps:
                    case R.id.nav_timetables:
                    case R.id.nav_settings:
                    case R.id.nav_help_feedback:
                    default:
                        String selectedFeature = item.getTitle().toString();
                        Toast sampleToast = Toast.makeText(getApplicationContext(), selectedFeature, Toast.LENGTH_SHORT);
                        sampleToast.show();
                        break;

                }
                return true;
            }
        });
    }

    private void initFab() {
        final DialogFragment newFragment = new FabDialog();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment.show(getFragmentManager(), "fab");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
