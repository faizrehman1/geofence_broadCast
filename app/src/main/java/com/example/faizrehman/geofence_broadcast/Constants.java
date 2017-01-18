package com.example.faizrehman.geofence_broadcast;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by faizrehman on 1/12/17.
 */

public class Constants  {
    public Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    //public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
    public static final float GEOFENCE_RADIUS_IN_METERS = 40; // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<String, LatLng>();
    static {
        // San Francisco International Airport.
        BAY_AREA_LANDMARKS.put("Office", new LatLng(24.8134516,67.0482245));

        // Googleplex.
        BAY_AREA_LANDMARKS.put("Home", new LatLng(24.926401, 67.088301));

        // Test
        BAY_AREA_LANDMARKS.put("Five star", new LatLng(24.9252498,67.0860951));


//        BAY_AREA_LANDMARKS.put("MeatOne ", new LatLng(24.813602758751703, 67.04840116202831));
////
////        // OFFICE
//        BAY_AREA_LANDMARKS.put("HOME ", new LatLng(24.882545824417782,67.05190446227789));
////
////        //HOTEL
//        BAY_AREA_LANDMARKS.put("Agha ", new LatLng(24.814108846402565,67.050461769104));
////
//        BAY_AREA_LANDMARKS.put("Saylani ", new LatLng(24.88304828447051,67.06828840076923));
//        BAY_AREA_LANDMARKS.put("SSUET-F ", new LatLng(24.935934148512622,67.03469607979059));
//        BAY_AREA_LANDMARKS.put("SSUET-B ", new LatLng(24.91561927070885,67.09298986941576));
//




    }

}
