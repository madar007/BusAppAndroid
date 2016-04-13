package com.example.mashfique.mapdemo;

/**
 * Created by Mashfique on 4/1/2016.
 */

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
    This class will help us to parse route paths and stop locations from XML
    This is a bad name for the class. Should be something like StopsAndRouteXMLParser.
*/
public class XMLParser extends DefaultHandler {

    private ArrayList<ArrayList<LatLng>> routeData;         // This will contain coordinates for route polylines
    private ArrayList<LatLng> temp;                         // helper for building routeData
    private boolean startPath = false;                      // helper for building routeData

    private ArrayList<BusStop> stopsArray;                  // will contain stops info for a specific route - refer to BusStop.java for info.
    private LatLngBounds boundsForRoute;                    // This will set the bounds for the map to focus on, when on that specific route

    @Override
    public void startDocument() throws SAXException {
        //Initialize member variables.
        routeData = new ArrayList<ArrayList<LatLng>>();
        temp = new ArrayList<LatLng>();
        stopsArray = new ArrayList<BusStop>();
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase("path")) {
            startPath = true;
        } else if (qName.equalsIgnoreCase("point")) {
            if (startPath) {
                String latStr = attributes.getValue("lat");
                String lonStr = attributes.getValue("lon");
                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);
                LatLng coordinates = new LatLng(lat, lon);
                temp.add(coordinates);
            }
        } else if (qName.equalsIgnoreCase("stop") && attributes.getLength()>1 ) {
            /*  Note : attribute.getValue("attribute-name") gives null when the attribute doesn't exist
                We need to handle that whenever required later in the program.
             */
            String tag = attributes.getValue("tag");
            String title = attributes.getValue("title");
            String shortTitle = attributes.getValue("shortTitle");
            String latStr = attributes.getValue("lat");
            String lonStr = attributes.getValue("lon");

            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);

            BusStop stop = new BusStop(tag,title,shortTitle,lat,lon);
            stopsArray.add(stop);
        } else if (qName.equalsIgnoreCase("route")) {
            String latMinStr = attributes.getValue("latMin");
            String latMaxStr = attributes.getValue("latMax");
            String lonMinStr = attributes.getValue("lonMin");
            String lonMaxStr = attributes.getValue("lonMax");

            double latMin = Double.parseDouble(latMinStr);
            double latMax = Double.parseDouble(latMaxStr);
            double lonMin = Double.parseDouble(lonMinStr);
            double lonMax = Double.parseDouble(lonMaxStr);

            boundsForRoute = new LatLngBounds(
                    new LatLng(latMin, lonMin), new LatLng(latMax, lonMax));;
        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("path")) {
            if (temp.size() > 0) {
                routeData.add(new ArrayList<LatLng>(temp));
            }
            temp.clear();
            startPath = false;
        }
    }

    /* This will send routeData to MapsActivity.java - the code which handles the map */
    public ArrayList<ArrayList<LatLng>> getRouteData() {
        return routeData;
    }

    /* This will send stopsArray to MapsActivity.java */
    public ArrayList<BusStop> getStopsArray() {
        return stopsArray;
    }

    /* This will send bounds for the specific route to MapsActivity.java */
    public LatLngBounds getBoundsForRoute() {
        return boundsForRoute;
    }
}
