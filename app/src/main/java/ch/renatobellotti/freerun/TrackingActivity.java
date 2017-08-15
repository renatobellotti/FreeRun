package ch.renatobellotti.freerun;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;


public class TrackingActivity extends Activity {

    private LocationManager manager;
    private GPXGenerator gpx;

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gpx.appendData(location);
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
        setContentView(R.layout.display_current_run_data);

        manager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Log.v("MyTAG", "test");
        String s = getString(R.string.gpx_header);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.removeUpdates(listener);
        try {
            gpx.close();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }
}
