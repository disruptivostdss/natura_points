package com.fiap.felipevieira.mapsexemplo;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public static final float GEOFENCE_RADIUS_IN_METERS = 50;

    public static final List<Usuario> USUARIOS = new ArrayList<>();

    static {
        USUARIOS.add(0,new Usuario(1,"José Mariano Leite",0));
        USUARIOS.add(0,new Usuario(2,"Maria Joaquina Silveira",10));
        USUARIOS.add(0,new Usuario(3,"Joana Mariana Bosque",0));
        USUARIOS.add(0,new Usuario(4,"Maria de Fátima Silverado",0));
    }


}
