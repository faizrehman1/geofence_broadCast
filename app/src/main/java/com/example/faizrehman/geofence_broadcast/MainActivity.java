package com.example.faizrehman.geofence_broadcast;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener, ResultCallback<Status> {

    private GoogleApiClient mGoogleApiClient;
    private Button btnAddfence,addToFirebase;
    private ArrayList<Geofence> geofenceArrayList;
    private PendingIntent pendingIntent;
    private BroadCastIntent broadCastIntent;
    private DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geofenceArrayList = new ArrayList<>();
        btnAddfence = (Button)findViewById(R.id.add_geofence);
        addToFirebase = (Button)findViewById(R.id.add_geofencetofire);
        broadCastIntent = new BroadCastIntent();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final Intent intent = new Intent(this,BroadCastIntent.class);
       // buildGoogleApiClient();
        registerReceiver(broadCastIntent, new IntentFilter("android.intent.action.BOOT_COMPLETED"));

        addToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateGeofenceList();
            }
        });

        btnAddfence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // AddGeofenceHandler();
                sendBroadcast(intent);
                Intent intent1 = new Intent(MainActivity.this,Geofence_Service.class);
                intent1.putExtra("booleanKey",true);
                MainActivity.this.startService(intent1);
            }
        });
    }
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }

    protected void AddGeofenceHandler(){
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

//        try {
//            LocationServices.GeofencingApi.addGeofences(
//                    mGoogleApiClient,
//                    // The GeofenceRequest object.
//                    getGeofencingRequest(),
//                    // A pending intent that that is reused when calling removeGeofences(). This
//                    // pending intent is used to generate an intent when a matched geofence
//                    // transition is observed.
//                    getPendingIntent()
//            ).setResultCallback(this); // Result processed in onResult().
//        } catch (SecurityException securityException) {
//            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
//        }
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {

            geofenceArrayList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
            String Pushref = mDatabase.child("geofence").push().getKey();
                    mDatabase.child("geofence").child(Pushref).setValue(new Model(entry.getValue().longitude,entry.getValue().latitude,Constants.GEOFENCE_RADIUS_IN_METERS,Pushref,entry.getKey()));


        }
    }

//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(geofenceArrayList);
//        return builder.build();
//    }

//    private PendingIntent getPendingIntent(){
//
//        if(pendingIntent!=null){
//            return pendingIntent;
//        }
//        Intent intent  = new Intent(this,BroadCastIntent.class);
//        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadCastIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResult(@NonNull Status status) {

    }
}
