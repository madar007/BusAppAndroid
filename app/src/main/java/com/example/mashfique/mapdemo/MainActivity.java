package com.example.mashfique.mapdemo;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Set;


public class MainActivity extends AppCompatActivity implements FocusFabDialog.OnSelectedFocusListener{

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNav;

    private Toolbar toolbar;
    private FloatingActionButton fab;
    String favorite = null;
    private MapFragment mapFragment;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initDrawer();
        initFab();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        ImageView offlineImage = (ImageView) findViewById(R.id.offline_image);
//        if (mWifi.isConnected()) {
//            if (savedInstanceState == null) {
                mapFragment = new MapFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mapFragment).commit();
//            }
//        } else {
//            mDrawerLayout.openDrawer(Gravity.LEFT);
//            test.setVisibility(View.VISIBLE);
//        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    /* Implementation of Internet Connectivity Listener- Works for both WIFI and Cellular Data
     * Source: https://github.com/CammyKamal/BlogTutorials/tree/master/InternetCheckAndSnackBar
     * */
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    //private CoordinatorLayout coordinatorLayout;
    private boolean internetConnected=true;

    // Method to register runtime broadcast receiver to show snackbar alert for internet connection..
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    // Runtime Broadcast receiver inner class to capture internet connectivity event
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    private void setSnackbarMessage(String status,boolean showBar) {
        String internetStatus = "";
        if (status.equalsIgnoreCase("Wifi enabled") || status.equalsIgnoreCase("Mobile data enabled")) {
            internetStatus = "Internet Connected";
            snackbar = Snackbar
                    .make(coordinatorLayout, internetStatus, Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //snackbar.dismiss();
                        }
                    });
        } else {
            internetStatus = "Internet Connection Lost - Working Offline";
            snackbar = Snackbar
                    .make(coordinatorLayout, internetStatus, Snackbar.LENGTH_INDEFINITE);
        }

        // Changing message text color
        //snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        //View sbView = snackbar.getView();
        //TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        //textView.setTextColor(Color.WHITE);
        if (internetStatus.equalsIgnoreCase("Internet Connection Lost - Working Offline")) {
            if (internetConnected) {
                snackbar.show();
                internetConnected = false;
            }
        } else {
            if (!internetConnected) {
                internetConnected = true;
                snackbar.show();
            }
        }
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
                        //setContentView(R.layout.activity_favorites);
                        Intent favIntent = new Intent(getApplicationContext(), FavoritesActivity.class );
                        startActivity(favIntent);
                        break;
                    case R.id.nav_offline_maps:
                    case R.id.nav_timetables:
                        Intent timeTableIntent = new Intent(getApplicationContext(), TimeTableActivity.class);
                        startActivity(timeTableIntent);
                        break;
                    case R.id.nav_settings:
                        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;
                    case R.id.nav_help_feedback:
                        Intent helpIntent = new Intent(getApplicationContext(), HelpActivity.class);
                        startActivity(helpIntent);
                        break;
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
        final DialogFragment newFragment = new FocusFabDialog();
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

    @Override
    public void onSelectedFocus(String location) {
        mapFragment.fabRefocus(location);
    }
}
