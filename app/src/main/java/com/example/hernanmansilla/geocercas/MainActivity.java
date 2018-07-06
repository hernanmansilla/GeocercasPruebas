package com.example.hernanmansilla.geocercas;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.hernanmansilla.geocercas.GeofenceService.Entre_Geocerca;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String GEOFENCE_ID = "MyGeofenceID";
    public static Context contexto_gral;
    public double Latitud;
    public double Longitud;
    public double Latitud_GPS;
    public double Longitud_GPS;
    public String Latitud_string;
    public String Longitud_string;

    GoogleApiClient googleApiClient = null;

    private Button startLocationMonitoring;
    private Button startGeofenceMonitoring;
    private Button stopGeofenceMonitoring;
    public static EditText Estado_Geocerca;
    private Button Boton_Refresh;
    private EditText Latitud_vista;
    private EditText Longitud_vista;
    private Button Radio;
    private EditText Radio_edit;
    public float Radio_mts=1;
    byte[] Latitud_bytes = new byte[11];
    byte[] Longitud_bytes = new byte[11];

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contexto_gral = getBaseContext();

        startLocationMonitoring = (Button) findViewById(R.id.startLocationMonitoring);
        startGeofenceMonitoring = (Button) findViewById(R.id.startGeofenceMonitoring);
        stopGeofenceMonitoring = (Button) findViewById(R.id.stopGeofenceMonitoring);
        Estado_Geocerca = (EditText) findViewById(R.id.Estado_Geocerca);
        Boton_Refresh = (Button) findViewById(R.id.Boton_actualizar);
        Latitud_vista = (EditText) findViewById(R.id.Latitud);
        Longitud_vista = (EditText) findViewById(R.id.Longitud);
        Radio = (Button) findViewById(R.id.Radio);
        Radio_edit = (EditText) findViewById(R.id.Radio_edit);

        Boton_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Latitud_vista.setText(Double.toString(Latitud_GPS));
                Longitud_vista.setText(Double.toString(Longitud_GPS));

                if(Entre_Geocerca==1)
                {
                    Estado_Geocerca.setText("Entre Geocerca");
                }
                else if(Entre_Geocerca==0)
                {
                    Estado_Geocerca.setText("Sali Geocerca");
                }
            }
        });

        Radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Radio_edit_string = null;

                Radio_edit_string = Radio_edit.getText().toString();

                Radio_mts = Float.parseFloat(Radio_edit_string);

                Toast.makeText(MainActivity.this, "Radio añadido", Toast.LENGTH_SHORT).show();
            }
        });

        startLocationMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationMonitoring();
            }
        });

        startGeofenceMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGeofenceMonitoring();
            }
        });

        stopGeofenceMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopGeofenceMonitoring();
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Toast.makeText(MainActivity.this, "Conectado a la API GOOGLE", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Toast.makeText(MainActivity.this, "Conexion suspendida a la API GOOGLE", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Fallo de conexino a la API GOOGLE", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1234);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(MainActivity.this, "onResume called", Toast.LENGTH_SHORT).show();

        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (response != ConnectionResult.SUCCESS) {
            Toast.makeText(MainActivity.this, "Google play service no disponible", Toast.LENGTH_SHORT).show();
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        } else
        {
            Toast.makeText(MainActivity.this, "Google play service disponible", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Toast.makeText(MainActivity.this, "onStart caller", Toast.LENGTH_SHORT).show();
        googleApiClient.reconnect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Toast.makeText(MainActivity.this, "onStop caller", Toast.LENGTH_SHORT).show();
        googleApiClient.disconnect();
        Entre_Geocerca=2;
        Estado_Geocerca.setText("");
    }

    private void startLocationMonitoring()
    {
        Toast.makeText(MainActivity.this, "StarLocation called", Toast.LENGTH_SHORT).show();

        try
        {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(1000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                 //   Toast.makeText(MainActivity.this, "Ubicacion actualizada", Toast.LENGTH_SHORT).show();

                    Latitud = location.getLatitude();
                    Longitud = location.getLongitude();

                 //   Latitud /= 0.0000001;
                 //   Longitud /= 0.0000001;


                    DecimalFormat df = new DecimalFormat("##.000000");
                    Latitud_string = df.format(Latitud);
                    String Latitud_string_aux = Latitud_string.replace(',','.');
                    Latitud_GPS =  Double.parseDouble(Latitud_string_aux);

                    Longitud_string = df.format(Longitud);
                    Longitud_bytes = Longitud_string.getBytes();
                    String Longitud_string_aux = Longitud_string.replace(',','.');
                    Longitud_GPS =  Double.parseDouble(Longitud_string_aux);

                }
            });
        }catch (SecurityException e) {
            Toast.makeText(MainActivity.this, "Security exception", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGeofenceMonitoring()
    {
        Toast.makeText(MainActivity.this, "StarGeofence called", Toast.LENGTH_SHORT).show();

        try {

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(GEOFENCE_ID)
                    .setCircularRegion(Latitud_GPS,Longitud_GPS,Radio_mts)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                  //  .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
                    .addGeofence(geofence).build();

            Entre_Geocerca=2;
            Estado_Geocerca.setText("");

            Intent intent = new Intent(this, GeofenceService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            if(!googleApiClient.isConnected())
            {
                Toast.makeText(MainActivity.this, "Google API cliente no conectado", Toast.LENGTH_SHORT).show();
            }
            else
            {
              LocationServices.GeofencingApi.addGeofences(googleApiClient,geofencingRequest,pendingIntent)
                        .setResultCallback(new ResultCallback<Status>()
                        {

                            @Override
                            public void onResult(Status status)
                            {
                                if(status.isSuccess())
                                {
                                    Toast.makeText(MainActivity.this, "Geocerca añadida", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Geocerca NO añadida", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                  //  LocationServices.getGeofencingClient();

            }
        }catch (SecurityException e)
        {
            Toast.makeText(MainActivity.this, "Security Exception", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopGeofenceMonitoring()
    {
        Toast.makeText(MainActivity.this, "StaopGeofence called", Toast.LENGTH_SHORT).show();

        ArrayList<String> geofenceIds = new ArrayList<String>();
        geofenceIds.add(GEOFENCE_ID);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient,geofenceIds);
    }

    public static double toDouble(String a){
        int sign = 1;
        int start = 0;
        if(a.charAt(0) == '-'){
            start = 1;
            sign = -1;
        }
        double value = 0;
        boolean decimal = false;
        int exp = 0;

        for(int i = start; i < a.length(); i++){
            if(a.charAt(i) == ','){
                if(decimal) return 0.0;
                decimal = true;
            }else{
                value += (a.charAt(i) - 48) * Math.pow(6,a.length() - i - 1);
            }

            if(decimal) exp++;
        }

        value =  value / Math.pow(6,exp);
        value *= sign;
        System.out.println(value);
        return value;

    }
}
