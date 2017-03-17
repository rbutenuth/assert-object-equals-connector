package de.codecentric.mule.assertobjectequals;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Compare two objects, drill down into List an
 */
public class ObjectComparator {
	private ObjectCompareOptionsFactory optionFactory;

	public ObjectComparator(ObjectCompareOptionsFactory optionFactory) {
		this.optionFactory = optionFactory;
	}

	/**
	 * Compare two objects. Drill down into {@link Map} and {@link List}, use
	 * {@link Object#equals(Object)} for all other classes.
	 *
	 * @param path Information where in the object tree we are.
	 * @param expected The expected value.
	 * @param actual The actual value.
	 * @param diffs Collect a textual description of the differences here.
	 * @return Is there a difference?
	 */
	public boolean compare(Path path, Object expected, Object actual, Collection<String> diffs) {
		if (expected == null) {
			if (actual == null) {
				return false; // ok, null equals null
			} else { // actual != null
				diffs.add("at " + path + ", expected is null, actual " + actual);
				return true;
			}
		} else { // expected != null
			if (actual == null) {
				diffs.add("at " + path + ", expected " + expected + ", actual is null");
				return true;
			} else { // actual != null
				return compareNonNullObjects(path, expected, actual, diffs);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean compareNonNullObjects(Path path, Object expected, Object actual, Collection<String> diffs) {
		if (expected instanceof List) {
			if (actual instanceof List) {
				return compareLists(path, (List<Object>) expected, (List<Object>) actual, diffs);
			} else {
				diffs.add("at " + path + ", expected List, but found " + actual.getClass().getName());
				return true;
			}
		} else if (expected instanceof Map) {
			if (actual instanceof Map) {
				return compareMaps(path, (Map<Object, Object>) expected, (Map<Object, Object>) actual, diffs);
			}
		} else {
			if (!expected.equals(actual)) {
				diffs.add("at " + path + ", expected " + expected + ", but found " + actual);
				return true;
			}
		}
		return false;
	}

	private boolean compareLists(Path path, List<Object> expected, List<Object> actual, Collection<String> diffs) {
		if (expected.size() != actual.size()) {
			diffs.add("at " + path + ", expected size " + expected.size() + ", actual " + actual.size());
			return true;
		}
		int size = expected.size();
		Iterator<Object> eIter = expected.iterator();
		Iterator<Object> aIter = actual.iterator();
		boolean foundDiff = false;
		for (int i = 0; i < size; i++) {
			Path nextPath = path.listEntry(i, size);
			if (!eIter.hasNext()) {
				throw new RuntimeException("at " + path + ", unexpected end of iteration at index " + i);
			}
			if (!aIter.hasNext()) {
				throw new RuntimeException("at " + path + ", unexpected end of iteration at index " + i);
			}
			Object expectedObj = eIter.next();
			Object actualObj = aIter.next();
			foundDiff |= compare(nextPath, expectedObj, actualObj, diffs);
		}
		return foundDiff;
	}

	private boolean compareMaps(Path path, Map<Object, Object> expected, Map<Object, Object> actual, Collection<String> diffs) {
		if (compareMapKeysOnly(path, expected, actual, diffs)) {
			return true;
		}
		boolean foundDiff = false;
		for (Map.Entry<Object, Object> entry : expected.entrySet()) {
			Object expectedKey = entry.getKey();
			foundDiff |= compare(path.mapEntry(expectedKey.toString()), entry.getValue(), actual.get(expectedKey), diffs);
		}
		return foundDiff;
	}

	private boolean compareMapKeysOnly(Path path, Map<Object, Object> expected, Map<Object, Object> actual, Collection<String> diffs) {
		// In all cases, expected keys must be a sub set of actual keys
		Set<Object> keys = new LinkedHashSet<Object>(expected.keySet());
		keys.removeAll(actual.keySet());
		if (!keys.isEmpty()) {
			diffs.add("at " + path + ", objects missing in actual: " + collectionToString(keys));
			return true;
		}
		ObjectCompareOptions options = optionFactory.createOptions(path);
		// The other way is only relevant when we *don't* hav a contains only chedk
		if (!options.isContainsCheckOnly()) {
			keys = new LinkedHashSet<Object>(actual.keySet());
			keys.removeAll(expected.keySet());
			if (!keys.isEmpty()) {
				diffs.add("at " + path + ", objects missing in expected: " + collectionToString(keys));
				return true;
			}
		}
		if (options.isMapOrderRelevant()) {
			if (checkOrder(path, expected.keySet(), actual.keySet(), diffs)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkOrder(Path path, Set<Object> expectedKeys, Set<Object> actualKeysOrig, Collection<String> diffs) {
		Set<Object> actualKeys = new LinkedHashSet<Object>(actualKeysOrig);
		// Remove all keys which are *not* in expected
		Iterator<Object> actualIter = actualKeys.iterator();
		while (actualIter.hasNext()) {
			if (!expectedKeys.contains(actualIter.next())) {
				actualIter.remove();
			}
		}
		// Now the two sets should be equal (and in same order)
		if (expectedKeys.size() != actualKeys.size()) {
			throw new RuntimeException("at " + path + " unexpected size mismatch");
		}
		Iterator<Object> expectedIter = expectedKeys.iterator();
		actualIter = actualKeys.iterator();
		int size = expectedKeys.size();
		for (int i = 0; i < size; i++) {
			if (!expectedIter.hasNext()) {
				throw new RuntimeException("at " + path + ", unexpected of iteration at index " + i);
			}
			if (!actualIter.hasNext()) {
				throw new RuntimeException("at " + path + ", unexpected  of iteration at index " + i);
			}
			Object eKey = expectedIter.next();
			Object aKey = actualIter.next();
			if (!eKey.equals(aKey)) {
				diffs.add("at " + path + ", expect key " + eKey + ", actual " + aKey);
				return true;
			}
		}
		return false;
	}

	private String collectionToString(Set<Object> col) {
		StringBuilder sb = new StringBuilder();
		for (Object o : col) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(o.toString());
		}
		return sb.toString();
	}
}
