package ch.renatobellotti.freerun;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int SHARE_REQUEST_CODE = 0;

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
        DialogProperties properties = new DialogProperties();
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.selection_mode = DialogConfigs.MULTI_MODE;
        //properties.root = new File(getStorageDirectory());
        File file = Environment.getExternalStorageDirectory();
        file = new File(file, "Freerun/");
        file.mkdirs();
        Log.w(TAG, file.getAbsolutePath());
        properties.root = file;
        //properties.extensions = new String[]{"gpx"};
        FilePickerDialog dialog = new FilePickerDialog(this, properties);
        dialog.setTitle(getString(R.string.shareMessage));
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
                                              @Override
                                              public void onSelectedFilePaths(String[] files) {
                                                  ArrayList<Uri> uris = new ArrayList<Uri>();
                                                  for(String filename: files){
                                                      uris.add(Uri.fromFile(new File(filename)));
                                                  }

                                                  Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                                  shareIntent.putExtra(Intent.EXTRA_STREAM, uris);
                                                  shareIntent.setType("text/xml");
                                                  startActivity(Intent.createChooser(shareIntent, getString(R.string.shareMessage)));
                                              }
                                          });
                dialog.show();
        //Intent selectFileIntent = new Intent(this, GPXSelectorActivity.class);
        //startActivityForResult(selectFileIntent, SHARE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SHARE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                // actually share the files
                String path = data.getExtras().getString(GPXSelectorActivity.SELECTED_PATH_FIELD);
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                //shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.shareMessage)));
            }else{
                // TODO: handle errors; for now, just ignore the errors
            }
        }else{
            // undefined behaviour: how should this have happened?
            // TODO
        }
    }


}
