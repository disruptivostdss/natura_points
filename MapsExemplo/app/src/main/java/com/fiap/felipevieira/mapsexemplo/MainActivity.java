package com.fiap.felipevieira.mapsexemplo;

import android.Manifest;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import static com.fiap.felipevieira.mapsexemplo.R.id.botoesFragment;


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
    private HashMap<String, Coordenada> hashListaCoordenada;
    private FrameLayout botoesFragment;
    private LatLng mCoordenadas;


    MarkerOptions mo;
    Marker marker;
    String nomeGeofence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = Constantes.USUARIOS.get(2);

        //MAPEAMENTO DOS COMPONENTES DA ACTIVITY
        txtNomeUser = (TextView) findViewById(R.id.txtNomeUser);
        txtPontos = (TextView) findViewById(R.id.txtPontos);
        imgUsuario = (ImageView) findViewById(R.id.imgUsuario);
        botoesFragment = (FrameLayout) findViewById(R.id.botoesFragment);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragmento);



        //CONFIGURAÇÃO EXIBIÇÃO FRAGMENTO MAPS
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .compassEnabled(true)
                .rotateGesturesEnabled(true);
        mo = new MarkerOptions().position(new LatLng(0, 0)).title("Minha localização atual");

        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
        carregaCoordenadas();
        nomeGeofence = getIntent().getStringExtra("geofence");

        txtNomeUser.setText(usuario.getNome().toString());
        txtPontos.setText(String.valueOf(usuario.getPontos()) + " pontos");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(mo);
        desenhaFence();


    }

    private Circle geoFenceLimits;

    private void drawGeofence() {


        if (geoFenceLimits != null)
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center(geoFenceMarker.getPosition())
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(Constantes.GEOFENCE_RADIUS_IN_METERS);
        geoFenceLimits = mMap.addCircle(circleOptions);
    }


    private Marker geoFenceMarker;

    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng) {
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if (mMap != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = mMap.addMarker(markerOptions);
        }
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
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(LOG_TAG, "Conexão do GoogleApiClient foi suspensa!");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "Conexão do GoogleApiClient falhou!");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, location.toString());


        mCoordenadas = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(mCoordenadas);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mCoordenadas));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

        desenhaFence();

        int visibilidade = getIntent().getIntExtra("showButton", 4);

        if (visibilidade == 0) {
            botoesFragment.setVisibility(View.VISIBLE);
        } else {
            botoesFragment.setVisibility(View.INVISIBLE);
        }

    }

    public void doCheckin(View v) {



        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Alerta de Check-in");
        b.setMessage("Você está prestes a realizar o Check-in em " + nomeGeofence);
        b.setIcon(R.drawable.logo);
        b.setPositiveButton("Fazer Check-in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                usuario.setPontos(usuario.getPontos() + 5);

                txtPontos.setText(String.valueOf(usuario.getPontos()) + " pontos");
                dialogInterface.dismiss();

            }
        });
        b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog ad = b.create();
        ad.show();




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            Intent iCheckIn = new Intent(this, CheckInActivity.class);

            iCheckIn.putExtra("usuario", usuario);
            iCheckIn.putExtra("uri", caminho);
            iCheckIn.putExtra("stop",nomeGeofence);
            Log.i("NATURA_POINTS", caminho);
            startActivityForResult(iCheckIn, 2);

        } else if (resultCode == -1 && requestCode == 2) {
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

        for (Map.Entry<String, Coordenada> entry : hashListaCoordenada.entrySet()) {
            Log.i("DADOS_ARRAY", Integer.toString(entry.getValue().getId()));
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().getLatitude(),
                            entry.getValue().getLongitude(),
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
        addGeofencesButtonHandler();
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
            Log.i("NATURA_POINTS", "Geofences adicionadas!");

            desenhaFence();

        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = Integer.toString(status.getStatusCode());
        }
    }

    private void desenhaFence() {
        String nomeGeofence = getIntent().getStringExtra("geofence");

        if(nomeGeofence != null && hashListaCoordenada != null) {
            for (Map.Entry<String, Coordenada> entry : hashListaCoordenada.entrySet()) {
                if (nomeGeofence.equals(entry.getKey())) {

                    markerForGeofence(new LatLng(entry.getValue().getLatitude(),
                            entry.getValue().getLongitude()));
                    drawGeofence();

                }
            }

        }
    }


    //CAMERA

    public void capturarFoto(View v) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile("foto", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        caminho = image.getAbsolutePath();

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        startActivityForResult(i, 1);
    }


    public void carregaCoordenadas() {

        final String URL = "http://npoints.mybluemix.net/rest/stop";


        JsonArrayRequest req = new JsonArrayRequest(
                URL,
                new RequestCoordenada(),
                new RequestError());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);

    }


    public class RequestCoordenada implements com.android.volley.Response.Listener<JSONArray> {


        @Override
        public void onResponse(JSONArray response) {
            hashListaCoordenada = new HashMap<String, Coordenada>();

            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    Log.i("COORDENADAS_OBJETO", obj.toString());
                    Coordenada c = new Coordenada(
                            obj.getInt("id"),
                            obj.getString("nome"),
                            obj.getDouble("latitude"),
                            obj.getDouble("longitude"),
                            obj.getString("descricao"));
                    hashListaCoordenada.put(obj.getString("nome"), c);

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            populateGeofenceList();
        }
    }



}
