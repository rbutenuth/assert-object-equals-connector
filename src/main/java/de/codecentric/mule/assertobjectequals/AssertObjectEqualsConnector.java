package de.codecentric.mule.assertobjectequals;

import java.util.List;
import java.util.Map;

import org.mule.api.annotations.Category;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;

@Connector(name = "assert-object-equals", friendlyName = "Assert Object Equals", description = "Compares two structures (Map/List based)")
@Category(name = "org.mule.tooling.category.munit", description = "MUnit")
public class AssertObjectEqualsConnector {
	private ObjectComparator comparator = null;//new ObjectComparator();

	/**
	* String represantation of the path pattern. String must contain
	* zero to <code>n</code> parts. The parts must comply with the following syntax:
	* <ul>
	* <li><code>?</code>: Wildcard one, matches one element in a path</li>
	* <li><code>*</code>: Wildcard any, matches zero to <code>n</code> elements in a path</li>
	* <li><code>[#]</code>: List wildcard, matches a list entry with any index</li>
	* <li><code>[0]</code>: Matches a list entry with the given number. 0 or positive numbers: Count from beginning, negative number: Cound from end (-1 is last element)</li>
	* <li><code>['.*']</code>: Matches a map entry where the key must match the given regular expression. If you need a ' in the expression, just write ''. The example '.*' matches all keys.</li>
	* </ul>
	 */
	//	@Configurable
	//	@Placement(group = "Objects")
	//	@FriendlyName("Path pattern")
	private Map<String, List<PathOption>> pathObjections;

	//	@Configurable
	//	@Placement(group = "XML")
	//	@FriendlyName("Todo...")
	private String someXmlStuff;

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
	@Processor(friendlyName = "Compare objects")
	public Object equals(Object expected, @Default("#[payload]") Object actual) {
		//comparator.compare(new PathState(new Path(), true, true), expected, actual);
		// java.lang.AssertionError
		return actual;
	}

	public Map<String, List<PathOption>> getPathObjections() {
		return pathObjections;
	}

	public void setPathObjections(Map<String, List<PathOption>> aPathObjections) {
		pathObjections = aPathObjections;
	}

	public String getSomeXmlStuff() {
		return someXmlStuff;
	}

	public void setSomeXmlStuff(String aSomeXmlStuff) {
		someXmlStuff = aSomeXmlStuff;
	}
}