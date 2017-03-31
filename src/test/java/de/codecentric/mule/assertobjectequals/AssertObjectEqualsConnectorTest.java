package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
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
    public void allNull() throws JsonProcessingException, IOException {
        aoec.compareObjects(null, null, null);
    }
    
    @Test
    public void streamObjectEmptyOptions() throws JsonProcessingException, IOException {
        InputStream expected = string2Stream("[\"a\", \"b\", \"c\"]");
        Object actual = Arrays.asList("a", "b", "c");
        aoec.compareObjects(expected, actual, new ArrayList<String>());
    }
    
    @Test
    public void jsonStringObjectEmptyOptions() throws JsonProcessingException, IOException {
        String expected = "[\"a\", \"b\", \"c\"]";
        Object actual = Arrays.asList("a", "b", "c");
        aoec.compareObjects(expected, actual, new ArrayList<String>());
    }
    
    @Test
    public void nullEmptyOptions() throws JsonProcessingException, IOException {
        aoec.compareObjects(null, null, new ArrayList<String>());
    }
    
    
    @Test
    public void notEqualEmptyOptions() throws Exception {
        expectNotEqual("huhu", null, new ArrayList<String>(), "at '', expected huhu, actual is null");
    }
    
    private InputStream string2Stream(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }
    
    private void expectNotEqual(Object expected, Object actual, List<String> pathOptions, String expectedMessage) throws Exception {
        try {
            aoec.compareObjects(expected, actual, pathOptions);
            fail("AssertionError missing!");
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
