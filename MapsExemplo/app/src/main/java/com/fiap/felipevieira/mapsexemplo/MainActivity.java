package com.fiap.felipevieira.mapsexemplo;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,ResultCallback<Status>,OnMapReadyCallback {


    private final String LOG_TAG = "FelipeTestApp";

    private TextView txtNomeUser;
    private TextView txtPontos;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ArrayList<Geofence> mGeofenceList;
    private GoogleMap mMap;
    private Usuario usuario;
    private String caminho;
    private ImageView imgUsuario;

    MarkerOptions mo;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = Constantes.USUARIOS.get(2);



        txtNomeUser = (TextView) findViewById(R.id.txtNomeUser);
        txtPontos = (TextView) findViewById(R.id.txtPontos);
        imgUsuario = (ImageView) findViewById(R.id.imgUsuario);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragmento);



        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .compassEnabled(true)
                .rotateGesturesEnabled(true);

        mo = new MarkerOptions().position(new LatLng(0, 0)).title("Minha localização atual");

        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
        populateGeofenceList();


        txtNomeUser.setText(usuario.getNome().toString());
        txtPontos.setText(String.valueOf(usuario.getPontos()) + " pontos");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker =  mMap.addMarker(mo);


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(2000);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },0);

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        addGeofencesButtonHandler();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "Conexão do GoogleApiClient foi suspensa!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "Conexão do GoogleApiClient falhou!");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, location.toString());


        LatLng mCoordenadas = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(mCoordenadas);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mCoordenadas));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    }

    public void doCheckin(View v){


        Intent iCheckIn = new Intent(this, CheckInActivity.class);
        iCheckIn.putExtra("usuario",usuario);
        startActivityForResult(iCheckIn,0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == -1 && requestCode == 0){
            usuario = (Usuario) data.getSerializableExtra("usuario");
            txtPontos.setText(String.valueOf(usuario.getPontos()) + " pontos");

            Toast.makeText(this,
                    "Check-In Realizado",
                    Toast.LENGTH_SHORT
            ).show();
        }else if(requestCode == 1){

            Intent iCheckIn = new Intent(this, CheckInActivity.class);

            iCheckIn.putExtra("usuario",usuario);
            iCheckIn.putExtra("uri", caminho);
            Log.i("NATURA_POINTS",caminho);
            startActivityForResult(iCheckIn,2);

        }else if(resultCode == -1 && requestCode == 2){
            usuario = (Usuario) data.getSerializableExtra("usuario");
            txtPontos.setText(String.valueOf(usuario.getPontos()) + " pontos");

            Toast.makeText(this,
                    "Check-In com Foto Realizado",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void populateGeofenceList() {

        mGeofenceList = new ArrayList<Geofence>();

        for (Map.Entry<String, LatLng> entry : Constantes.MARCADORES_CHECKIN.entrySet()) {
            Log.i("DADOS_ARRAY",entry.getValue().toString());
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constantes.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constantes.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());

        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofencesTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent())
                    .setResultCallback(MainActivity.this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }


    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.i("NATURA_POINTS","Geofences adicionadas!");
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = Integer.toString(status.getStatusCode());
        }
    }


    //CAMERA

    public void capturarFoto(View v){
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile("foto",".jpg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        caminho = image.getAbsolutePath();

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        startActivityForResult(i, 1);
    }

    /*public void enviar(View v){
        try {
            byte[] b = IOUtils.toByteArray(new FileInputStream(caminho));
            String file = Base64.encodeToString(b, Base64.DEFAULT);

            JSONObject obj = new JSONObject();
            obj.put("proprietario","Felipe");
            obj.put("imagem", file);

            Log.i("NATURA_POINTS",obj.toString());


            final String URL = "http://10.20.63.61:8080/ImobiliariaWeb/ImobiliariaServlet";

            RequestQueue reqQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonReq = new JsonObjectRequest(URL,obj,new Ok(),new Erro());
            reqQueue.add(jsonReq);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private class Erro implements com.android.volley.Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                Toast.makeText(MainActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("ERRO",error.getMessage());
        }
    }

    private class Ok implements com.android.volley.Response.Listener<JSONObject>{

        @Override
        public void onResponse(JSONObject response) {
            try {
                Toast.makeText(MainActivity.this,response.getString("status"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("OK",response.toString());
        }
    }

}
