package com.example.mashfique.mapdemo;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ragnarok on 4/24/2016.
 */
public class DirectionFetcher {

    private final String DISTANCE = "distance";
    private final String DURATION = "duration";
    private final String END_LOCATION = "end_location";
    private final String INSTRUCTIONS = "html_instructions";
    private final String POLYLINE = "polyline";
    private final String POINTS = "points";
    private final String START_LOCATION = "start_location";
    private final String STEPS = "steps";
    private final String TRAVEL_MODE = "travel_mode";
    private final String TRAVEL_WALKING = "WALKING";
    private final String TRAVEL_TRANSIT = "TRANSIT";
    private final String TRANSIT_DETAILS = "transit_details";
    private final String JSON_TEXT = "text";
    private final String LAT = "lat";
    private final String LNG = "lng";

    private String fromPlace_ID;
    private String toPlace_ID;

    public DirectionFetcher(String fromPlace_ID, String toPlace_ID) {
        this.fromPlace_ID = fromPlace_ID;
        this.toPlace_ID = toPlace_ID;
    }

    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        private String LOG_TAG = FetchDirectionsTask.class.getSimpleName();


        /*
            params[0] - fromPlace_ID
            params[1] - toPlace_ID
         */
        @Override
        protected String doInBackground(String... params) {
            final String API_KEY = "AIzaSyA29AoYe_6eR-QPHHqnJRX5SSbwbRKSPK8";
            final String ORIGIN_PARAM = "origin";
            final String DEST_PARAM = "destination";
            final String PLACE_ID_PARAM = "place_id:";
            final String MODE_PARAM = "mode";
            final String ALT_PARAM = "alternatives";
            final String KEY_PARAM = "key";
            final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";

            //final String HACK_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJE2e_xBIts1IR1SevMibNscU&destination=place_id:ChIJzeTjMEAts1IRMR4Va4m5-vY&mode=transit&alternatives=true&key=AIzaSyA29AoYe_6eR-QPHHqnJRX5SSbwbRKSPK8";

            String directionsJSON = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(ORIGIN_PARAM, PLACE_ID_PARAM.concat(params[0]))
                        .appendQueryParameter(DEST_PARAM, PLACE_ID_PARAM.concat(params[1]))
                        .appendQueryParameter(MODE_PARAM, "transit")
                        .appendQueryParameter(ALT_PARAM, "true")
                        .appendQueryParameter(KEY_PARAM, API_KEY)
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
                directionsJSON = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to Directions API", e);
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
            return directionsJSON;
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            List<List<DirectionStep>> routes = parseJsonDirections(jsonStr);

            for (List<DirectionStep> route : routes) {

                System.out.println("NEW ROUTE");
                for (DirectionStep step : route) {
                    System.out.println(step.toString());
                }
                System.out.println();
            }
        }
    }

    private List<List<DirectionStep>> parseJsonDirections(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        List<List<DirectionStep>> listOfRoutes = null;
        try {
            final String JSON_ROUTES = "routes";
            List<DirectionStep> route;
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray routeArray = jsonObject.getJSONArray(JSON_ROUTES);
            JSONObject jsonRoute;

            listOfRoutes = new ArrayList<>();
            for (int i = 0; i < routeArray.length(); i++) {
                jsonRoute = routeArray.getJSONObject(i);
                route = parseJsonLeg(jsonRoute);

                if (route != null) {
                    listOfRoutes.add(route);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listOfRoutes;
    }

    private List<DirectionStep> parseJsonLeg(JSONObject jsonRoute) {

        List<DirectionStep> listOfSteps = null;
        try {
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONArray jsonSteps = jsonLegs.getJSONObject(0).getJSONArray(STEPS);

            DirectionStep currentStep;
            listOfSteps = new ArrayList<>();
            for (int i = 0; i < jsonSteps.length(); i++) {
                JSONObject step = jsonSteps.getJSONObject(i);

                if (step.getString(TRAVEL_MODE).equals(TRAVEL_TRANSIT)) {
                    currentStep = parseJsonTransit(step);
                } else {
                    currentStep = parseJsonWalking(step);
                }

                if (currentStep != null) {
                    listOfSteps.add(currentStep);
                } else {
                    listOfSteps = null;
                    return null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listOfSteps;
    }

    private DirectionStep parseJsonWalking(JSONObject walkingStep) {
        DirectionStep step = null;
        try {
            LatLng start_latLng;
            LatLng end_latlng;
            PolylineOptions polylineOptions;
            step = new DirectionStep();

            step.setDistance(walkingStep.getJSONObject(DISTANCE).getString(JSON_TEXT));
            step.setDuration(walkingStep.getJSONObject(DURATION).getString(JSON_TEXT));
            end_latlng = new LatLng(walkingStep.getJSONObject(END_LOCATION).getDouble(LAT),
                    walkingStep.getJSONObject(END_LOCATION).getDouble(LNG));
            step.setEndLocation(end_latlng);
            step.setInstructions(walkingStep.getString(INSTRUCTIONS));

            String encodedPolyLine = walkingStep.getJSONObject(POLYLINE).getString(POINTS);
            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.BLUE);
            polylineOptions.width(10);
            polylineOptions.geodesic(false);
            polylineOptions.addAll(PolyUtil.decode(encodedPolyLine));
            step.setPolylineOptions(polylineOptions);

            start_latLng = new LatLng(walkingStep.getJSONObject(START_LOCATION).getDouble(LAT),
                    walkingStep.getJSONObject(START_LOCATION).getDouble(LNG));
            step.setStartLocation(start_latLng);

            step.setTravelMode(walkingStep.getString(TRAVEL_MODE));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return step;
    }

    private DirectionStep parseJsonTransit(JSONObject transitStep) {
        DirectionStep step = null;
        try {
            LatLng start_latLng;
            LatLng end_latlng;
            PolylineOptions polylineOptions;
            step = new DirectionStep();

            if (parseJsonTransitDetails(step, transitStep.getJSONObject(TRANSIT_DETAILS))) {

                step.setDistance(transitStep.getJSONObject(DISTANCE).getString(JSON_TEXT));
                step.setDuration(transitStep.getJSONObject(DURATION).getString(JSON_TEXT));
                end_latlng = new LatLng(transitStep.getJSONObject(END_LOCATION).getDouble(LAT),
                        transitStep.getJSONObject(END_LOCATION).getDouble(LNG));
                step.setEndLocation(end_latlng);
                step.setInstructions(transitStep.getString(INSTRUCTIONS));

                String encodedPolyLine = transitStep.getJSONObject(POLYLINE).getString(POINTS);
                polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(10);
                polylineOptions.geodesic(false);
                polylineOptions.addAll(PolyUtil.decode(encodedPolyLine));
                step.setPolylineOptions(polylineOptions);

                start_latLng = new LatLng(transitStep.getJSONObject(START_LOCATION).getDouble(LAT),
                        transitStep.getJSONObject(START_LOCATION).getDouble(LNG));
                step.setStartLocation(start_latLng);
                step.setTravelMode(transitStep.getString(TRAVEL_MODE));
            } else {
                step = null;
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return step;
    }

    private boolean parseJsonTransitDetails(DirectionStep step, JSONObject transitDetails) {

        final String ARRIVAL_STOP = "arrival_stop";
        final String ARRIVAL_LOCATION = "location";
        final String ARRIVAL_NAME = "name";

        try {
            if (isCampusBus(transitDetails)) {
                LatLng arrivalLatLng;

                JSONObject arrivalStop = transitDetails.getJSONObject(ARRIVAL_STOP);
                JSONObject location = arrivalStop.getJSONObject(ARRIVAL_LOCATION);
                arrivalLatLng = new LatLng(location.getDouble(LAT), location.getDouble(LNG));
                step.setTransit_arrival_latlng(arrivalLatLng);
                step.setTransit_arrival_name(arrivalStop.getString(ARRIVAL_NAME));
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isCampusBus(JSONObject transitDetails) {
        final String LINE = "line";
        final String AGENCIES = "agencies";
        final String AGENCY_NAME = "name";
        final String UOFMN = "University of Minnesota";

        try {
            return transitDetails.getJSONObject(LINE).getJSONArray(AGENCIES).getJSONObject(0).getString(AGENCY_NAME).equals(UOFMN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void fetch() {
        FetchDirectionsTask newTask = new FetchDirectionsTask();
        newTask.execute(fromPlace_ID, toPlace_ID);
    }

}
