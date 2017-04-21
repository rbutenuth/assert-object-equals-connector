package de.codecentric.mule.assertobjectequals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.annotations.Category;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.param.Default;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultComparisonFormatter;
import org.xmlunit.diff.Diff;

@Connector(name = "assert-object-equals", friendlyName = "Assert Object Equals", description = "Compares two Java Map/List based structures)")
@Category(name = "org.mule.tooling.category.munit", description = "MUnit")
public class AssertObjectEqualsConnector {

    /**
     * Compare two objects. Drill down into {@link Map} and {@link List}, use {@link Object#equals(Object)} for all
     * other classes.
     *
     * @param expected
     *            The expected value. Automatic conversions are provided:
     *            <ul>
     *            <li>InputStream is read/parsed as Json</li>
     *            <li>byte[] is parsed as Json</li>
     *            <li>String is parsed as Json when it starts with [ or { (after <code>trim()</code></li>
     *            </ul>
     *            Remember: Encoding for Json is always UTF8
     * @param actual
     *            The actual value. Automatic conversions are provided:
     *            <ul>
     *            <li>InputStream is read/parsed as Json</li>
     *            <li>byte[] is parsed as Json</li>
     *            <li>String is parsed as Json when it starts with [ or { (after <code>trim()</code></li>
     *            </ul>
     *            Remember: Encoding for Json is always UTF8
     * @param containsOnlyOnMaps
     *            The actual value entry set of maps can contain more values than the expected set. So you tests do not
     *            fail when there are more elements than expected in the result.
     *
     * @param checkMapOrder
     *            The order of map entries is checked. The default is to ignore order of map entries.
     *
     * @param pathOptions
     *            Options for path patterns to control the comparison. Syntax of one List entry: Zero to <code>n</code>
     *            path parts. The parts can have the following syntax:
     *            <ul>
     *            <li><code>?</code>: Wildcard one, matches one element in a path</li>
     *            <li><code>*</code>: Wildcard any, matches zero to <code>n</code> elements in a path</li>
     *            <li><code>[#]</code>: List wildcard, matches a list entry with any index</li>
     *            <li><code>[0]</code>: Matches a list entry with the given number. 0 or positive numbers: Count from
     *            beginning, negative number: Cound from end (-1 is last element)</li>
     *            <li><code>['.*']</code>: Matches a map entry where the key must match the given regular expression. If
     *            you need a ' in the expression, just write ''. The example '.*' matches all keys.</li>
     *            </ul>
     *            A space as separator. One or more of the following options (case not relevant):
     *
     *            CONTAINS_ONLY_ON_MAPS: The actual value entry set of maps can contain more values than the expected
     *            set. So you tests do not fail when there are more elements than expected in the result.
     *
     *            CHECK_MAP_ORDER: The order of map entries is checked. The default is to ignore order of map entries.
     *
     *            IGNORE: The actual node and its subtree is ignored completely.
     *
     * @return <code>actual</code>, but converted to <code>Object</code> when it had to be parsed.
     * @throws Exception
     *             When comparison fails or on technical problems (e.g. parsing)
     */
    @Processor(friendlyName = "Compare Objects")
    public Object compareObjects(@FriendlyName("Expected value") Object expected, //
            @Default("#[payload]") @FriendlyName("Actual value") Object actual, //
            @Default("false") @FriendlyName("Contains only on maps") boolean containsOnlyOnMaps, //
            @Default("false") @FriendlyName("Check map order") boolean checkMapOrder, //
            @Default("#[[]]") @FriendlyName("Path patterns+options") List<String> pathOptions) //

            throws Exception {

        Object expectedObj = convert2Object(expected);
        Object actualObj = convert2Object(actual);
        ObjectComparator comparator = createComparator(containsOnlyOnMaps, checkMapOrder,
                pathOptions == null ? new ArrayList<String>() : pathOptions);
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

        return actualObj;
    }

    /**
     * Compare two XML documents. See <a href="https://github.com/xmlunit/user-guide/wiki/">XMLUnit Wiki</a>} how this
     * works
     *
     * @param expected
     *            The expected value, XML as String, InputStream, byte[] or DOM tree.
     * @param actual
     *            The actual value, XML as String, InputStream, byte[] or DOM tree.
     * @param xmlCompareOption
     *            How to compare the XML documents.
     *
     *            IGNORE_COMMENTS: Will remove all comment-Tags "<!-- Comment -->" from test- and control-XML before
     *            comparing.
     *
     *            IGNORE_WHITESPACE: Ignore whitespace by removing all empty text nodes and trimming the non-empty ones.
     *
     *            NORMALIZE_WHITESPACE: Normalize Text-Elements by removing all empty text nodes and normalizing the
     *            non-empty ones.
     *
     * @return <code>actual</code>
     */
    @Processor(friendlyName = "Compare XML")
    public Object compareXml(Object expected, //
            @Default("#[payload]") Object actual, //
            @Default("NORMALIZE_WHITESPACE") @FriendlyName("XML compare option") XmlCompareOption xmlCompareOption) {

        DiffBuilder diffBuilder = DiffBuilder.compare(expected).withTest(actual);

        switch (xmlCompareOption) {
        case IGNORE_COMMENTS:
            diffBuilder = diffBuilder.ignoreComments();
            break;
        case IGNORE_WHITESPACE:
            diffBuilder = diffBuilder.ignoreWhitespace();
            break;
        case NORMALIZE_WHITESPACE:
            diffBuilder = diffBuilder.normalizeWhitespace();
            break;
        default:
            throw new IllegalArgumentException("I forgot to implement for a new enum constant.");
        }

        Diff diff = diffBuilder.build();

        if (diff.hasDifferences()) {
            throw new AssertionError(diff.toString(new DefaultComparisonFormatter()));
        }

        return actual;
    }

    private ObjectComparator createComparator(boolean containsOnlyOnMaps, boolean checkMapOrder, List<String> pathOptionsStrings) {
        PathPatternParser ppp = new PathPatternParser();
        Collection<PathPattern> patterns = new ArrayList<>();

        for (String pathOptionString : pathOptionsStrings) {
            patterns.add(ppp.parse(pathOptionString));
        }
        EnumSet<PathOption> rootOptions = EnumSet.noneOf(PathOption.class);
        if (containsOnlyOnMaps) {
            rootOptions.add(PathOption.CONTAINS_ONLY_ON_MAPS);
        }
        if (checkMapOrder) {
            rootOptions.add(PathOption.CHECK_MAP_ORDER);
        }
        ObjectCompareOptionsFactory optionFactory = new PatternBasedOptionsFactory(rootOptions, patterns);
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
