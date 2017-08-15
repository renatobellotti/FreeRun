package ch.renatobellotti.freerun;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;


public class TrackingActivity extends Activity {

    private static final int BUFFER_SIZE = 20;
    private Location[]  buffer = new Location[BUFFER_SIZE];
    private int currentIndex = 0;

    private LocationManager manager;

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // TODO
            if(currentIndex != BUFFER_SIZE){
                buffer[currentIndex] = location;
                ++currentIndex;
            }else{
                // buffer is full
                // TODO
                // write all buffered data to a file
                currentIndex = 0;
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
        setContentView(R.layout.display_current_run_data);

        manager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
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
    }
}
