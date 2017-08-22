package ch.renatobellotti.freerun;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.when;


/**
 * Tests the class GPXGenerator.
 */
@RunWith(MockitoJUnitRunner.class)
public class GPXGeneratorTest{

    private static final String TEST_PATH = getTestDirPath();
    private static final String HEADER_ONLY_FILENAME = "header_only.gpx";
    private static final String HEADER_ONLY_PATH = TEST_PATH + HEADER_ONLY_FILENAME;
    private static final String FULL_CONTENT_FILENAME = "full_content.gpx";
    private static final String FULL_CONTENT_PATH = TEST_PATH + FULL_CONTENT_FILENAME;

    @Mock
    Context context;
    @Mock
    Location loc;

    @Before
    public void setup(){
        when(loc.getLatitude()).thenReturn(47.365748);
        when(loc.getLongitude()).thenReturn(8.546048);
        when(loc.getAltitude()).thenReturn((double) 0);
        when(loc.getTime()).thenReturn((long) 0);
        when(loc.getExtras()).thenReturn(null);
    }

    // the current minimum API level does not support System.lineSeparator()!
    // all the "\n" characters should be replaced by the API method if the minimum API level is
    // increased one day

    @Test
    public void testHeader(){
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(HEADER_ONLY_PATH);
        } catch (FileNotFoundException e) {
            fail("FileNotFoundException when opening the FileOutputStream for header only test case:\n" + e.getMessage());
        }
        GPXGenerator generator = new GPXGenerator(file);
        assertNotNull(generator);
        try {
            generator.close();
        } catch (IOException e) {
            fail("IOException when closing the GPXGenerator:\n" + e.getMessage());
        }

        // validate the XML
        validateGPX(HEADER_ONLY_PATH);
    }

    @Test
    public void testFullContent(){
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(FULL_CONTENT_PATH);
        } catch (FileNotFoundException e) {
            fail("FileNotFoundException when opening the FileOutputStream for header only test case:\n" + e.getMessage());
        }
        GPXGenerator generator = new GPXGenerator(file);
        assertNotNull(generator);
        generator.appendData(loc);
        try {
            generator.close();
        } catch (IOException e) {
            fail("IOException when closing the GPXGenerator:\n" + e.getMessage());
        }

        // validate the XML
        validateGPX(FULL_CONTENT_PATH);
    }

    /**
     * Validates the GPX file at pathToGPX.
     *
     * Note that the command line program xmllint has to be installed and in the path
     * in order for this method to work.
     * @param pathToGPX Path to the GPX file to validate
     */
    private void validateGPX(String pathToGPX){
        final String CMD_MASK = "xmllint --noout --schema http://www.topografix.com/GPX/1/1/gpx.xsd %1$s";
        // Interestingly, the following command does not validate (because a DTD file is missing...)
        // xmllint --noout --postvalid --schema http://www.topografix.com/GPX/1/1/gpx.xsd /tmp/full_content.gpx
        final String CMD = String.format(CMD_MASK, pathToGPX);
        try {
            Process process = Runtime.getRuntime().exec(CMD);
            process.waitFor();
            if(process.exitValue() != 0){
                // some error while validating the GPX file
                // get the error output of xmllint
                String xmllintOutput = "";
                BufferedReader outputStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while((line = outputStream.readLine()) != null){
                    xmllintOutput += line + "\n";
                }

                // print error message
                String errorMessage = "The GPX file %1$s was not successfully validated.\n";
                errorMessage += "Output of xmllint:\n";
                errorMessage += xmllintOutput;
                fail(String.format(errorMessage, pathToGPX));
            }
        } catch (IOException e) {
            String errorMsg = "IOException while trying to run xmllint:\n" + e.getMessage();
            System.out.println(errorMsg);
            fail(errorMsg);
        } catch (InterruptedException e) {
            String errorMsg = "InterruptedException while waiting for xmllint to finish:\n" + e.getMessage();
            System.out.println(errorMsg);
            fail(errorMsg);
        }
    }

    @NonNull
    private static String getTestDirPath(){
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        System.out.println("Saving to directory: " + tmpDir.getAbsolutePath() + "/");
        return tmpDir.getAbsolutePath() + "/";
    }
}