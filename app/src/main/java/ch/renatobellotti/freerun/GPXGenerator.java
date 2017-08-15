package ch.renatobellotti.freerun;

import android.content.Context;
import android.location.Location;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;


class GPXGenerator {

    private PrintWriter file;

    GPXGenerator(Context context, String fileName) throws FileNotFoundException {
        file = new PrintWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
        writeHeader();
    }

    private void writeHeader() {
        // parts of the header were copied from https://de.wikipedia.org/wiki/GPS_Exchange_Format
        file.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
        file.write("\n");
        file.write(String.format(
                "<gpx version=\"1.1\" creator=\"Freerun Android App v%1\">",
                BuildConfig.VERSION_CODE
        ));
        file.write("\n");
        file.write("\t<trk>");
    }

    /** Add another track point to the GPX file.
     *
     * For more information and examples about the GPX format, see https://de.wikipedia.org/wiki/GPS_Exchange_Format.
     *
     * @param location The data to append as a track point
     */
    void appendData(Location location){
        // 1: latitude, 2: longitude, 3: attributes
        final String TRACKPOINT_FORMAT = "\t\t<trkpoint lat=\"%1\" lon=\"%2\">\n%3\t\t</trkpoint>";

        // attributes
        final String ALTITUDE_FORMAT = "\t\t\t<ele>%1</ele>\n";
        final String TIME_FORMAT = "\t\t\t<time>%1</time>\n";
        final String NUMBER_OF_SAT_FORMAT = "\t\t\t<sat>%1</sat>\n";
        final String SPEED_FORMAT = "\t\t\t<speed>%1</speed>";

        StringBuilder attributes = new StringBuilder();
        attributes.append(String.format(ALTITUDE_FORMAT, location.getAltitude()));
        attributes.append(String.format(NUMBER_OF_SAT_FORMAT, location.getExtras().getCharArray("satellites")));
        attributes.append(String.format(TIME_FORMAT, location.getTime()));
        attributes.append(String.format(SPEED_FORMAT, location.getSpeed()));

        // TODO: include "horizontal dilution of precision" as an attribute
        // TODO: include speed uncertainty as an attribute [NOT IN THE STANDARD!]

        String trackpoint = String.format(TRACKPOINT_FORMAT, location.getLatitude(), location.getLongitude(), attributes);
    }

    public void close() throws IOException {
        file.write("\t</trk>");
        file.write("</gpx>");
        file.flush();
        file.close();
    }
}
