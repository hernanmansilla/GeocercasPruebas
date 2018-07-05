package com.example.hernanmansilla.geocercas;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceService extends IntentService
{
    public static final String TAG= "GeofenceService";

    public GeofenceService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        if(event.hasError())
        {

        }
        else
        {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId =geofence.getRequestId();

            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER)
            {
                Toast.makeText(getApplicationContext(), "Entraste a la geocerca", Toast.LENGTH_SHORT).show();
         //       Toast.makeText(contexto_gral, "Entraste a la geocerca", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Saliste de la geocerca", Toast.LENGTH_SHORT).show();
          //      Toast.makeText(contexto_gral, "Saliste de la geocerca", Toast.LENGTH_SHORT).show();
            }

        }


    }
}
