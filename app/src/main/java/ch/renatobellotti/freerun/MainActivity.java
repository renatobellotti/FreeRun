package ch.renatobellotti.freerun;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static int SELECT_GPX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startTrackingActivity(View v){
        Intent intent = new Intent(this, TrackingActivity.class);
        startActivity(intent);
    }

    public void share(View v){
        // let the user select a file
        Intent pickActivityIntent = new Intent(this, GPXSelectorActivity.class);
        startActivityForResult(pickActivityIntent, SELECT_GPX);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SELECT_GPX){
            if(resultCode == RESULT_OK){
                String path = data.getStringExtra(GPXSelectorActivity.SELECTED_PATH_FIELD);
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                //System.out.println(path);
                Log.d("TAG", "Sharing file: " + path);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setData(uri);
                shareIntent.setType("text/xml");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);


                // WARNING: this won't work because the app's internal directory is not publicly available!
                startActivity(Intent.createChooser(shareIntent, getString(R.string.select_send_option)));
            }else{
                // TODO: handle error; for now just ignore the error and act as if nothing had happened
                // *whistling*
            }
        }else{
            // TODO: handle error (because no other request could have been made)
        }
    }
}
