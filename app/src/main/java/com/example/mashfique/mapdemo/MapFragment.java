package com.example.mashfique.mapdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
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
import com.google.android.gms.maps.model.Polyline;
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
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment
        implements OnMapReadyCallback, AsyncResponse_FetchBusRoute, AsyncResponse_FetchDirections {

    private MapView mMapView;
    private static GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Timer timer;
    private TimerTask timerMarkerTask;
    private Handler animationHandler;

    private Toolbar toolbar;
    private AutoCompleteTextView fromSearch;
    private AutoCompleteTextView toSearch;
    private GooglePlacesAutocompleteAdapter mPlacesAdapter;
    private TabLayout mTabLayout;

    private HashMap<String, Bus> busesMap = new HashMap<String, Bus>();      // Will contain buses for a specific route
    private HashMap<String, Marker> busMarkerMap = new HashMap<String, Marker>();
    int tabPosition = 0;

    private GooglePlacesPrediction from;
    private GooglePlacesPrediction to;

    private BottomSheetBehavior directionsSheet;
    private ArrayAdapter<Step> directionsAdapter;
    private Stack<Step> directionsStack;
    private FloatingActionButton fab_direc_prev;
    private FloatingActionButton fab_direc_next;
    private FloatingActionButton fab_direc_stop;
    private HashMap<String, Polyline> routePolyLines;
    private Marker[] startEndMarker;
    private Marker queryMarker;

    private String favText;

    String RouteData[] = new String[5]; // useful for temporary caching- should probably change later
    String[] routeNames = {"4thst", "connector", "university", "stadium", "stpaul"};
    int count = 0;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        favText = getArguments().getString("favText");
        animationHandler = new Handler();
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_main);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API).build();
        mGoogleApiClient.connect();

        initTabs();
        initBottomSheet();
        initDirectionFabs();
        initMap(savedInstanceState);
        initSearches();
        // Load routes for (temporary) caching:
        loadAllBusRoutes();
        super.onCreate(savedInstanceState);

    }
    void loadAllBusRoutes() {
        for (int i = 0; i < 5; i++) {
            new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", routeNames[i]);
        }
    }
    private void initDirectionFabs() {
        fab_direc_prev = (FloatingActionButton) getActivity().findViewById(R.id.fab_prev_direction);
        fab_direc_next = (FloatingActionButton) getActivity().findViewById(R.id.fab_next_direction);
        fab_direc_stop = (FloatingActionButton) getActivity().findViewById(R.id.fab_stop_direction);

        fab_direc_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!directionsStack.isEmpty()) {
                    Step previousStep = directionsStack.pop();
                    directionsAdapter.insert(previousStep, 0);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(previousStep.getEndLocation(), 17), 1500, null);
                    if (queryMarker != null) {
                        queryMarker.remove();
                    }
                    queryMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(previousStep.getEndLocation())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    directionsAdapter.notifyDataSetChanged();
                }
            }
        });

        fab_direc_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (directionsAdapter.getCount() != 1) {
                    Step currentStep = directionsAdapter.getItem(0);
                    directionsStack.push(currentStep);
                    directionsAdapter.remove(currentStep);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentStep.getEndLocation(), 17f), 1500, null);
                    if (queryMarker != null) {
                        queryMarker.remove();
                    }
                    queryMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(currentStep.getEndLocation())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    directionsAdapter.notifyDataSetChanged();
                }
            }
        });

        fab_direc_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRouteDrawings();
                if (queryMarker != null) {
                    queryMarker.remove();
                }
                queryMarker = null;
                directionsAdapter.clear();
                directionsAdapter.notifyDataSetChanged();
                directionsSheet.setHideable(true);
                directionsSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                fab_direc_next.hide();
                fab_direc_stop.hide();
                fab_direc_prev.hide();
                directionsStack.clear();
                toSearch.setText("");
                fromSearch.setText("");
            }
        });

    }

    private void clearRouteDrawings() {
        if (routePolyLines != null && startEndMarker != null) {
            for (Polyline polyline : routePolyLines.values()) {
                polyline.remove();
            }
            routePolyLines.clear();
            startEndMarker[0].remove();
            startEndMarker[1].remove();
            startEndMarker = null;
        }
    }

    private void initMap(Bundle savedInstanceState) {
        mMapView = (MapView) getActivity().findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);


    }

    private void initBottomSheet() {
        final ListView bottomSheetView = (ListView) getActivity().findViewById(R.id.bottomsheet_main);
        directionsAdapter = new ArrayAdapter<>(getContext(), R.layout.directions_list_item);
        bottomSheetView.setAdapter(directionsAdapter);
        directionsSheet = BottomSheetBehavior.from(bottomSheetView);
        directionsSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        fab_direc_prev.hide();
                        fab_direc_stop.hide();
                        fab_direc_next.hide();
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (!fab_direc_next.isShown()) {
                            fab_direc_next.show();
                            fab_direc_prev.show();
                            fab_direc_stop.show();
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetView.setSelection(0);
                        break;
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
    }

    private void initSearches() {
        final InputMethodManager inputMethodManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        fromSearch = (AutoCompleteTextView) toolbar.getChildAt(0).findViewById(R.id.autocomplete_from_main);
        toSearch = (AutoCompleteTextView) toolbar.getChildAt(0).findViewById(R.id.autocomplete_to_main);
        mPlacesAdapter = new GooglePlacesAutocompleteAdapter(getContext(), R.layout.autocomplete_list_item);

        Log.d("check", "Did you even get here?");


        fromSearch.setAdapter(mPlacesAdapter);
        fromSearch.setThreshold(2);
        toSearch.setAdapter(mPlacesAdapter);
        toSearch.setThreshold(2);


        fromSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from = mPlacesAdapter.getItem(position);
                String selectedPlace = from.getBuildingName();
                fromSearch.setText(selectedPlace);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (!toSearch.getText().toString().matches("")) {
                    queryMarker.remove();
                    showDirections(from.getPlaceID(), to.getPlaceID());
                } else {
                    focusOnSearch(from.getPlaceID());
                }
                fromSearch.clearFocus();
            }
        });

        toSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                to = mPlacesAdapter.getItem(position);
                String selectedPlace = to.getBuildingName();
                toSearch.setText(selectedPlace);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (!fromSearch.getText().toString().matches("")) {
                    queryMarker.remove();
                    showDirections(from.getPlaceID(), to.getPlaceID());
                } else {
                    focusOnSearch(to.getPlaceID());
                }
                toSearch.clearFocus();
            }
        });
        if(favText != null ){
            //to = mPlacesAdapter.autocomplete(favText).get(0);
            toSearch.setText(favText);
            fromSearch.setText("Current Location");


            if (!toSearch.getText().toString().matches("")) {
                showDirections("ChIJWzc6sRcts1IRBK-Dyxa2BGw","ChIJi2l8AD4ts1IR6hQ3FDu_3Qk");
            }
        }


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
                // Check if bus data actually loaded - should change implementation later
                for (int i = 0; i < 5; i++) {
                    if (RouteData[i].length() == 0) {
                        loadAllBusRoutes();
                        break;
                    }
                }
                int tabPos = tab.getPosition();
                tabPosition = tabPos;       // Set member variable for tab position (for using outside this method)
                drawRouteOnTabSelection(tabPosition);
                switch (tabPos) {
                    case 0:
                        // Fetch route
                        //new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "4thst");
                        // Fetch buses
                        long epochTime = System.currentTimeMillis() / 1000L;
                        String epochTimeStr = Long.toString(epochTime);
                        new FetchBusTask(MapFragment.this).execute("vehicleLocations", "umn-twin", "4thst", epochTimeStr);
                        break;
                    case 1:
                        // Fetch route
                        //new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "connector");
                        // Fetch buses
                        epochTime = System.currentTimeMillis() / 1000L;
                        epochTimeStr = Long.toString(epochTime);
                        new FetchBusTask(MapFragment.this).execute("vehicleLocations", "umn-twin", "connector", epochTimeStr);
                        break;
                    case 2:
                        // Fetch route
                        //new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "university");
                        // Fetch buses
                        epochTime = System.currentTimeMillis() / 1000L;
                        epochTimeStr = Long.toString(epochTime);
                        new FetchBusTask(MapFragment.this).execute("vehicleLocations", "umn-twin", "university", epochTimeStr);
                        break;
                    case 3:
                        // Fetch route
                        //new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "stadium");
                        // Fetch buses
                        epochTime = System.currentTimeMillis() / 1000L;
                        epochTimeStr = Long.toString(epochTime);
                        new FetchBusTask(MapFragment.this).execute("vehicleLocations", "umn-twin", "stadium", epochTimeStr);
                        break;
                    case 4:
                        // Fetch route
                        //new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "stpaul");
                        // Fetch buses
                        epochTime = System.currentTimeMillis() / 1000L;
                        epochTimeStr = Long.toString(epochTime);
                        new FetchBusTask(MapFragment.this).execute("vehicleLocations", "umn-twin", "stpaul", epochTimeStr);
                        break;
                    default:
                        break;
                }
                startAnimationTimer();          // take care of the buses
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                stopAnimationTimer();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void focusOnSearch(String placeID) {
        if (queryMarker != null) {
            queryMarker.remove();
        }

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeID)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            Place place = places.get(0);
                            LatLng placeLatLng = place.getLatLng();
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 18), 1500, null);
                            queryMarker = mGoogleMap.addMarker(new MarkerOptions().position(placeLatLng));
                        }
                        places.release();
                    }
                });
    }

    public void fabRefocus(String location) {
        switch (location) {
            case "Current Location":
                focusOnCurrentLocation();
                break;
            case "East Bank":
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.974825, -93.229518), 15f), 1500, null);
                break;
            case "West Bank":
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.971612, -93.241614), 16f), 1500, null);
                break;
            case "St. Paul":
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.984442, -93.1836874), 15f), 1500, null);
                break;
        }
    }

    public void focusOnCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                LatLng currentLocation = new LatLng(lat, lng);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                if (queryMarker != null) {
                    queryMarker.remove();
                }
                queryMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(currentLocation));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation == null) {
            Toast.makeText(getContext(), "Turn on your phone's location services", Toast.LENGTH_SHORT).show();
        } else {
            locationListener.onLocationChanged(lastLocation);
        }
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void processDirectionsResult(List<Route> results) {
        directionsAdapter.clear();
        if ((results != null) && (results.size() > 0)) {
            directionsAdapter.addAll(results.get(0).getListOfSteps());
            drawRoute(results.get(0));
        } else {
            directionsAdapter.add(Step.getErrorStep());
            directionsAdapter.notifyDataSetChanged();
        }
    }

    private void showDirections(String fromPlace_ID, String toPlace_ID) {
        DirectionFetcher fetcher = new DirectionFetcher(fromPlace_ID, toPlace_ID);
        fetcher.fetch(this);
        directionsSheet.setPeekHeight(UnitsConverter.dpToPx(75));
        directionsSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (directionsStack == null) {
            directionsStack = new Stack<>();
        }
        fab_direc_prev.show();
        fab_direc_next.show();
        fab_direc_stop.show();
        directionsSheet.setHideable(false);
    }

    private void drawRoute(Route results) {
        List<Step> steps = results.getListOfSteps();
        if (routePolyLines == null) {
            routePolyLines = new HashMap<>();
        }

        for (Step step : steps) {
            PolylineOptions polylineOptions = step.getPolylineOptions();
            polylineOptions.color(Color.BLUE);
            polylineOptions.width(10);
            polylineOptions.geodesic(false);
            polylineOptions.zIndex(1.0f);
            Polyline polyline = mGoogleMap.addPolyline(polylineOptions);
            routePolyLines.put(polyline.getId(), polyline);
        }

        startEndMarker = new Marker[2];
        startEndMarker[0] = mGoogleMap.addMarker(new MarkerOptions().position(results.getStartLatLng())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        startEndMarker[1] = mGoogleMap.addMarker(new MarkerOptions().position(results.getEndLatLng())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(results.getLatLngBound(), 10), 1500, null);
    }

    private void redrawRoutes() {
        if (routePolyLines != null && routePolyLines.size() > 0) {
            HashMap<String, Polyline> redrawnPolylines = new HashMap<>();
            for (String polyKey : routePolyLines.keySet()) {
                PolylineOptions polylineOption = new PolylineOptions();
                polylineOption.addAll(routePolyLines.get(polyKey).getPoints());
                polylineOption.color(Color.BLUE);
                polylineOption.width(10);
                polylineOption.geodesic(false);

                Polyline polyline = mGoogleMap.addPolyline(polylineOption);
                redrawnPolylines.put(polyline.getId(), polyline);
            }
            routePolyLines = redrawnPolylines;

            Marker startMarker = startEndMarker[0];
            Marker endMarker = startEndMarker[1];

            startEndMarker[0] = mGoogleMap.addMarker(new MarkerOptions().position(startMarker.getPosition())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            startEndMarker[1] = mGoogleMap.addMarker(new MarkerOptions().position(endMarker.getPosition())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        new FetchBusRouteTask(MapFragment.this).execute("routeConfig", "umn-twin", "4thst");

        drawRouteOnTabSelection(tabPosition);
        startAnimationTimer();      // take care of the buses

        if (ContextCompat.checkSelfPermission(mMapView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(false);
        } else {
            // Show rationale and request permission.
        }
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

    void drawRouteOnTabSelection(int tab) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XMLParser xmlhandler = new XMLParser();
            saxParser.parse(new InputSource(new StringReader(RouteData[tab])), xmlhandler);

            mGoogleMap.clear();
            setupRoute(xmlhandler);
            setupStops(xmlhandler);

            //  *********************** Take care of setting bounds :
            LatLngBounds boundsForRoute = xmlhandler.getBoundsForRoute();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsForRoute, 900, 600, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processBusRouteResults(String results) {
        if (results != null) {
            // ************************* Parse route and corresponding stops from xml
            try {
                if (count < 5) {
                    RouteData[count] = results;
                }
                count++;
                if (count == 1) {       // special case- need to fix with cache implementation
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    XMLParser xmlhandler = new XMLParser();
                    saxParser.parse(new InputSource(new StringReader(results)), xmlhandler);

                    mGoogleMap.clear();
                    setupRoute(xmlhandler);
                    setupStops(xmlhandler);

                    //  *********************** Take care of setting bounds :
                    LatLngBounds boundsForRoute = xmlhandler.getBoundsForRoute();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsForRoute, 900, 600, 2));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void processBusResults(String results) {
        if (results != null) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                BusLocationXMLParser busXmlHandler = new BusLocationXMLParser();
                saxParser.parse(new InputSource(new StringReader(results)), busXmlHandler);

                // Make new markers for buses
                if (busesMap.isEmpty()) {
                    busesMap = busXmlHandler.getBusesMap();
                    String id;
                    Bus bus;
                    for (HashMap.Entry<String, Bus> entry : busesMap.entrySet()) {
                        id = entry.getKey();
                        //System.out.println("BUS id: " + id);
                        bus = entry.getValue();
                        MarkerOptions busMarkerOptions = new MarkerOptions()
                                .position(new LatLng(bus.getLat(), bus.getLon())).title("bus: " + bus.getId())
                                .anchor((float) 0.5, (float) 0.5)
                                .rotation((float) bus.getAngle())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_busmarker))
                                .flat(true);
                        Marker busMarker = mGoogleMap.addMarker(busMarkerOptions);  // add marker to maps
                        busMarkerMap.put(id, busMarker);                            // add marker to hashmap
                    }
                } else {  // Update Markers
                    HashMap<String, Bus> tempBusesMap = busXmlHandler.getBusesMap();
                    //System.out.print("size of tempBusesMap: " + tempBusesMap.size() + "\n");

                    String id;
                    Bus bus;
                    // Iterate tempHashMap to compare existing buses in busesMap:
                    for (HashMap.Entry<String, Bus> entry : tempBusesMap.entrySet()) {
                        id = entry.getKey();
                        bus = entry.getValue();
                        if (busMarkerMap.containsKey(id)) {     // Update Bus
                            busesMap.put(id, bus);
                            double latitude = busesMap.get(id).getLat();
                            double longitude = busesMap.get(id).getLon();
                            float angle = (float) busesMap.get(id).getAngle();
                            rotateMarker(busMarkerMap.get(id), angle, mGoogleMap);
                            animateMarker(busMarkerMap.get(id), new LatLng(latitude, longitude), false);
                        } else {      // busesMap doesn't contain a bus, so add it to busesMap and mGoogleMap
                            busesMap.put(id, bus);
                            MarkerOptions busMarkerOptions = new MarkerOptions()
                                    .position(new LatLng(bus.getLat(), bus.getLon())).title("bus: " + bus.getId())
                                    .anchor((float) 0.5, (float) 0.5)
                                    .rotation((float) bus.getAngle())
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_busmarker))
                                    .flat(true);
                            Marker busMarker = mGoogleMap.addMarker(busMarkerOptions);  // add marker to maps
                            busMarkerMap.put(id, busMarker);                            // add marker to hashmap
                        }
                    }
                    // if busesMap has a bus that tempBusesMap doesn't contain, delete that bus
                    for (HashMap.Entry<String, Bus> entry : busesMap.entrySet()) {
                        id = entry.getKey();
                        if (!tempBusesMap.containsKey(id)) {
                            Marker marker = busMarkerMap.get(id);
                            marker.remove();            // remove bus marker from the map
                            busesMap.remove(id);        // remove bus from busesMap
                            busMarkerMap.remove(id);    // remove bus from busMarkerMap (hash map containing markers)
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
//                        Toast toast = Toast.makeText(getContext(), "Update markers", Toast.LENGTH_SHORT);
//                        toast.show();

                        // Update buses:
                        String routeName = "";
                        switch (tabPosition) {          // use tabPosition to determine selected route
                            case 0:
                                routeName = "4thst";
                                break;
                            case 1:
                                routeName = "connector";
                                break;
                            case 2:
                                routeName = "university";
                                break;
                            case 3:
                                routeName = "stadium";
                                break;
                            case 4:
                                routeName = "stpaul";
                                break;
                            default:
                                break;
                        }
                        long epochTime = System.currentTimeMillis() / 1000L;
                        String epochTimeStr = Long.toString(epochTime);
                        new FetchBusTask(MapFragment.this).execute("vehicleLocations", "umn-twin", routeName, epochTimeStr);
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

    // Used for getting bus data
    public static class FetchBusTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchBusTask.class.getSimpleName();
        public AsyncResponse_FetchBusRoute delegate = null;

        public FetchBusTask(MapFragment mapFragment) {
            delegate = mapFragment;
        }

        /*
            params[0] - command
            params[1] - agency
            params[2] - route
            params[3] - epoch/unix time
         */
        @Override
        protected String doInBackground(String... params) {
            final int NUM_QUERY_PARAMS = 4;

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
                final String EPOCH_TIME = "t";

                Uri builtUri = Uri.parse(NEXTBUS_BASE_URL).buildUpon()
                        .appendQueryParameter(COMMAND_PARAM, params[0])
                        .appendQueryParameter(AGENCY_PARAM, params[1])
                        .appendQueryParameter(ROUTE_PARAM, params[2])
                        .appendQueryParameter(EPOCH_TIME, params[3])
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
            //delegate.processBusRouteResults(xmlString);
            delegate.processBusResults(xmlString);
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
