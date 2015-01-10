package com.sedanza;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by anthony on 1/10/15.
 */
public class DataService extends Service {
    public static final String SERVICE_NAME = "com.sedanza.DataService";

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }

    public static final int INTERVAL_QUERY = 1000 * 60 * 5; // 5 minutes
    public static final int INTERVAL_TEST = 5; // Number of queries before sending notification
    public static final float ACCEL_THRESHOLD = 0.4f;
    public static final int ACTIVE_PERIODS_THRESHOLD = 2;

    int queryCount = 0;
    int activePeriods = 0;

    @Override
    public void onCreate() {
        final DataCollect collector = new DataCollect(this);
        collector.start();



        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                float accel = collector.getAccelerometerAvg();
                collector.clearAccelerometer();

                queryCount++;

                if (accel > ACCEL_THRESHOLD) activePeriods++;


                Log.d("SERVICE","Accel: "+accel+" Query Count: "+queryCount);

                if (queryCount % INTERVAL_TEST == 0){
                    if (activePeriods < ACTIVE_PERIODS_THRESHOLD){
                        sendNotification();
                        Log.d("SERVICE","Sending notification");
                    }
                    Log.d("SERVICE","Active periods: "+activePeriods+" Query Count: "+queryCount);
                    activePeriods = 0;
                    queryCount = 0;
                }

            }
        },INTERVAL_QUERY);
    }

    public void sendNotification(){
        int notificationId = 001;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
//        viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Get up")
                        .setContentText("You have been sitting for too long")
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
}
