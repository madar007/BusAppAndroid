package com.example.mashfique.mapdemo;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

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
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String xml ="<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n" +
            "<body copyright=\"All data copyright University of Minnesota 2016.\">\n" +
            "<route tag=\"4thst\" title=\"4th Street Circulator\" shortTitle=\"4th Street\" color=\"542614\" oppositeColor=\"ffffff\" latMin=\"44.9705642\" latMax=\"44.981076\" lonMin=\"-93.2458978\" lonMax=\"-93.226732\">\n" +
            "<stop tag=\"19thrive\" title=\"19th Avenue Ramp (Southbound)\" shortTitle=\"19th Ramp (Southbound)\" lat=\"44.9705642\" lon=\"-93.2456879\" stopId=\"41\"/>\n" +
            "<stop tag=\"bleghall\" title=\"Blegen Hall\" lat=\"44.97233\" lon=\"-93.2437\" stopId=\"42\"/>\n" +
            "<stop tag=\"coffunio_e\" title=\"Coffman Union (Eastbound)\" lat=\"44.9735879\" lon=\"-93.235138\" stopId=\"44\"/>\n" +
            "<stop tag=\"oakuniv\" title=\"Oak &amp; Washington (Northbound)\" lat=\"44.975312\" lon=\"-93.226732\" stopId=\"45\"/>\n" +
            "<stop tag=\"ridder\" title=\"Ridder Arena\" lat=\"44.9782439\" lon=\"-93.2301139\" stopId=\"38\"/>\n" +
            "<stop tag=\"4th15th\" title=\"4th St at 15th Ave (Westbound)\" shortTitle=\"4th @ 15th (Westbound)\" lat=\"44.9804618\" lon=\"-93.2356466\" stopId=\"47\"/>\n" +
            "<stop tag=\"10thuniv\" title=\"10th Ave SE at University Ave (Southbound)\" shortTitle=\"10th SE @ University (Southbound)\" lat=\"44.981076\" lon=\"-93.2426629\" stopId=\"48\"/>\n" +
            "<stop tag=\"mondhall\" title=\"Mondale Hall\" lat=\"44.9733128\" lon=\"-93.2458978\" stopId=\"51\"/>\n" +
            "<direction tag=\"loop\" title=\"Loop\" name=\"\" useForUI=\"true\">\n" +
            "  <stop tag=\"19thrive\" />\n" +
            "  <stop tag=\"bleghall\" />\n" +
            "  <stop tag=\"coffunio_e\" />\n" +
            "  <stop tag=\"oakuniv\" />\n" +
            "  <stop tag=\"ridder\" />\n" +
            "  <stop tag=\"4th15th\" />\n" +
            "  <stop tag=\"10thuniv\" />\n" +
            "  <stop tag=\"mondhall\" />\n" +
            "</direction>\n" +
            "<path>\n" +
            "<point lat=\"44.975312\" lon=\"-93.226732\"/>\n" +
            "<point lat=\"44.975624\" lon=\"-93.226476\"/>\n" +
            "<point lat=\"44.97587\" lon=\"-93.2263059\"/>\n" +
            "<point lat=\"44.975966\" lon=\"-93.226257\"/>\n" +
            "<point lat=\"44.9760739\" lon=\"-93.226234\"/>\n" +
            "<point lat=\"44.9761669\" lon=\"-93.226234\"/>\n" +
            "<point lat=\"44.9763\" lon=\"-93.226255\"/>\n" +
            "<point lat=\"44.976427\" lon=\"-93.226301\"/>\n" +
            "<point lat=\"44.976543\" lon=\"-93.2263679\"/>\n" +
            "<point lat=\"44.976645\" lon=\"-93.226456\"/>\n" +
            "<point lat=\"44.976751\" lon=\"-93.226588\"/>\n" +
            "<point lat=\"44.976863\" lon=\"-93.22676\"/>\n" +
            "<point lat=\"44.977022\" lon=\"-93.227062\"/>\n" +
            "<point lat=\"44.977119\" lon=\"-93.227266\"/>\n" +
            "<point lat=\"44.9782439\" lon=\"-93.230114\"/>\n" +
            "</path>\n" +
            "<path>\n" +
            "<point lat=\"44.973312\" lon=\"-93.245898\"/>\n" +
            "<point lat=\"44.972375\" lon=\"-93.2457979\"/>\n" +
            "<point lat=\"44.971171\" lon=\"-93.245686\"/>\n" +
            "</path>\n" +
            "<path>\n" +
            "<point lat=\"44.973588\" lon=\"-93.2351319\"/>\n" +
            "<point lat=\"44.973588\" lon=\"-93.233085\"/>\n" +
            "<point lat=\"44.973602\" lon=\"-93.23032\"/>\n" +
            "<point lat=\"44.973602\" lon=\"-93.228738\"/>\n" +
            "<point lat=\"44.973627\" lon=\"-93.227285\"/>\n" +
            "<point lat=\"44.973631\" lon=\"-93.227253\"/>\n" +
            "<point lat=\"44.97364\" lon=\"-93.227223\"/>\n" +
            "<point lat=\"44.973654\" lon=\"-93.227195\"/>\n" +
            "<point lat=\"44.973673\" lon=\"-93.22717\"/>\n" +
            "<point lat=\"44.9736959\" lon=\"-93.227148\"/>\n" +
            "<point lat=\"44.973722\" lon=\"-93.22713\"/>\n" +
            "<point lat=\"44.973751\" lon=\"-93.227117\"/>\n" +
            "<point lat=\"44.973781\" lon=\"-93.227109\"/>\n" +
            "<point lat=\"44.973813\" lon=\"-93.227106\"/>\n" +
            "<point lat=\"44.974038\" lon=\"-93.227112\"/>\n" +
            "<point lat=\"44.974263\" lon=\"-93.227113\"/>\n" +
            "<point lat=\"44.974488\" lon=\"-93.227107\"/>\n" +
            "<point lat=\"44.974713\" lon=\"-93.227096\"/>\n" +
            "<point lat=\"44.974765\" lon=\"-93.22709\"/>\n" +
            "<point lat=\"44.974815\" lon=\"-93.227078\"/>\n" +
            "<point lat=\"44.974865\" lon=\"-93.22706\"/>\n" +
            "<point lat=\"44.974911\" lon=\"-93.227037\"/>\n" +
            "<point lat=\"44.974955\" lon=\"-93.227008\"/>\n" +
            "<point lat=\"44.975312\" lon=\"-93.226732\"/>\n" +
            "</path>\n" +
            "<path>\n" +
            "<point lat=\"44.981076\" lon=\"-93.2426629\"/>\n" +
            "<point lat=\"44.980516\" lon=\"-93.24311\"/>\n" +
            "<point lat=\"44.980341\" lon=\"-93.243258\"/>\n" +
            "<point lat=\"44.980136\" lon=\"-93.2434339\"/>\n" +
            "<point lat=\"44.980046\" lon=\"-93.2434959\"/>\n" +
            "<point lat=\"44.979952\" lon=\"-93.2435519\"/>\n" +
            "<point lat=\"44.979855\" lon=\"-93.2436029\"/>\n" +
            "<point lat=\"44.979755\" lon=\"-93.243646\"/>\n" +
            "<point lat=\"44.979652\" lon=\"-93.243683\"/>\n" +
            "<point lat=\"44.9795469\" lon=\"-93.243714\"/>\n" +
            "<point lat=\"44.978043\" lon=\"-93.2441489\"/>\n" +
            "<point lat=\"44.977922\" lon=\"-93.244181\"/>\n" +
            "<point lat=\"44.9778019\" lon=\"-93.24422\"/>\n" +
            "<point lat=\"44.977685\" lon=\"-93.244263\"/>\n" +
            "<point lat=\"44.97757\" lon=\"-93.2443119\"/>\n" +
            "<point lat=\"44.977457\" lon=\"-93.244366\"/>\n" +
            "<point lat=\"44.977346\" lon=\"-93.244425\"/>\n" +
            "<point lat=\"44.977217\" lon=\"-93.2445089\"/>\n" +
            "<point lat=\"44.97603\" lon=\"-93.245417\"/>\n" +
            "<point lat=\"44.975931\" lon=\"-93.245492\"/>\n" +
            "<point lat=\"44.975829\" lon=\"-93.24556\"/>\n" +
            "<point lat=\"44.975722\" lon=\"-93.245623\"/>\n" +
            "<point lat=\"44.975613\" lon=\"-93.2456799\"/>\n" +
            "<point lat=\"44.9754999\" lon=\"-93.24573\"/>\n" +
            "<point lat=\"44.975385\" lon=\"-93.245774\"/>\n" +
            "<point lat=\"44.975291\" lon=\"-93.245818\"/>\n" +
            "<point lat=\"44.975195\" lon=\"-93.245857\"/>\n" +
            "<point lat=\"44.975096\" lon=\"-93.24589\"/>\n" +
            "<point lat=\"44.974996\" lon=\"-93.245917\"/>\n" +
            "<point lat=\"44.974894\" lon=\"-93.245937\"/>\n" +
            "<point lat=\"44.974791\" lon=\"-93.245952\"/>\n" +
            "<point lat=\"44.974688\" lon=\"-93.24596\"/>\n" +
            "<point lat=\"44.97425\" lon=\"-93.24596\"/>\n" +
            "<point lat=\"44.973555\" lon=\"-93.2459669\"/>\n" +
            "<point lat=\"44.973522\" lon=\"-93.245964\"/>\n" +
            "<point lat=\"44.973489\" lon=\"-93.245956\"/>\n" +
            "<point lat=\"44.973459\" lon=\"-93.245943\"/>\n" +
            "<point lat=\"44.973431\" lon=\"-93.245924\"/>\n" +
            "<point lat=\"44.973414\" lon=\"-93.245912\"/>\n" +
            "<point lat=\"44.9733949\" lon=\"-93.245903\"/>\n" +
            "<point lat=\"44.973375\" lon=\"-93.245897\"/>\n" +
            "<point lat=\"44.973354\" lon=\"-93.245894\"/>\n" +
            "<point lat=\"44.9733329\" lon=\"-93.245894\"/>\n" +
            "<point lat=\"44.973312\" lon=\"-93.245898\"/>\n" +
            "</path>\n" +
            "<path>\n" +
            "<point lat=\"44.9782439\" lon=\"-93.230114\"/>\n" +
            "<point lat=\"44.978285\" lon=\"-93.230217\"/>\n" +
            "<point lat=\"44.978856\" lon=\"-93.231595\"/>\n" +
            "<point lat=\"44.979408\" lon=\"-93.232999\"/>\n" +
            "<point lat=\"44.9799239\" lon=\"-93.2343099\"/>\n" +
            "<point lat=\"44.980192\" lon=\"-93.234953\"/>\n" +
            "<point lat=\"44.980462\" lon=\"-93.235647\"/>\n" +
            "</path>\n" +
            "<path>\n" +
            "<point lat=\"44.970564\" lon=\"-93.245688\"/>\n" +
            "<point lat=\"44.969684\" lon=\"-93.2457\"/>\n" +
            "<point lat=\"44.969663\" lon=\"-93.245703\"/>\n" +
            "<point lat=\"44.969644\" lon=\"-93.245713\"/>\n" +
            "<point lat=\"44.969629\" lon=\"-93.245729\"/>\n" +
            "<point lat=\"44.96962\" lon=\"-93.245748\"/>\n" +
            "<point lat=\"44.9696179\" lon=\"-93.245769\"/>\n" +
            "<point lat=\"44.969622\" lon=\"-93.24579\"/>\n" +
            "<point lat=\"44.97021\" lon=\"-93.247163\"/>\n" +
            "<point lat=\"44.970218\" lon=\"-93.247178\"/>\n" +
            "<point lat=\"44.970229\" lon=\"-93.247192\"/>\n" +
            "<point lat=\"44.970241\" lon=\"-93.247203\"/>\n" +
            "<point lat=\"44.970256\" lon=\"-93.247212\"/>\n" +
            "<point lat=\"44.970272\" lon=\"-93.247219\"/>\n" +
            "<point lat=\"44.9702879\" lon=\"-93.2472219\"/>\n" +
            "<point lat=\"44.970305\" lon=\"-93.247223\"/>\n" +
            "<point lat=\"44.97122\" lon=\"-93.247216\"/>\n" +
            "<point lat=\"44.971241\" lon=\"-93.247215\"/>\n" +
            "<point lat=\"44.971255\" lon=\"-93.247214\"/>\n" +
            "<point lat=\"44.971269\" lon=\"-93.247212\"/>\n" +
            "<point lat=\"44.971283\" lon=\"-93.247206\"/>\n" +
            "<point lat=\"44.971296\" lon=\"-93.247199\"/>\n" +
            "<point lat=\"44.971307\" lon=\"-93.24719\"/>\n" +
            "<point lat=\"44.971317\" lon=\"-93.247179\"/>\n" +
            "<point lat=\"44.971325\" lon=\"-93.247167\"/>\n" +
            "<point lat=\"44.971331\" lon=\"-93.247154\"/>\n" +
            "<point lat=\"44.971335\" lon=\"-93.24714\"/>\n" +
            "<point lat=\"44.971342\" lon=\"-93.247091\"/>\n" +
            "<point lat=\"44.971354\" lon=\"-93.2470419\"/>\n" +
            "<point lat=\"44.971371\" lon=\"-93.246994\"/>\n" +
            "<point lat=\"44.9713939\" lon=\"-93.246949\"/>\n" +
            "<point lat=\"44.9714219\" lon=\"-93.246906\"/>\n" +
            "<point lat=\"44.971454\" lon=\"-93.246867\"/>\n" +
            "<point lat=\"44.971475\" lon=\"-93.246841\"/>\n" +
            "<point lat=\"44.971492\" lon=\"-93.246812\"/>\n" +
            "<point lat=\"44.971506\" lon=\"-93.246781\"/>\n" +
            "<point lat=\"44.971762\" lon=\"-93.2461189\"/>\n" +
            "<point lat=\"44.9717849\" lon=\"-93.246055\"/>\n" +
            "<point lat=\"44.971803\" lon=\"-93.245991\"/>\n" +
            "<point lat=\"44.971817\" lon=\"-93.245925\"/>\n" +
            "<point lat=\"44.971827\" lon=\"-93.245858\"/>\n" +
            "<point lat=\"44.971834\" lon=\"-93.245821\"/>\n" +
            "<point lat=\"44.971845\" lon=\"-93.245785\"/>\n" +
            "<point lat=\"44.97186\" lon=\"-93.245751\"/>\n" +
            "<point lat=\"44.97188\" lon=\"-93.245718\"/>\n" +
            "<point lat=\"44.971903\" lon=\"-93.245689\"/>\n" +
            "<point lat=\"44.971932\" lon=\"-93.245652\"/>\n" +
            "<point lat=\"44.971956\" lon=\"-93.245611\"/>\n" +
            "<point lat=\"44.971973\" lon=\"-93.245567\"/>\n" +
            "<point lat=\"44.9719849\" lon=\"-93.245521\"/>\n" +
            "<point lat=\"44.972052\" lon=\"-93.245138\"/>\n" +
            "<point lat=\"44.97233\" lon=\"-93.2437\"/>\n" +
            "</path>\n" +
            "<path>\n" +
            "<point lat=\"44.971171\" lon=\"-93.245686\"/>\n" +
            "<point lat=\"44.970564\" lon=\"-93.245688\"/>\n" +
            "</path>\n" +
            "<path>\n" +
            "<point lat=\"44.97233\" lon=\"-93.2437\"/>\n" +
            "<point lat=\"44.97249\" lon=\"-93.24279\"/>\n" +
            "<point lat=\"44.97253\" lon=\"-93.24256\"/>\n" +
            "<point lat=\"44.97277\" lon=\"-93.24132\"/>\n" +
            "<point lat=\"44.97294\" lon=\"-93.24026\"/>\n" +
            "<point lat=\"44.9729699\" lon=\"-93.24006\"/>\n" +
            "<point lat=\"44.97311\" lon=\"-93.23934\"/>\n" +
            "<point lat=\"44.973277\" lon=\"-93.238382\"/>\n" +
            "<point lat=\"44.973421\" lon=\"-93.237573\"/>\n" +
            "<point lat=\"44.973494\" lon=\"-93.237119\"/>\n" +
            "<point lat=\"44.973554\" lon=\"-93.236725\"/>\n" +
            "<point lat=\"44.973569\" lon=\"-93.236562\"/>\n" +
            "<point lat=\"44.973578\" lon=\"-93.236433\"/>\n" +
            "<point lat=\"44.973588\" lon=\"-93.2351319\"/>\n" +
            "</path>\n" +
            "<path>\n" +
            "<point lat=\"44.980462\" lon=\"-93.235647\"/>\n" +
            "<point lat=\"44.9810609\" lon=\"-93.237154\"/>\n" +
            "<point lat=\"44.982671\" lon=\"-93.241191\"/>\n" +
            "<point lat=\"44.982681\" lon=\"-93.2412219\"/>\n" +
            "<point lat=\"44.982686\" lon=\"-93.241256\"/>\n" +
            "<point lat=\"44.982686\" lon=\"-93.241289\"/>\n" +
            "<point lat=\"44.982681\" lon=\"-93.241322\"/>\n" +
            "<point lat=\"44.982671\" lon=\"-93.241354\"/>\n" +
            "<point lat=\"44.982656\" lon=\"-93.241384\"/>\n" +
            "<point lat=\"44.9826369\" lon=\"-93.241411\"/>\n" +
            "<point lat=\"44.981766\" lon=\"-93.242104\"/>\n" +
            "<point lat=\"44.981076\" lon=\"-93.2426629\"/>\n" +
            "</path>\n" +
            "</route>\n" +
            "</body>\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // ************************** Moving a bus marker
        LatLng busLocation = new LatLng(44.975312, -93.226732);     // positioned at Oak St.
        LatLng loc = new LatLng(44.97233, -93.2437);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(busLocation).title("This is a bus")
                .anchor((float) 0.5, (float) 0.5)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_busmarker)));
        //animateMarker(marker, loc, false);          // will go here
        //rotateMarker(marker,(float)90, mMap);

        // ************************* Parse route and corresponding stops from xml
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XMLParser xmlhandler = new XMLParser();
            saxParser.parse(new InputSource(new StringReader(xml)), xmlhandler);

            setupRouteAndStops(xmlhandler);         // This will set up map with route paths and stops

        } catch (Exception e) {
            e.printStackTrace();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsForRoute, 700, 400, 2));
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

    static public void rotateMarker(final Marker marker, final float toRotation, GoogleMap map) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


}
