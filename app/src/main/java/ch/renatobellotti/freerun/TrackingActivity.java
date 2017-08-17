package ch.renatobellotti.freerun;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import phelat.widget.PlusTextView;


public class TrackingActivity extends Activity implements View.OnClickListener{

    private static String TAG = "TrackingActivity";

    private static String PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationManager manager;
    private GPXGenerator gpx;
    private boolean trackingActive;
    private boolean firstLocation;

    //measurement data
    private int time;   // how long the tracking runs (in seconds)
    private int numberOfPoints;
    private float distance;
    private float speedSum;
    private Location lastLoc;

    // triggers GUI update
    // GUI may only be updated from the main thread, that is why a message is sent to it to perform the update there
    private TimerTask updateInterfaceTask = new TimerTask() {
        @Override
        public void run() {
            if(trackingActive){
                updateInterfaceHandler.obtainMessage().sendToTarget();
            }
        }
    };


    // actually updates the GUI
    private static class ExtendedHandler extends Handler{
        // needed to access member variables of the activity while making updateInterfaceHandler static
        // in order to prevent memory leak
        // (handlers of the main thread prevent the activity from being garbage collected)
        static WeakReference<TrackingActivity> weakReference;

        @Override
        public void handleMessage(Message msg){
            // TODO: update the interface
            TrackingActivity activity = weakReference.get();
            // update time
            PlusTextView timeData = activity.findViewById(R.id.timeData);
            timeData.setText(String.format("%1$02d:%2$02d", activity.time/60, activity.time%60));
            ++activity.time;
            //update current speed
            PlusTextView currentSpeedData = activity.findViewById(R.id.currentSpeedData);
            currentSpeedData.setText(String.format("%1$1.2f m/s", activity.lastLoc.getSpeed()));
            //update average speed
            PlusTextView averageSpeedData = activity.findViewById(R.id.averageSpeedData);
            averageSpeedData.setText(String.format("%1$1.2f m/s", activity.speedSum/activity.numberOfPoints));
            // update distance
            PlusTextView distanceData = activity.findViewById(R.id.distanceData);
            distanceData.setText(String.format("%1$1.2f km", activity.distance/1000));
        }
    }

    private ExtendedHandler updateInterfaceHandler;


    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // save for later analysis
            gpx.appendData(location);

            // for display during tracking and calculating of averages on the device
            if(lastLoc == null){
                lastLoc = location;
            }
            ++numberOfPoints;
            speedSum += location.getSpeed();
            distance += location.distanceTo(lastLoc);

            lastLoc = location;


            // only if this is the first run
            if(firstLocation) {
                // now that we have our first data point, we can update the GUI without throwing a NullPointerException
                firstLocation = false;
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(updateInterfaceTask, 0, 1000);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            // TODO
        }

        @Override
        public void onProviderEnabled(String s) {
            // TODO
        }

        @Override
        public void onProviderDisabled(String s) {
            // TODO
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateInterfaceHandler = new ExtendedHandler();
        ExtendedHandler.weakReference = new WeakReference<>(this);

        setContentView(R.layout.while_running);

        // set up reaction to start/stop tracking
        Button button = findViewById(R.id.toggleTracking);
        button.setOnClickListener(this);

        trackingActive = false;
        firstLocation = true;

        // initialize measurement data
        time = 0;
        numberOfPoints = 0;
        distance = 0;
        speedSum = 0;
        lastLoc = null;

        manager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        // Use the UNIX time stamp of the current time as the file name:
        // This is independent of the user's locale settings and can easily be converted to be
        // displayed in the user's current locale even when he/she changes it.
        long timeStamp = System.currentTimeMillis() / 1000L;
        String filename = timeStamp + ".gpx";
        try {
            gpx = new GPXGenerator(this, filename);
        }catch(IOException e){
            // TODO
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deactivateTracking();
        updateInterfaceTask.cancel();
        try {
            gpx.close();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private void activateTracking(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "No permissions to access the GPS location!");
            assert(false);
            return;
        }
        trackingActive = true;
        if(!manager.isProviderEnabled(PROVIDER)){
            // TODO: start dialog to enable GPS
        }
        manager.requestLocationUpdates(PROVIDER, 0, 0, listener);
        Button button = findViewById(R.id.toggleTracking);
        button.setText(R.string.stopTracking);
    }

    private void deactivateTracking(){
        trackingActive = false;
        manager.removeUpdates(listener);
        Button button = findViewById(R.id.toggleTracking);
        button.setText(R.string.continueTracking);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.toggleTracking){
            if(trackingActive){
                deactivateTracking();
            }else{
                activateTracking();
            }
        }else{
            // TODO
            // undefined behaviour: what else could have triggered this method?
            Log.e(TAG, "Undefined behaviour: Unknown source of onClick: " + view.toString());
            assert(false);
        }
    }
}
