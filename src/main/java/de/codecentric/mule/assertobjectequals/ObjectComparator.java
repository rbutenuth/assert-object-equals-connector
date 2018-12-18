package de.codecentric.mule.assertobjectequals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
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

    private class State {
        final Path path;
        final Object expected;
        final Object actual;
        final EnumSet<PathOption> options;

        private State(Path path, Object expected, Object actual, EnumSet<PathOption> options) {
            this.path = path;
            this.expected = expected;
            this.actual = actual;
            this.options = options;
        }

        public State(Object expected, Object actual) {
            path = new Path();
            this.expected = expected;
            this.actual = actual;
            options = optionFactory.createOptions(null, path);
        }

        public State listEntry(int listIndex, int listSize, Object expected, Object actual) {
            Path next = path.listEntry(listIndex, listSize);
            return new State(next, expected, actual, optionFactory.createOptions(options, next));
        }

        public State mapEntry(String key, Object expected, Object actual) {
            Path next = path.mapEntry(key);
            return new State(next, expected, actual, optionFactory.createOptions(options, next));
        }
    }

    public ObjectComparator(ObjectCompareOptionsFactory optionFactory) {
        this.optionFactory = optionFactory;
    }

    /**
     * Compare two objects. Drill down into {@link Map} and {@link List}, use {@link Object#equals(Object)} for all other
     * classes.
     *
     * @param expected
     *            The expected value.
     * @param actual
     *            The actual value.
     * @return Textual description of the differences.
     */
    public Collection<String> compare(Object expected, Object actual) {
        State state = new State(expected, actual);
        Collection<String> diffs = new ArrayList<String>();
        compare(state, diffs);
        return diffs;
    }

    private void compare(State state, Collection<String> diffs) {
        if (state.options.contains(PathOption.IGNORE)) {
            return;
        }
        if (state.expected == null) {
            if (state.actual == null) {
                // ok, null equals null
            } else { // actual != null
                diffs.add("at '" + state.path + "', expected is null, actual " + state.actual);
            }
        } else { // expected != null
            if (state.actual == null) {
                diffs.add("at '" + state.path + "', expected " + state.expected + ", actual is null");
            } else { // actual != null
                compareNonNullObjects(state, diffs);
            }
        }
    }

    private void compareNonNullObjects(State state, Collection<String> diffs) {
        if (state.expected instanceof List) {
            if (state.actual instanceof List) {
                compareLists(state, diffs);
            } else {
                diffs.add("at '" + state.path + "', expected List, but found " + state.actual.getClass().getName());
            }
        } else if (state.expected instanceof Map) {
            if (state.actual instanceof Map) {
                compareMaps(state, diffs);
            }
        } else {
            if (!state.expected.equals(state.actual)) {
                diffs.add("at '" + state.path + "', expected " + state.expected + ", but found " + state.actual);
            }
        }
    }

    private void compareLists(State state, Collection<String> diffs) {
        @SuppressWarnings("unchecked")
        List<Object> expected = (List<Object>) state.expected;
        @SuppressWarnings("unchecked")
        List<Object> actual = (List<Object>) state.actual;

        if (expected.size() != actual.size()) {
            diffs.add("at '" + state.path + "', expected size " + expected.size() + ", actual " + actual.size());
            return;
        }
        int size = expected.size();
        Iterator<Object> eIter = expected.iterator();
        Iterator<Object> aIter = actual.iterator();
        for (int i = 0; i < size && eIter.hasNext() && aIter.hasNext(); i++) {
            State nextState = state.listEntry(i, size, eIter.next(), aIter.next());
            compare(nextState, diffs);
        }
    }

    private void compareMaps(State state, Collection<String> diffs) {
        if (compareMapKeysOnly(state, diffs)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<Object, Object> expected = (Map<Object, Object>) state.expected;
        @SuppressWarnings("unchecked")
        Map<Object, Object> actual = (Map<Object, Object>) state.actual;

        for (Map.Entry<Object, Object> entry : expected.entrySet()) {
            Object expectedKey = entry.getKey();
            compare(state.mapEntry(expectedKey.toString(), entry.getValue(), actual.get(expectedKey)), diffs);
        }
    }

    private boolean compareMapKeysOnly(State state, Collection<String> diffs) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> expected = (Map<Object, Object>) state.expected;
        @SuppressWarnings("unchecked")
        Map<Object, Object> actual = (Map<Object, Object>) state.actual;

        // In all cases, expected keys must be a sub set of actual keys
        Set<Object> keys = new LinkedHashSet<Object>(expected.keySet());
        keys.removeAll(actual.keySet());
        if (!keys.isEmpty()) {
            diffs.add("at '" + state.path + "', objects missing in actual: " + collectionToString(keys));
            return true;
        }
        // The other way is only relevant when we *don't* have a contains only
        if (!state.options.contains(PathOption.CONTAINS_ONLY_ON_MAPS)) {
            keys = new LinkedHashSet<Object>(actual.keySet());
            keys.removeAll(expected.keySet());
            if (!keys.isEmpty()) {
                diffs.add("at '" + state.path + "', objects missing in expected: " + collectionToString(keys));
                return true;
            }
        }
        if (state.options.contains(PathOption.CHECK_MAP_ORDER)) {
            return checkOrder(state.path, expected.keySet(), actual.keySet(), diffs);
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
        for (int i = 0; i < size && expectedIter.hasNext() && actualIter.hasNext(); i++) {
            Object eKey = expectedIter.next();
            Object aKey = actualIter.next();
            if (!eKey.equals(aKey)) {
                diffs.add("at '" + path + "', expect key " + eKey + ", actual " + aKey);
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
