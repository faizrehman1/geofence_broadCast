package com.example.faizrehman.geofence_broadcast;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by faizrehman on 1/12/17.
 */

public class Geofence_Service extends Service implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,LocationListener, ResultCallback<Status> {
    public GoogleApiClient mGoogleApiClient = null;
    private List<Model> sqlGeofenceArrayList;
    private PendingIntent pendingIntent;
    private DatabaseReference mDatabase;
    protected static final String TAG = "Service:";
    private Geofence_Sqlite geo_Sqlite;
    private ArrayList<Geofence> arrayListGeo;
    private static Geofence_Service geofence_service;
    private boolean isCheckforAddfence;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Geofence_Service getInstance(){
        return geofence_service;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"service Started",Toast.LENGTH_LONG).show();
        int i=0;

        if(intent!=null) {
            if(intent.hasExtra("booleanKey")) {
                isCheckforAddfence = intent.getBooleanExtra("booleanKey", false);
            }else{
                isCheckforAddfence = false;
            }
            if(mGoogleApiClient==null){
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            }else if(!mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
                Intent intentt = new Intent(this,Geofence_Service.class);
                intentt.putExtra("booleanKey",true);
                startService(intentt);
            } else if(mGoogleApiClient.isConnected() && isCheckforAddfence){
                populateGeofenceList();
                AddGeofenceHandler(

                );
            }
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"onCreate Service",Toast.LENGTH_LONG).show();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        sqlGeofenceArrayList = new ArrayList<>();
        arrayListGeo = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        geo_Sqlite = new Geofence_Sqlite(this);

        if(isNetworkConnected()) {
            mDatabase.child("geofence").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i(TAG, "onChildAdded: ");
                    Model model = dataSnapshot.getValue(Model.class);
                    if (sqlGeofenceArrayList.size() == 0) {
                        geo_Sqlite.Addvalue(model);
                    } else {
                        for (int i = 0; i < sqlGeofenceArrayList.size(); i++) {
                            if (sqlGeofenceArrayList.get(i).getFenceKey().equals(model.getFenceKey())) {
                                Toast.makeText(Geofence_Service.this, model.getFenceKey() + "is Already added", Toast.LENGTH_SHORT).show();
                            } else {
                                geo_Sqlite.Addvalue(model);
                                Toast.makeText(Geofence_Service.this, "add value in sqlite", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.i(TAG, "onChildChanged: ");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "onChildRemoved: ");
                    Model model = dataSnapshot.getValue(Model.class);
                    for(int i=0;i<sqlGeofenceArrayList.size();i++){
                        if(sqlGeofenceArrayList.get(i).getFenceKey().equals(model.getFenceKey())){
                            geo_Sqlite.deleteGeofence(model.getFenceKey());
                            Toast.makeText(Geofence_Service.this,model.getFenceKey()+"updated",Toast.LENGTH_SHORT).show();
                        }
                    }

                    }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.i(TAG, "onChildMoved: ");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    public void populateGeofenceList() {
        sqlGeofenceArrayList = geo_Sqlite.getData();
        arrayListGeo.clear();
        Toast.makeText(this,"getGeofences from sqllite",Toast.LENGTH_SHORT).show();
        for (Model model : sqlGeofenceArrayList) {
            arrayListGeo.add(new Geofence.Builder()
                    .setRequestId(model.getPlace())
                    .setCircularRegion(
                            model.getLatitude(),
                            model.getLongitude(),
                            model.getRadius()

                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
       mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(arrayListGeo);
        return builder.build();
    }

    private PendingIntent getPendingIntent() {

        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, BroadCastIntent.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    protected void AddGeofenceHandler() {
        if (!mGoogleApiClient.isConnected() || arrayListGeo.isEmpty()) {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getPendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if(status.isSuccess()){
            Toast.makeText(this,"Geofence Addedd",Toast.LENGTH_SHORT).show();
        }else{
            String geofenceErro = GeofenceErrorMsg.getErrorMessage(this,status.getStatusCode());
            Log.e("Final Result", "onResult: "+geofenceErro);
        }
    }

}


