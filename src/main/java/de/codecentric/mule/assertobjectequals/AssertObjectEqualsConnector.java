package de.codecentric.mule.assertobjectequals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.api.annotations.Category;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.param.Default;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

@Connector(name = "assert-object-equals", friendlyName = "Assert Object Equals", description = "Compares two Java Map/List based structures)")
@Category(name = "org.mule.tooling.category.munit", description = "MUnit")
public class AssertObjectEqualsConnector {

    /**
     * Compare two objects. Drill down into {@link Map} and {@link List}, use
     * {@link Object#equals(Object)} for all other classes.
     *
     * @param expected
     *            The expected value.
     * @param actual
     *            The actual value.
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
            return new ObjectMapper().reader(Object.class).readValue((InputStream)value);
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
