package com.example.mashfique.mapdemo;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ragnarok on 4/15/2016.
 */
public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;
    final String LOG_TAG = this.getClass().getSimpleName() + "-autocomplete";

    public GooglePlacesAutocompleteAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    resultList = autocomplete(constraint.toString());
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();;
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public ArrayList<String> autocomplete(String input) {
        String jsonResults = getJsonResults(input);

        if (jsonResults == null) {
            return resultList;
        }

        //Log.v(LOG_TAG, "JSON results: " + jsonResults);
        try {
            String prediction;
            JSONObject jsonObject = new JSONObject(jsonResults.toString());
            JSONArray predJsonArray = jsonObject.getJSONArray("predictions");
            resultList = new ArrayList<>();

            for (int i = 0; i < predJsonArray.length(); i++) {
                prediction = buildPrediction(predJsonArray.getJSONObject(i));
                if (prediction != null) {
                    resultList.add(prediction);
                }
                //resultList.add(predJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    private String buildPrediction(JSONObject predictionObject) {
        final String JSON_DESCRIPTION = "description";
        final String MINNEAPOLIS = "Minneapolis";
        final String SAINT_PAUL = "Saint Paul";
        String prediction = null;

        try {
            String predString = predictionObject.getString(JSON_DESCRIPTION);
            List<String> predTokens = Arrays.asList(predString.split(","));
            String city = predTokens.get(2).trim();

            if (city.contentEquals(MINNEAPOLIS) || city.equals(SAINT_PAUL)) {
                prediction = predTokens.get(0);
                prediction = prediction.concat("\n");
                prediction = prediction.concat(predTokens.get(1));
                prediction = prediction.concat(", ");
                prediction = prediction.concat(predTokens.get(2));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return prediction;
    }

    private String getJsonResults(String input) {
        final String BASE_URL_PLACES = "https://maps.googleapis.com/maps/api/place";
        final String TYPE_AUTOCOMPLETE = "/autocomplete";
        final String JSON_FORMAT = "/json";
        final String API_KEY = "AIzaSyA29AoYe_6eR-QPHHqnJRX5SSbwbRKSPK8";
        final String LOCATION = "&location=44.974127+-93.227889";
        final String RADIUS = "&radius=8000";

        HttpURLConnection connection = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(BASE_URL_PLACES + TYPE_AUTOCOMPLETE + JSON_FORMAT);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:US");
            sb.append(LOCATION);
            sb.append(RADIUS);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            //Log.v(LOG_TAG, "built url: " + sb);

            URL url = new URL(sb.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            int read;
            char[] buffer = new char[1024];
            while ((read = in.read(buffer)) != -1) {
                jsonResults.append(buffer, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return jsonResults.toString();
    }
}
