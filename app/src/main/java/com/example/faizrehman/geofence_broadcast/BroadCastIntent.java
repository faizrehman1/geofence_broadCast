package com.example.faizrehman.geofence_broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by faizrehman on 1/12/17.
 */

public class BroadCastIntent extends BroadcastReceiver {
    protected static final String TAG = "Worker Thread";
    public GoogleApiClient googleApiClient = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"Hello",Toast.LENGTH_LONG).show();

       if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){

        Toast.makeText(context,"Service start in BroadcastReciever",Toast.LENGTH_LONG).show();
        Intent intent1 = new Intent(context,Geofence_Service.class);
        intent1.putExtra("booleanKey",true);
        context.startService(intent1);

          }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String erorMsg = GeofenceErrorMsg.getErrorMessage(context,geofencingEvent.getErrorCode());
            Log.e(TAG, "onHandleIntent: "+erorMsg);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){

            List<Geofence> geoFencingList = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetail = getTransitionDetail(context,geofenceTransition,geoFencingList);
            sendNotification(context,geofenceTransitionDetail);
            Log.i(TAG, "onHandleIntent: "+geofenceTransitionDetail);
        }else{
            Log.e(TAG, "onHandleIntent: "+context.getString(R.string.geofence_transition_invalid_type) );
        }
    }


    public String getTransitionDetail(Context context,int geoTransition,List<Geofence> triggerGeoList){

        String transitionDetailString = getTransitionString(context,geoTransition);
        ArrayList triggerList = new ArrayList();
        for(Geofence geo:triggerGeoList){
            triggerList.add(geo.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggerList);



        return transitionDetailString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(Context context,int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return context.getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return context.getString(R.string.geofence_transition_exited);
            default:
                return context.getString(R.string.unknown_geofence_transition);
        }
    }


    private void sendNotification(Context context,String geofenceTransitionDetail) {
        Intent intentNoti = new Intent(context,MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        // Add the main Activity to the task stack as the parent.
        taskStackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        taskStackBuilder.addNextIntent(intentNoti);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(geofenceTransitionDetail)
                .setContentText(context.getString(R.string.geofence_transition_notification_text))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());

    }

}
