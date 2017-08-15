package ch.renatobellotti.freerun;


import android.content.Context;
import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.when;


/**
 * Tests the class GPXGenerator.
 */
@RunWith(MockitoJUnitRunner.class)
public class GPXGeneratorTest{

    private static final String TEST_PATH = "/tmp/";
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
        try {
            when(context.openFileOutput(HEADER_ONLY_FILENAME, Context.MODE_PRIVATE)).thenReturn(new FileOutputStream(HEADER_ONLY_PATH));
            when(context.openFileOutput(FULL_CONTENT_FILENAME, Context.MODE_PRIVATE)).thenReturn(new FileOutputStream(FULL_CONTENT_PATH));
        } catch (FileNotFoundException e) {
            fail("FileNotFoundException when opening the output stream:\n" + e.getMessage());
        }
        when(loc.getLatitude()).thenReturn(47.365748);
        when(loc.getLongitude()).thenReturn(8.546048);
        when(loc.getAltitude()).thenReturn((double) 0);
        when(loc.getTime()).thenReturn((long) 0);
        when(loc.getSpeed()).thenReturn((float) 0);
        when(loc.getExtras()).thenReturn(null);
    }

    private GPXGenerator getGenerator(final String filename){
        try{
            return new GPXGenerator(context, filename);
        } catch (FileNotFoundException e) {
            fail("FileNotFoundException when constructing a GPXGenerator");
        }

        // should never happen
        return null;
    }

    @Test
    public void testHeader(){
        GPXGenerator generator = getGenerator(HEADER_ONLY_FILENAME);
        assertNotNull(generator);
        try {
            generator.close();
        } catch (IOException e) {
            fail("IOException when closing the GPXGenerator");
        }

        // validate the XML
        validateGPX(HEADER_ONLY_PATH);
    }

    @Test
    public void testFullContent(){
        GPXGenerator generator = getGenerator(FULL_CONTENT_FILENAME);
        assertNotNull(generator);
        generator.appendData(loc);
        try {
            generator.close();
        } catch (IOException e) {
            fail("IOException when closing the GPXGenerator");
        }

        // validate the XML
        validateGPX(HEADER_ONLY_PATH);
    }

    // An easier solution would be to call xmllint:
    // xmllint --noout --schema http://www.topografix.com/GPX/1/1/gpx.xsd /tmp/full_content.gpx
    // Interestingly, the following command does not validate (because a DTD file is missing...)
    // xmllint --noout --postvalid --schema http://www.topografix.com/GPX/1/1/gpx.xsd /tmp/full_content.gpx
    private void validateGPX(String pathToGPX){
        // The following code has been adapted from:
        // http://www.journaldev.com/895/how-to-validate-xml-against-xsd-in-java
        try {
            InputStream onlineSchema = new URL("http://www.topografix.com/gpx/1/1/gpx.xsd").openStream();

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(onlineSchema));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(pathToGPX)));
        } catch(IOException e){
            fail("IOException while validating the GPX file");
        } catch (SAXException e) {
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            fail("SAXException while validating the GPX file");
        }
    }
}