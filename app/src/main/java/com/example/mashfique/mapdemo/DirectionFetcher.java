package com.example.mashfique.mapdemo;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
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

    private final String START_ADDR = "start_address";
    private final String END_ADDR = "end_address";
    private final String ARRIVAL_TIME = "arrival_time";
    private final String DEPART_TIME = "departure_time";
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

    private String LOG_TAG = DirectionFetcher.class.getSimpleName();

    public DirectionFetcher(String fromPlace_ID, String toPlace_ID) {
        this.fromPlace_ID = fromPlace_ID;
        this.toPlace_ID = toPlace_ID;
    }

    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        private String LOG_TAG = FetchDirectionsTask.class.getSimpleName();
        private AsyncResponse_FetchDirections context;

        public FetchDirectionsTask(AsyncResponse_FetchDirections context) {
            this.context = context;
        }

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
            List<Route> routes = parseJsonDirections(jsonStr);
            context.processDirectionsResult(routes);
        }
    }

    private List<Route> parseJsonDirections(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        List<Route> routes = null;
        try {
            final String JSON_ROUTES = "routes";
            JSONObject jsonResponse = new JSONObject(jsonString);
            JSONArray jsonRoutes = jsonResponse.getJSONArray(JSON_ROUTES);
            routes = new ArrayList<>();

            JSONObject jsonNewRoute;
            Route newRoute;
            List<Step> steps;
            for (int i = 0; i < jsonRoutes.length(); i++) {
                jsonNewRoute = jsonRoutes.getJSONObject(i);
                newRoute = new Route();
                if (parseJsonRouteSummary(jsonNewRoute, newRoute)) {
                    steps = parseJsonLeg(jsonNewRoute);
                    if (steps != null) {
                        newRoute.addAllSteps(steps);
                        routes.add(newRoute);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return routes;
    }

    private boolean parseJsonRouteSummary(JSONObject jsonRoute, Route route) {

        boolean success = false;
        try {
            if (route != null) {
                JSONObject jsonLeg = jsonRoute.getJSONArray("legs").getJSONObject(0);
                String startAddr = jsonLeg.getString(START_ADDR);
                String endAddr = jsonLeg.getString(END_ADDR);
//                String arrivalTime = jsonLeg.getJSONObject(ARRIVAL_TIME).getString(JSON_TEXT);
//                String departureTime = jsonLeg.getJSONObject(DEPART_TIME).getString(JSON_TEXT);
                String distance = jsonLeg.getJSONObject(DISTANCE).getString(JSON_TEXT);
                String duration = jsonLeg.getJSONObject(DURATION).getString(JSON_TEXT);

                route.setStartAddress(startAddr);
                route.setEndAddress(endAddr);
//                route.setArrival_time(arrivalTime);
//                route.setDeparture_time(departureTime);
                route.setDistance(distance);
                route.setDuration(duration);
                success = true;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return success;
    }

    private List<Step> parseJsonLeg(JSONObject jsonRoute) {

        List<Step> steps = null;
        try {
            JSONObject jsonLeg = jsonRoute.getJSONArray("legs").getJSONObject(0);
            JSONArray jsonSteps = jsonLeg.getJSONArray(STEPS);
            steps = new ArrayList<>();

            JSONObject jsonCurrentStep;
            JSONArray jsonDetailedSteps;
            List<Step> detailedSteps;
            Step currentStep;
            boolean successfulParse = true;
            for (int i = 0; i < jsonSteps.length(); i++) {
                jsonCurrentStep = jsonSteps.getJSONObject(i);

                if (jsonCurrentStep.getString(TRAVEL_MODE).equals(TRAVEL_TRANSIT)) {
                    currentStep = parseJsonTransit(jsonCurrentStep);
                    if (currentStep != null) {
                        steps.add(currentStep);
                    } else {
                        successfulParse = false;
                    }
                } else {
                    jsonDetailedSteps = jsonCurrentStep.getJSONArray(STEPS);
                    detailedSteps = parseJsonDetailSteps(jsonDetailedSteps);
                    if (detailedSteps != null) {
                        steps.addAll(detailedSteps);
                    } else {
                        successfulParse = false;
                    }
                }

                if (!successfulParse) {
                    steps = null;
                    return steps;
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return steps;
    }

    private List<Step> parseJsonDetailSteps(JSONArray jsonDetailedSteps) {
        List<Step> detailedSteps = null;
        try {
            detailedSteps = new ArrayList<>();
            for (int i = 0; i < jsonDetailedSteps.length(); i++) {
                Step currentStep = parseJsonWalking(jsonDetailedSteps.getJSONObject(i));
                if (currentStep != null) {
                    detailedSteps.add(currentStep);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return detailedSteps;
    }

    private Step parseJsonWalking(JSONObject jsonWalking) {
        Step step = null;
        try {
            LatLng start_latLng;
            LatLng end_latlng;
            PolylineOptions polylineOptions;
            step = new Step();

            step.setDistance(jsonWalking.getJSONObject(DISTANCE).getString(JSON_TEXT));
            step.setDuration(jsonWalking.getJSONObject(DURATION).getString(JSON_TEXT));
            end_latlng = new LatLng(jsonWalking.getJSONObject(END_LOCATION).getDouble(LAT),
                    jsonWalking.getJSONObject(END_LOCATION).getDouble(LNG));
            step.setEndLocation(end_latlng);
            step.setInstructions(cleanHtmlDirections(jsonWalking.getString(INSTRUCTIONS)));

            String encodedPolyLine = jsonWalking.getJSONObject(POLYLINE).getString(POINTS);
            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.BLUE);
            polylineOptions.width(10);
            polylineOptions.geodesic(false);
            polylineOptions.addAll(PolyUtil.decode(encodedPolyLine));
            step.setPolylineOptions(polylineOptions);

            start_latLng = new LatLng(jsonWalking.getJSONObject(START_LOCATION).getDouble(LAT),
                    jsonWalking.getJSONObject(START_LOCATION).getDouble(LNG));
            step.setStartLocation(start_latLng);

            step.setTravelMode(jsonWalking.getString(TRAVEL_MODE));

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return step;
    }

    private Step parseJsonTransit(JSONObject jsonTransit) {
        Step step = null;
        try {
            LatLng start_latLng;
            LatLng end_latlng;
            PolylineOptions polylineOptions;
            step = new Step();

            if (parseJsonTransitDetails(step, jsonTransit.getJSONObject(TRANSIT_DETAILS))) {

                step.setDistance(jsonTransit.getJSONObject(DISTANCE).getString(JSON_TEXT));
                step.setDuration(jsonTransit.getJSONObject(DURATION).getString(JSON_TEXT));
                end_latlng = new LatLng(jsonTransit.getJSONObject(END_LOCATION).getDouble(LAT),
                        jsonTransit.getJSONObject(END_LOCATION).getDouble(LNG));
                step.setEndLocation(end_latlng);
                step.setInstructions(jsonTransit.getString(INSTRUCTIONS));

                String encodedPolyLine = jsonTransit.getJSONObject(POLYLINE).getString(POINTS);
                polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(10);
                polylineOptions.geodesic(false);
                polylineOptions.addAll(PolyUtil.decode(encodedPolyLine));
                step.setPolylineOptions(polylineOptions);

                start_latLng = new LatLng(jsonTransit.getJSONObject(START_LOCATION).getDouble(LAT),
                        jsonTransit.getJSONObject(START_LOCATION).getDouble(LNG));
                step.setStartLocation(start_latLng);
                step.setTravelMode(jsonTransit.getString(TRAVEL_MODE));
            } else {
                step = null;
                return null;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return step;
    }

    private boolean parseJsonTransitDetails(Step step, JSONObject transitDetails) {

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
            Log.e(LOG_TAG, e.getMessage());
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
            Log.e(LOG_TAG, e.getMessage());
        }
        return false;
    }

    private String cleanHtmlDirections(String html_direction) {
        if (html_direction != null) {
            return html_direction.replaceAll("\\<.*?\\>", "");
        } else {
            return "";
        }
    }

    public void fetch(AsyncResponse_FetchDirections context) {
        FetchDirectionsTask newTask = new FetchDirectionsTask(context);
        newTask.execute(fromPlace_ID, toPlace_ID);
    }


}
