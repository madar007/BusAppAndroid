package com.example.mashfique.mapdemo;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpNavigationTabs();

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
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // TODO: remove this temp marker

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(44.975312, -93.226732))
                .title("Hello world"));

        LatLng pos = new LatLng(44.975312, -93.226732);
        Marker melbourne = mMap.addMarker(new MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.defaultMarker()));

        // ************************** Moving a bus marker
        LatLng busLocation = new LatLng(44.975312, -93.226732);     // positioned at Oak St.
        LatLng loc = new LatLng(44.97233, -93.2437);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(loc).title("This is a bus")
                .anchor((float) 0.5, (float) 0.5)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_busmarker)));
        animateMarker(marker, loc, false);          // will go here
        //rotateMarker(marker,(float)90, mMap);

        FetchBusInformationTask fetchBusInfo = new FetchBusInformationTask();
        fetchBusInfo.execute("routeConfig", "umn-twin", "4thst");

    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public void rotateMarker(final Marker marker, final float toRotation, GoogleMap map) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void setUpNavigationTabs() {
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }
        };

        for (int i = 0; i < 3; i++) {
            actionBar.addTab(actionBar.newTab()
                            .setText("Tab " + (i + 1))
                            .setTabListener(tabListener));
        }
    }


    public class FetchBusInformationTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchBusInformationTask.class.getSimpleName();

        /*
            params[0] - command
            params[1] - agency
            params[2] - route
         */
        @Override
        protected String doInBackground(String... params) {
            final int NUM_QUERY_PARAMS = 3;

            if (params.length != NUM_QUERY_PARAMS) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String busInfoXmlStr = null;

            try {
                final String NEXTBUS_BASE_URL = "http://webservices.nextbus.com/service/publicXMLFeed?";
                final String COMMAND_PARAM = "command";
                final String AGENCY_PARAM = "a";
                final String ROUTE_PARAM = "r";

                Uri builtUri = Uri.parse(NEXTBUS_BASE_URL).buildUpon()
                        .appendQueryParameter(COMMAND_PARAM, params[0])
                        .appendQueryParameter(AGENCY_PARAM, params[1])
                        .appendQueryParameter(ROUTE_PARAM, params[2])
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    buffer.append(currentLine + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                busInfoXmlStr = buffer.toString();
                Log.v(LOG_TAG, "Bus info XML String: " + busInfoXmlStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to NextBus API", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return busInfoXmlStr;
        }

        @Override
        protected void onPostExecute(String xmlString) {
            if (xmlString != null) {
                // ************************* Parse route and corresponding stops from xml
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    XMLParser xmlhandler = new XMLParser();
                    saxParser.parse(new InputSource(new StringReader(xmlString)), xmlhandler);

                    setupRouteAndStops(xmlhandler);         // This will set up map with route paths and stops

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /* This function will setup route and corresponding stops for a specific route */
        private void setupRouteAndStops(XMLParser xmlhandler) {
            //  *********************** Drawing polylines:
            ArrayList<ArrayList<LatLng>> routeData = xmlhandler.getRouteData();
            PolylineOptions polylineOptions = new PolylineOptions();

            for (ArrayList<LatLng> array: routeData) {
                polylineOptions.color(Color.RED);
                polylineOptions.width(6);
                polylineOptions.geodesic(false);
                for (LatLng coord: array) {
                    polylineOptions.add(coord);
                }
                mMap.addPolyline(polylineOptions);
                polylineOptions = new PolylineOptions();
            }

            // *********************** Drawing stops:
            ArrayList<BusStop> stopsArray = xmlhandler.getStopsArray();
            for (BusStop stop: stopsArray) {
                String tag = stop.getTag();
                String title = stop.getTitle();
                String shortTitle;
                if ((shortTitle = stop.getShortTitle()) == null) { shortTitle = title; }    // Some stops don't have a short-title
                double latS = stop.getLat();
                double lonS = stop.getLon();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latS, lonS)).title(shortTitle)
                        .anchor((float) 0.5, (float) 0.5)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_stop)));
            }

            //  *********************** Take care of setting bounds :
            LatLngBounds boundsForRoute = xmlhandler.getBoundsForRoute();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsForRoute, 900, 600, 2));
        }

    }
}


