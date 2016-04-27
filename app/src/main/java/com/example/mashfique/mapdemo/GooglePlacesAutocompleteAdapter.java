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
public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<GooglePlacesPrediction> implements Filterable {

    private ArrayList<GooglePlacesPrediction> resultList;
    private Context context;
    final String LOG_TAG = this.getClass().getSimpleName() + "-autocomplete";

    public GooglePlacesAutocompleteAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public GooglePlacesPrediction getItem(int position) {
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
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public ArrayList<GooglePlacesPrediction> autocomplete(String input) {
        String jsonResults = getJsonResults(input);

        if (jsonResults == null) {
            return resultList;
        }

        //Log.v(LOG_TAG, "JSON results: " + jsonResults);
        try {
            GooglePlacesPrediction prediction;
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

    private GooglePlacesPrediction buildPrediction(JSONObject predictionObject) {
        final String JSON_DESCRIPTION = "description";
        final String JSON_PLACE_ID = "place_id";
        final String JSON_TERMS = "terms";
        final String JSON_VALUE = "value";
        final String MINNEAPOLIS = "Minneapolis";
        final String SAINT_PAUL = "Saint Paul";
        final int VALID_TERM_SIZE = 5;

        try {
            String predDescription = predictionObject.getString(JSON_DESCRIPTION);
            String predPlace_id = predictionObject.getString(JSON_PLACE_ID);
            JSONArray predTerms = predictionObject.getJSONArray(JSON_TERMS);

            List<String> predTermValues = new ArrayList<>();
            for (int i = 0; i < predTerms.length(); i++) {
                String jsonValue = predTerms.getJSONObject(i).getString(JSON_VALUE);
                jsonValue = jsonValue.trim();
                predTermValues.add(jsonValue);
            }

            if (predTermValues.contains(MINNEAPOLIS) || predTermValues.contains(SAINT_PAUL)) {
                if (predTermValues.size() == VALID_TERM_SIZE) {
                    String buildingName = predTermValues.get(0);
                    String street = predTermValues.get(1);
                    String city = predTermValues.get(2);
                    String state = predTermValues.get(3);
                    String country = predTermValues.get(4);

                    return new GooglePlacesPrediction(predDescription, predPlace_id, buildingName,
                            street, city, state, country);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return null;
    }

    private String getJsonResults(String input) {
        final String BASE_URL_PLACES = "https://maps.googleapis.com/maps/api/place";
        final String TYPE_AUTOCOMPLETE = "/autocomplete";
        final String JSON_FORMAT = "/json";
        final String API_KEY = context.getResources().getString(R.string.browser_api_key);
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
