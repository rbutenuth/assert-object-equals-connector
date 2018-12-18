package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.mule.api.transformer.DataType;
import org.mule.api.transport.OutputHandler;
import org.mule.devkit.api.transformer.TransformingValue;

public class XmlTests extends AbstractConnectorTest {

    @Test
    public void stringOutputHandler() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b></b></a>";

        OutputHandler actual = new ByteArrayBasedOutputHandler(xml.getBytes(StandardCharsets.UTF_8));
        TransformingValue<Object, DataType<Object>> r = aoec.compareXml(xml, "#[payload]", XmlCompareOption.NORMALIZE_WHITESPACE,
                createEvent(actual));
        OutputHandler result = (OutputHandler) r.getValue();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.write(null, bos);
        bos.close();
        String resultAsString = new String(bos.toByteArray(), StandardCharsets.UTF_8);
        assertEquals(xml, resultAsString);
    }

    @Test
    public void xmlEqual() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b></b></a>";
        InputStream actual = string2Stream(xml);
        TransformingValue<Object, DataType<Object>> r = aoec.compareXml(xml, "#[payload]", XmlCompareOption.NORMALIZE_WHITESPACE,
                createEvent(actual));
        InputStream resultStream = (InputStream) r.getValue();
        assertEquals(xml, stream2String(resultStream));
    }

    @Test
    public void xmlNotEqualDueToComment() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b></b></a>";
        String actual = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b><!-- Hello, world! --></b></a>";
        expectNotEqualXml(expected, actual, XmlCompareOption.NORMALIZE_WHITESPACE,
                "Expected child nodelist length '0' but was '1' - comparing <b...> at /a[1]/b[1] to <b...> at /a[1]/b[1]");
    }

    @Test
    public void xmlNotEqualDueToWhitespace() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>abba</b></a>";
        String actual = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>ab ba</b></a>";
        expectNotEqualXml(expected, actual, XmlCompareOption.NORMALIZE_WHITESPACE,
                "Expected text value 'abba' but was 'ab ba' - comparing <b ...>abba</b> at /a[1]/b[1]/text()[1] to <b ...>ab ba</b> at /a[1]/b[1]/text()[1]");
    }

    @Test
    public void xmlEqualDueToIgnoreAllWhitespace() throws Exception {
        // "normalize": Tab and space are equal
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>ab ba</b></a>";
        String actual = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>ab\tba</b></a>";
        aoec.compareXml(expected, "#[payload]", XmlCompareOption.NORMALIZE_WHITESPACE, createEvent(actual));
    }

    @Test
    public void xmlEqualIgnoreComments() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b></b></a>";
        String actual = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b><!-- Hello, world! --></b></a>";
        aoec.compareXml(expected, "#[payload]", XmlCompareOption.IGNORE_COMMENTS, createEvent(actual));
    }

    @Test
    public void xmlEqualIgnoreWhitespace() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>abba</b></a>";
        String actual = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>abba </b></a>";
        aoec.compareXml(expected, "#[payload]", XmlCompareOption.IGNORE_WHITESPACE, createEvent(actual));
    }

    private void expectNotEqualXml(Object expected, Object actual, XmlCompareOption options, String expectedMessage) throws Exception {
        try {
            aoec.compareXml(expected, "#[payload]", options, createEvent(actual));
            fail("AssertionError missing!");
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
