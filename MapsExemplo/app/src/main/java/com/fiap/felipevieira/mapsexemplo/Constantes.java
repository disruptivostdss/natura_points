package com.fiap.felipevieira.mapsexemplo;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by felipe.vieira on 30/08/2017.
 */

public final class Constantes {

    private Constantes() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";


    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    public static final float GEOFENCE_RADIUS_IN_METERS = 40;


    public static final HashMap<String, LatLng> MARCADORES_CHECKIN = new HashMap<String, LatLng>();

    static {
        MARCADORES_CHECKIN.put("CASA", new LatLng(-23.629444, -46.570946));
        MARCADORES_CHECKIN.put("FIAP", new LatLng(-23.574047, -46.623441));
        MARCADORES_CHECKIN.put("LINS X AMARANTE",new LatLng(-23.572936, -46.622789));
        MARCADORES_CHECKIN.put("BOTICÁRIO", new LatLng(-23.573696, -46.622946));
        MARCADORES_CHECKIN.put("COPI", new LatLng(-23.574641, -46.623228));

    }
}
