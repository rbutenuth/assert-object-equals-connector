package de.codecentric.mule.assertobjectequals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.api.annotations.Category;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.Default;
import org.mule.extension.annotations.param.Optional;

@Connector(name = "assert-object-equals", friendlyName = "Assert Object Equals", description = "Compares two structures (Map/List based)")
@Category(name = "org.mule.tooling.category.munit", description = "MUnit")
public class AssertObjectEqualsConnector {
    private ObjectComparator comparator = null;// new ObjectComparator();

    // @Configurable
    // @Placement(group = "XML")
    // @FriendlyName("Todo...")
    // private String someXmlStuff;

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
            @Default("#[{'.*':'']") @FriendlyName("Path pattern") Map<String, String> pathOtions,
            @FriendlyName("Some Strings") List<String> strings,
            @FriendlyName("yes/no") boolean yesNo) {
        // comparator.compare(new PathState(new Path(), true, true), expected,
        // actual);
        // java.lang.AssertionError
        return actual;
    }
}
