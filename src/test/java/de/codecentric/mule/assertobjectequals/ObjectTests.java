package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ObjectTests extends AbstractConnectorTest {

    @Test
    public void allNull() throws Exception {
        aoec.compareObjects(null, "#[payload]", false, false, null, createEvent(null));
    }

    @Test
    public void streamListEmptyOptions() throws Exception {
        InputStream expected = string2Stream("[\"a\", \"b\", \"c\"]");
        List<String> actual = list("a", "b", "c");
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) aoec.compareObjects(expected, "#[payload]", false, false, list(), createEvent(actual));
        assertEquals(actual, result);
    }

    @Test
    public void streamObjectEmptyOptions() throws Exception {
        InputStream expected = string2Stream("{\"a\": 1, \"b\": 2}");
        Map<Object, Object> actual = new LinkedHashMap<>();
        actual.put("b", 2);
        actual.put("a", 1);
        aoec.compareObjects(expected, "#[payload]", false, false, list(), createEvent(actual));
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
        aoec.compareObjects(string2Stream(expected), "#[payload]", true, false, list(), createEvent(actual));
        aoec.compareObjects(string2Stream(expected), "#[payload]", false, false, list("CONTAINS_ONLY_ON_MAPS"), createEvent(actual));
    }

    @Test
    public void jsonStringListEmptyOptions() throws Exception {
        String expected = "[\"a\", \"b\", \"c\"]";
        Object actual = list("a", "b", "c");
        aoec.compareObjects(expected, "#[payload]", false, false, list(), createEvent(actual));
    }

    @Test
    public void jsonByteArrayObjectEmptyOptions() throws Exception {
        byte[] expected = "[\"a\", \"b\", \"c\"]".getBytes(StandardCharsets.UTF_8);
        Object actual = list("a", "b", "c");
        aoec.compareObjects(expected, "#[payload]", false, false, list(), createEvent(actual));
    }

    @Test
    public void nullEmptyOptions() throws Exception {
        aoec.compareObjects(null, "#[payload]", false, false, list(), createEvent(null));
    }

    @Test
    public void notEqualEmptyOptions() throws Exception {
        expectNotEqual("huhu", null, false, false, list(), "at '', expected huhu, actual is null");
    }

    @Test
    public void streamStream() throws Exception {
        String jsonString = "[\"a\", \"b\", \"c\"]";
        InputStream expected = string2Stream(jsonString);
        InputStream actual = string2Stream(jsonString);
        InputStream result = (InputStream) aoec.compareObjects(expected, "#[payload]", false, false, list(), createEvent(actual));
        // The result has to be a stream with the original payload
        String resultString = stream2String(result);
        assertEquals(jsonString, resultString);
    }

    private void expectNotEqual(Object expected, Object actual, boolean containsOnlyOnMaps, boolean checkMapOrder, List<String> pathOptions,
            String expectedMessage) throws Exception {
        try {
            aoec.compareObjects(expected, "#[payload]", containsOnlyOnMaps, checkMapOrder, pathOptions, createEvent(actual));
            fail("AssertionError missing!");
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    private List<String> list(String... entries) {
        return Arrays.asList(entries);
    }
}
