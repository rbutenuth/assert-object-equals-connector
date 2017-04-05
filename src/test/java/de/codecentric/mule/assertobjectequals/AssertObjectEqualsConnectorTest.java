package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssertObjectEqualsConnectorTest {
    private AssertObjectEqualsConnector aoec;

    @Before
    public void before() {
        aoec = new AssertObjectEqualsConnector();
    }

    @After
    public void after() {
        aoec = null;
    }

    @Test
    public void allNull() throws Exception {
        aoec.compareObjects(null, null, null);
    }

    @Test
    public void streamObjectEmptyOptions() throws Exception {
        InputStream expected = string2Stream("[\"a\", \"b\", \"c\"]");
        Object actual = Arrays.asList("a", "b", "c");
        aoec.compareObjects(expected, actual, new ArrayList<String>());
    }

    @Test
    public void jsonStringObjectEmptyOptions() throws Exception {
        String expected = "[\"a\", \"b\", \"c\"]";
        Object actual = Arrays.asList("a", "b", "c");
        aoec.compareObjects(expected, actual, new ArrayList<String>());
    }

    @Test
    public void jsonByteArrayObjectEmptyOptions() throws Exception {
        byte[] expected = "[\"a\", \"b\", \"c\"]".getBytes(StandardCharsets.UTF_8);
        Object actual = Arrays.asList("a", "b", "c");
        aoec.compareObjects(expected, actual, new ArrayList<String>());
    }

    @Test
    public void nullEmptyOptions() throws Exception {
        aoec.compareObjects(null, null, new ArrayList<String>());
    }

    @Test
    public void notEqualEmptyOptions() throws Exception {
        expectNotEqual("huhu", null, new ArrayList<String>(), "at '', expected huhu, actual is null");
    }

    @Test
    public void xmlEqual() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b></b></a>";
        aoec.compareXml(xml, xml, XmlCompareOption.NORMALIZE_WHITESPACE);
    }

    private InputStream string2Stream(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }

    private void expectNotEqual(Object expected, Object actual, List<String> pathOptions, String expectedMessage)
            throws Exception {
        try {
            aoec.compareObjects(expected, actual, pathOptions);
            fail("AssertionError missing!");
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
