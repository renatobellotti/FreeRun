package ch.renatobellotti.freerun;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GPXSelectorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
//public class GPXSelectorActivity extends Activity implements AdapterView.OnItemClickListener{

    static final String SELECTED_PATH_FIELD = "SELECTED_PATH_FIELD";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_gpx);

        // get an array of all filenames formatted in a human readable way
        File directory = getFilesDir();
        String[] filenames = directory.list();
        // TODO: overwrite toString() method so the stamp can be kept but the display is still human readable
        /*for(int i=0; i<filenames.length; ++i){
            // cut off trailing ".gpx" to get the UNIX time stamp
            String basename = filenames[i].replace(".gpx", "");

            long milliSeconds = Long.parseLong(basename) * 1000;
            Date date = new Date(milliSeconds);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy HH:mm:ss");
            filenames[i] = dateFormat.format(date);
        }*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_view, filenames);
        ListView listView = (ListView) findViewById(R.id.fileListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = (ListView) parent;
        String filename = (String) listView.getItemAtPosition(position);
        File directory = getFilesDir();
        String path = directory + "/" + filename;
        Intent result = new Intent();
        result.putExtra(SELECTED_PATH_FIELD, path);
        setResult(RESULT_OK, result);
        finish();
    }
}
