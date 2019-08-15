package ch.renatobellotti.freerun;

import android.location.Location;
import android.os.Bundle;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;


/** Generate a GPX gpxFile out of Location objects.
 *
 *  More information about GPX can be found at: http://www.topografix.com/gpx_manual.asp
 */
class GPXGenerator {

    private PrintWriter gpxFile;
    private PrintWriter csvFile;

    GPXGenerator(OutputStream gpxFileOutput, OutputStream csvFileOutput) {
        // write header to GPX file
        gpxFile = new PrintWriter(gpxFileOutput);
        writeHeader();

        // write header to CSV file
        csvFile = new PrintWriter(csvFileOutput);
        csvFile.write("Time, Latitude, Longitude, Altitude, Number of Satellites\n");
    }

    private void writeHeader() {
        // parts of the header were copied from https://de.wikipedia.org/wiki/GPS_Exchange_Format
        gpxFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
        gpxFile.write("\n");
        gpxFile.write(String.format(
                "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" version=\"1.1\" creator=\"Freerun Android App v%1$s\">",
                BuildConfig.VERSION_CODE
        ));
        gpxFile.write("\n");
        gpxFile.write("\t<trk>\n");
        gpxFile.write("\t\t<trkseg>\n");
    }

    // TODO: change this so that the indvidual values are passed instead on a Location object
    // that way this class could also be used without the Android platform
    /** Add another track point to the GPX gpxFile.
     *
     * For more information and examples about the GPX format, see https://de.wikipedia.org/wiki/GPS_Exchange_Format.
     *
     * @param location The data to append as a track point
     */
    void appendData(Location location){
        // 1: latitude, 2: longitude, 3: attributes
        final String TRACKPOINT_FORMAT = "\t\t<trkpt lat=\"%1$s\" lon=\"%2$s\">\n%3$s\t\t</trkpt>\n";

        // attributes
        final String ALTITUDE_FORMAT = "\t\t\t<ele>%1$s</ele>\n";
        final String TIME_FORMAT = "\t\t\t<time>%1$s</time>\n";
        final String NUMBER_OF_SAT_FORMAT = "\t\t\t<sat>%1$s</sat>\n";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String time = dateFormat.format(location.getTime());

        StringBuilder attributes = new StringBuilder();
        attributes.append(String.format(ALTITUDE_FORMAT, location.getAltitude()));
        attributes.append(String.format(TIME_FORMAT, time));

        Bundle extras = location.getExtras();
        if((extras != null) && (extras.getString("satellites") != null)){
            String numberOfSatellites = extras.getString("satellites");
            attributes.append(String.format(NUMBER_OF_SAT_FORMAT, numberOfSatellites));
        }

        // TODO: include "horizontal dilution of precision" as an attribute
        // TODO: include speed uncertainty as an attribute [NOT IN THE STANDARD!]

        String trackpoint = String.format(TRACKPOINT_FORMAT, location.getLatitude(), location.getLongitude(), attributes);
        gpxFile.write(trackpoint);

        String[] infos = {
                time,
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()),
                String.valueOf(location.getAltitude())
        };

        String row = String.join(", ", infos);
        csvFile.write(row + "\n");
    }

    void close() throws IOException {
        gpxFile.write("\t\t</trkseg>\n");
        gpxFile.write("\t</trk>\n");
        gpxFile.write("</gpx>");
        gpxFile.flush();
        gpxFile.close();

        csvFile.flush();
        csvFile.close();
    }
}
