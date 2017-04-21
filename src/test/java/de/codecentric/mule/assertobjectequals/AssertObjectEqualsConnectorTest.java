package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        aoec.compareObjects(null, null, false, false, null);
    }

    @Test
    public void streamListEmptyOptions() throws Exception {
        InputStream expected = string2Stream("[\"a\", \"b\", \"c\"]");
        Object actual = list("a", "b", "c");
        aoec.compareObjects(expected, actual, false, false, list());
    }

    @Test
    public void streamObjectEmptyOptions() throws Exception {
        InputStream expected = string2Stream("{\"a\": 1, \"b\": 2}");
        Map<Object, Object> actual = new LinkedHashMap<>();
        actual.put("b", 2);
        actual.put("a", 1);
        aoec.compareObjects(expected, actual, false, false, list());
    }

    @Test
    public void streamObjectCheckMapOrder() throws Exception {
        String expected = "{\"a\": 1, \"b\": 2}";
        Map<Object, Object> actual = new LinkedHashMap<>();
        actual.put("b", 2);
        actual.put("a", 1);
        expectNotEqual(string2Stream(expected), actual, false, true, list(), "at '', expect key a, actual b");
        expectNotEqual(expected, actual, false, false, list("CHECK_MAP_ORDER"), "at '', expect key a, actual b");
    }

    @Test
    public void streamObjectContainsOnly() throws Exception {
        String expected = "{\"a\": 1}";
        Map<Object, Object> actual = new LinkedHashMap<>();
        actual.put("b", 2);
        actual.put("a", 1);
        aoec.compareObjects(string2Stream(expected), actual, true, false, list());
        aoec.compareObjects(string2Stream(expected), actual, false, false, list("CONTAINS_ONLY_ON_MAPS"));
    }

    @Test
    public void jsonStringListEmptyOptions() throws Exception {
        String expected = "[\"a\", \"b\", \"c\"]";
        Object actual = list("a", "b", "c");
        aoec.compareObjects(expected, actual, false, false, list());
    }

    @Test
    public void jsonByteArrayObjectEmptyOptions() throws Exception {
        byte[] expected = "[\"a\", \"b\", \"c\"]".getBytes(StandardCharsets.UTF_8);
        Object actual = list("a", "b", "c");
        aoec.compareObjects(expected, actual, false, false, list());
    }

    @Test
    public void nullEmptyOptions() throws Exception {
        aoec.compareObjects(null, null, false, false, list());
    }

    @Test
    public void notEqualEmptyOptions() throws Exception {
        expectNotEqual("huhu", null, false, false, list(), "at '', expected huhu, actual is null");
    }

    @Test
    public void xmlEqual() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b></b></a>";
        aoec.compareXml(xml, xml, XmlCompareOption.NORMALIZE_WHITESPACE);
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
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a>abba<b></b></a>";
        String actual = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>abba </b></a>";
        expectNotEqualXml(expected, actual, XmlCompareOption.NORMALIZE_WHITESPACE,
                "Expected child nodelist length '2' but was '1' - comparing <a...> at /a[1] to <a...> at /a[1]");
    }

    @Test
    public void xmlEqualIgnoreComments() {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b></b></a>";
        String actual = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b><!-- Hello, world! --></b></a>";
        aoec.compareXml(expected, actual, XmlCompareOption.IGNORE_COMMENTS);
    }

    @Test
    public void xmlEqualIgnoreWhitespace() {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>abba</b></a>";
        String actual = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>abba </b></a>";
        aoec.compareXml(expected, actual, XmlCompareOption.IGNORE_WHITESPACE);
    }

    private InputStream string2Stream(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }

    private void expectNotEqual(Object expected, Object actual, boolean containsOnlyOnMaps, boolean checkMapOrder, List<String> pathOptions,
            String expectedMessage) throws Exception {
        try {
            aoec.compareObjects(expected, actual, containsOnlyOnMaps, checkMapOrder, pathOptions);
            fail("AssertionError missing!");
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    private void expectNotEqualXml(Object expected, Object actual, XmlCompareOption options, String expectedMessage) throws Exception {
        try {
            aoec.compareXml(expected, actual, options);
            fail("AssertionError missing!");
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    private List<String> list(String... entries) {
        return Arrays.asList(entries);
    }
}
