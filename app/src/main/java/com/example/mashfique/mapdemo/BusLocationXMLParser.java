package com.example.mashfique.mapdemo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

/**
 * Created by Mashfique on 4/2/2016.
 */

/* This class will be used to parse live bus locations */
public class BusLocationXMLParser extends DefaultHandler {
    HashMap<String, Bus> busesMap;    // key -> bus id, value -> The Bus

    @Override
    public void startDocument() throws SAXException {
        // Initialize member variables
        busesMap = new HashMap<String, Bus>();
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase("vehicle")) {
            String id = attributes.getValue("id");
            String angleStr = attributes.getValue("heading");
            double angle = Double.parseDouble(angleStr);
            String latStr = attributes.getValue("lat");
            String lonStr = attributes.getValue("lon");
            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);
            Bus bus = new Bus(id, angle, lat, lon);
            busesMap.put(id, bus);
        }
    }

    public HashMap<String, Bus> getBusesMap() {
        return busesMap;
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
    }
}
