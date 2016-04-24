package com.example.mashfique.mapdemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, AsyncResponse_FetchBusRoute {

    private MapView mMapView;
    private static GoogleMap mGoogleMap;
    private TabLayout mTabLayout;
    private Timer timer;
    private TimerTask timerMarkerTask;
    private Handler animationHandler;
    private AutoCompleteTextView fromSearch;
    private AutoCompleteTextView toSearch;
    private GooglePlacesAutocompleteAdapter mPlacesAdapter;
    private Toolbar toolbar;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initTabs();
        animationHandler = new Handler();
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        initMap(rootView, savedInstanceState);
        initSearches(rootView);
        return rootView;
    }

    private void initMap(View view, Bundle savedInstanceState) {
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
    }

    private void initSearches(View view) {
        final InputMethodManager inputMethodManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        fromSearch = (AutoCompleteTextView) toolbar.getChildAt(0).findViewById(R.id.autocomplete_from_main);
        toSearch = (AutoCompleteTextView) toolbar.getChildAt(0).findViewById(R.id.autocomplete_to_main);
        mPlacesAdapter = new GooglePlacesAutocompleteAdapter(getContext(), R.layout.autocomplete_list_item);

        fromSearch.setAdapter(mPlacesAdapter);
        fromSearch.setThreshold(2);
        toSearch.setAdapter(mPlacesAdapter);
        toSearch.setThreshold(2);

        fromSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlace = mPlacesAdapter.getItem(position).toString();
                selectedPlace = selectedPlace.split("\n")[0];
                fromSearch.setText(selectedPlace);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });

        toSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlace = mPlacesAdapter.getItem(position).toString();
                selectedPlace = selectedPlace.split("\n")[0];
                toSearch.setText(selectedPlace);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    private void initTabs() {
        mTabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText("4th St."));
        mTabLayout.addTab(mTabLayout.newTab().setText("Connector"));
        mTabLayout.addTab(mTabLayout.newTab().setText("University"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Stadium"));
        mTabLayout.addTab(mTabLayout.newTab().setText("St. Paul"));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPos = tab.getPosition();
                switch (tabPos) {
                    case 0:
                        new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "4thst");
                        break;
                    case 1:
                        new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "connector");
                        break;
                    case 2:
                        new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "university");
                        break;
                    case 3:
                        new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "stadium");
                        break;
                    case 4:
                        new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "stpaul");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                stopAnimationTimer();

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                startAnimationTimer();
            }
        });
    }

    private void placeBusMarkers() {
        // ************************** Fake Buses:
        Marker bus1 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(44.976543, -93.2263679)).title("This is bus1")
                .anchor((float) 0.5, (float) 0.5)
                .rotation((float) 305.0)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_busmarker))
                .flat(true));

        Marker bus2 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(44.975195, -93.245857)).title("This is bus2")
                .anchor((float) 0.5, (float) 0.5)
                .rotation((float) 180.0)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_busmarker))
                .flat(true));

        Marker bus3 = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(44.971342, -93.247091)).title("This is bus3")
                .anchor((float) 0.5, (float) 0.5)
                .rotation((float) 70.0)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_busmarker))
                .flat(true));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "4thst");
    }

    public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mGoogleMap.getProjection();
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

    @Override
    public void processBusRouteResults(String results) {
        if (results != null) {
            // ************************* Parse route and corresponding stops from xml
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                XMLParser xmlhandler = new XMLParser();
                saxParser.parse(new InputSource(new StringReader(results)), xmlhandler);

                mGoogleMap.clear();
                setupRoute(xmlhandler);
                setupStops(xmlhandler);
                placeBusMarkers();
                //  *********************** Take care of setting bounds :
                LatLngBounds boundsForRoute = xmlhandler.getBoundsForRoute();
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsForRoute, 900, 600, 2));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startAnimationTimer() {
        timer = new Timer();
        startAnimation();
        timer.schedule(timerMarkerTask, 1000, 10000);
    }

    private void startAnimation() {
        timerMarkerTask = new TimerTask() {

            @Override
            public void run() {
                animationHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getContext(), "Update markers", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        };
    }

    private void stopAnimationTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /* This function will setup route and corresponding stops for a specific route */
    private void setupRoute(XMLParser xmlhandler) {
        //  *********************** Drawing polylines:
        ArrayList<ArrayList<LatLng>> routeData = xmlhandler.getRouteData();
        PolylineOptions polylineOptions = new PolylineOptions();

        for (ArrayList<LatLng> array : routeData) {
            polylineOptions.color(Color.RED);
            polylineOptions.width(10);
            polylineOptions.geodesic(false);
            for (LatLng coord : array) {
                polylineOptions.add(coord);
            }

            mGoogleMap.addPolyline(polylineOptions);
            polylineOptions = new PolylineOptions();
        }
    }

    private void setupStops(XMLParser xmlhandler) {
        ArrayList<BusStop> stopsArray = xmlhandler.getStopsArray();
        for (BusStop stop : stopsArray) {
            String tag = stop.getTag();
            String title = stop.getTitle();
            String shortTitle;
            if ((shortTitle = stop.getShortTitle()) == null) {
                shortTitle = title;
            }    // Some stops don't have a short-title
            double latS = stop.getLat();
            double lonS = stop.getLon();
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latS, lonS)).title(shortTitle)
                    .anchor((float) 0.5, (float) 0.5)
                    .snippet("3, 8, 15 (Minutes)")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_stop)));
        }
    }

    public static class FetchBusRouteTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchBusRouteTask.class.getSimpleName();
        public AsyncResponse_FetchBusRoute delegate = null;

        public FetchBusRouteTask(MapFragment mapFragment) {
            delegate = mapFragment;
        }

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
                //Log.v(LOG_TAG, "Bus info XML String: " + busInfoXmlStr);
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
            delegate.processBusRouteResults(xmlString);
        }

    }
}
