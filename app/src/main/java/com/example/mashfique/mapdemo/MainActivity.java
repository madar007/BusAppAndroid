package com.example.mashfique.mapdemo;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.google.android.gms.maps.SupportMapFragment;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNav;
    private SlidingUpPanelLayout slidingLayout;
    private ListView mDrawerList;
    private ListView sliderList;
    private ArrayAdapter<String>mDrawerAdapter;
    private ArrayAdapter<String>sliderAdapter;
    private TextView textView;

    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, new MapFragment()).commit();
        }
        initToolbar();
        initDrawer();
        initFab();
        initSlideUp();
        textView = (TextView) findViewById(R.id.text);
    }

    private void initSlideUp(){
        final String[] features = {"6 min (0.7 mi)", "Head right onto SE Huron Blvd",
                "Turn left onto Washington Ave SE", "Turn right onto Walnut St SE",
                "Turn left onto Beacon St SE", "Turn left onto Union St SE",
                "Arrived at Kenneth H. Keller Hall"};
        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
       /* sliderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, features);
        sliderList = (ListView) findViewById(R.id.listView);
        sliderList.setAdapter(sliderAdapter);*/
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
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
