package de.codecentric.mule.assertobjectequals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;

import org.mule.api.annotations.Category;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.param.Default;
import org.xmlunit.builder.Input;
import org.xmlunit.input.NormalizedSource;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

@Connector(name = "assert-object-equals", friendlyName = "Assert Object Equals", description = "Compares two Java Map/List based structures)")
@Category(name = "org.mule.tooling.category.munit", description = "MUnit")
public class AssertObjectEqualsConnector {

    /**
     * Compare two objects. Drill down into {@link Map} and {@link List}, use
     * {@link Object#equals(Object)} for all other classes. <br/>
     * Automatic conversions on <code>expected</code> and <code>actual</code>
     * are provided:
     * <ul>
     * <li>{@link InputStream} is read/parsed as Json</li>
     * <li>{@link byte[]} is parsed as Json</li>
     * <li>{@link String} is parsed as Json when it starts with [ or { (after
     * <code>trim()</code></li>
     * </ul>
     * Remember: Encoding for Json is always UTF8
     *
     * @param expected
     *            The expected value.
     * @param actual
     *            The actual value.
     * @param pathOptions
     *            Options for path patterns to control the comparison.
     * @return <code>actual</code>
     */
    @Processor(friendlyName = "Compare Objects")
    public Object compareObjects(Object expected, //
            @Default("#[payload]") Object actual, //
            @Default("#[[]]") @FriendlyName("Path patterns+options") List<String> pathOptions) //

            throws JsonProcessingException, IOException {

        Object expectedObj = convert2Object(expected);
        Object actualObj = convert2Object(actual);
        ObjectComparator comparator = createComparator(pathOptions == null ? new ArrayList<String>() : pathOptions);
        Collection<String> diff = comparator.compare(expectedObj, actualObj);

        if (!diff.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String s : diff) {
                if (message.length() > 0) {
                    message.append(System.lineSeparator());
                }
                message.append(s);
            }
            throw new AssertionError(message);
        }

        return actual;
    }

    /**
     * @param expected
     * @param actual
     * @return
     */
    @Processor(friendlyName = "Compare XML")
    public Object compareXml(Object expected, //
            @Default("#[payload]") Object actual, //
            @Default("#[[]]") @FriendlyName("Path patterns+options") List<String> pathOptions) {

        // Available wrappers around source:
        // CommentLessSource
        // WhitespaceStrippedSource
        // WhitespaceNormalizedSource
        // NormalizedSource

        Source source = Input.fromFile("file:/..../test.xml").build();
        //Input.from(object)
        
        // DefaultComparisonFormatter
        
        final String control = "<a><b attr=\"abc\"></b></a>";
        final String test = "<a><b attr=\"xyz\"></b></a>";

        /* https://github.com/xmlunit/user-guide/wiki/DiffBuilder
         
        Diff myDiff = DiffBuilder.compare(Input.fromString(control))
                      .withTest(Input.fromString(test))
                      .build();
                      
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
        
        Diff myDiff = DiffBuilder.compare(control)
        .withTest(test)
        .checkForSimilar().checkForIdentical() // [1]
        .ignoreComments() // [2]
        .ignoreWhitespace() // [3]
        .normalizeWhitespace() // [4]
        .withComparisonController(ComparisonController) // [5]
        .withComparisonFormatter(comparisonFormatter) // [6]
        .withComparisonListeners(comparisonListeners) // [7]
        .withDifferenceEvaluator(differenceEvaluator) // [8]
        .withDifferenceListeners(comparisonListeners) // [9]
        .withNodeMatcher(nodeMatcher) // [10]
        .withAttributeFilter(attributeFilter) // [11]
        .withNodeFilter(nodeFilter) // [12]
        .withNamespaceContext(map) // [13]
        .withDocumentBuilerFactory(factory); // [14]
        
        CompareMatcher is better for JUnit, see https://github.com/xmlunit/user-guide/wiki/CompareMatcher
        assertThat(test, CompareMatcher.isIdenticalTo(control));
        
        Xpath contains (geht aber auch direkt mit MEL)
        
        final String xml = "<a><b attr=\"abc\"></b></a>";  
        assertThat(xml, hasXPath("//a/b/@attr", equalTo("abc")));
        assertThat(xml, hasXPath("count(//a/b/c)", equalTo("0")));
        */
        
        return actual;
    }

    private ObjectComparator createComparator(List<String> pathOptionsStrings) {
        PathPatternParser ppp = new PathPatternParser();
        Collection<PathPattern> patterns = new ArrayList<>();

        for (String pathOptionString : pathOptionsStrings) {
            patterns.add(ppp.parse(pathOptionString));
        }
        ObjectCompareOptionsFactory optionFactory = new PatternBasedOptionsFactory(patterns);
        return new ObjectComparator(optionFactory);
    }

    private Object convert2Object(Object value) throws JsonProcessingException, IOException {
        if (value == null) {
            return null;
        } else if (value instanceof InputStream) {
            return new ObjectMapper().reader(Object.class).readValue((InputStream) value);
        } else if (value instanceof byte[]) {
            return new ObjectMapper().reader(Object.class).readValue((byte[]) value);
        } else if (value instanceof CharSequence) {
            String trimmed = ((CharSequence) value).toString().trim();
            if (trimmed.startsWith("[") || trimmed.startsWith("{")) {
                return new ObjectMapper().reader(Object.class).readValue(trimmed);
            } else {
                return value;
            }
        } else {
            return value;
        }
    }
}
