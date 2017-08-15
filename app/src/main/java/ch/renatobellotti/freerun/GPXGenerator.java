package ch.renatobellotti.freerun;

import android.content.Context;
import android.location.Location;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class GPXGenerator {

    private FileOutputStream file;

    public GPXGenerator(Context context, String fileName) throws FileNotFoundException {
        file = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        writeHeader();
    }

    private void writeHeader() {
        // TODO
    }

    public void appendData(Location location){
        // TODO
    }

    public void close() throws IOException {
        file.flush();
        file.close();
    }
}
