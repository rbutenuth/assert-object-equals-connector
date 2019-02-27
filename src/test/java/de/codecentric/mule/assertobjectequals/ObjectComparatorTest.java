package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObjectComparatorTest {
    private ObjectComparator ocEqualMapsOrdered;
    private ObjectComparator ocContainsMapsOrdered;
    private ObjectComparator ocEqualMapsUnordered;
    private ObjectComparator ocContainsMapsUnordered;
    private ObjectComparator ocIgnore;

    @Before
    public void setUp() {
        ocEqualMapsOrdered = new ObjectComparator(createFactory(EnumSet.of(PathOption.CHECK_MAP_ORDER)));
        ocContainsMapsOrdered = new ObjectComparator(
                createFactory(EnumSet.of(PathOption.CONTAINS_ONLY_ON_MAPS, PathOption.CHECK_MAP_ORDER)));
        ocEqualMapsUnordered = new ObjectComparator(createFactory(EnumSet.noneOf(PathOption.class)));
        ocContainsMapsUnordered = new ObjectComparator(createFactory(EnumSet.of(PathOption.CONTAINS_ONLY_ON_MAPS)));
        ocIgnore = new ObjectComparator(createFactory(EnumSet.of(PathOption.IGNORE)));
    }

    @After
    public void tearDown() {
        ocEqualMapsOrdered = null;
        ocContainsMapsOrdered = null;
        ocEqualMapsUnordered = null;
        ocContainsMapsUnordered = null;
        ocIgnore = null;
    }

    private ObjectCompareOptionsFactory createFactory(EnumSet<PathOption> options) {
        final EnumSet<PathOption> o = EnumSet.copyOf(options);
        return new ObjectCompareOptionsFactory() {
            @Override
            public EnumSet<PathOption> createOptions(EnumSet<PathOption> inherited, Path path) {
                return o;
            }
        };
    }

    @Test
    public void testNullNull() {
        assertEmpty(ocEqualMapsOrdered.compare(null, null));
    }

    @Test
    public void testStringStringEqual() {
        assertEmpty(ocEqualMapsOrdered.compare("foo", "foo"));
    }

    @Test
    public void testNullString() {
        Collection<String> diffs = ocEqualMapsOrdered.compare(null, "foo");
        assertCollectionEquals(diffs, "at '', expected is null, actual foo");
    }

    @Test
    public void testStringNull() {
        Collection<String> diffs = ocEqualMapsOrdered.compare("foo", null);
        assertCollectionEquals(diffs, "at '', expected foo, actual is null");
    }

    @Test
    public void testStringStringNotEqual() {
        Collection<String> diffs = ocEqualMapsOrdered.compare("foo", "bar");
        assertCollectionEquals(diffs, "at '', expected foo, but found bar");
    }

    @Test
    public void testStringStringNotEqualIgnore() {
        assertEmpty(ocIgnore.compare("foo", "bar"));
    }

    @Test
    public void testEmptyListListEqual() {
        List<String> list = Arrays.asList();
        assertEmpty(ocEqualMapsOrdered.compare(list, list));
    }

    @Test
    public void testListListEqual() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertEmpty(ocEqualMapsOrdered.compare(list, list));
    }

    @Test
    public void testListSizeNotEqual() {
        List<String> listA = Arrays.asList("a", "b", "c");
        List<String> listB = Arrays.asList("a", "b");
        Collection<String> diffs = ocEqualMapsOrdered.compare(listA, listB);
        assertCollectionEquals(diffs, "at '', expected size 3, actual 2");
    }

    @Test
    public void testListsNotEqual() {
        List<String> listA = Arrays.asList("a", "b", "c");
        List<String> listB = Arrays.asList("a", "c", "d");
        Collection<String> diffs = ocEqualMapsOrdered.compare(listA, listB);
        assertCollectionEquals(diffs, "at '[1]', expected b, but found c", "at '[2]', expected c, but found d");
    }

    @Test
    public void testListsNotEqualMap() {
        List<String> list = Arrays.asList("a", "b", "c");
        Map<String, String> map = new LinkedHashMap<>();
        Collection<String> diffs = ocEqualMapsOrdered.compare(list, map);
        assertCollectionEquals(diffs, "at '', expected List, but found java.util.LinkedHashMap");
    }

    @Test
    public void testMapNotEqualList() {
        List<String> list = Arrays.asList("a", "b", "c");
        Map<String, String> map = new LinkedHashMap<>();
        Collection<String> diffs = ocEqualMapsOrdered.compare(map, list);
        assertCollectionEquals(diffs, "at '', expected Map, but found java.util.Arrays$ArrayList");
    }

    @Test
    public void testEmptyMapsEqual() {
        Map<String, String> mapA = new LinkedHashMap<>();
        Map<String, String> mapB = new LinkedHashMap<>();
        assertEmpty(ocEqualMapsOrdered.compare(mapA, mapB));
        assertEmpty(ocEqualMapsUnordered.compare(mapA, mapB));
        assertEmpty(ocContainsMapsOrdered.compare(mapA, mapB));
        assertEmpty(ocContainsMapsUnordered.compare(mapA, mapB));
    }

    @Test
    public void testMapsEqual() {
        Map<String, String> mapA = new LinkedHashMap<>();
        mapA.put("a", "A");
        mapA.put("b", "B");
        mapA.put("c", "C");
        Map<String, String> mapB = new LinkedHashMap<>();
        mapB.put("a", "A");
        mapB.put("b", "B");
        mapB.put("c", "C");
        assertEmpty(ocEqualMapsOrdered.compare(mapA, mapB));
        assertEmpty(ocContainsMapsOrdered.compare(mapA, mapB));
    }

    @Test
    public void testMapsOrderChangedEqual() {
        Map<String, String> mapA = new LinkedHashMap<>();
        mapA.put("a", "A");
        mapA.put("b", "B");
        mapA.put("c", "C");
        Map<String, String> mapB = new LinkedHashMap<>();
        mapB.put("a", "A");
        mapB.put("c", "C");
        mapB.put("b", "B");
        assertEmpty(ocContainsMapsUnordered.compare(mapA, mapB));
        Collection<String> diffs = ocEqualMapsOrdered.compare(mapA, mapB);
        assertCollectionEquals(diffs, "at '', expect key b, actual c");
    }

    @Test
    public void testMapsMissingActual() {
        Map<String, String> mapA = new LinkedHashMap<>();
        mapA.put("a", "A");
        mapA.put("b", "B");
        mapA.put("c", "C");
        Map<String, String> mapB = new LinkedHashMap<>();
        mapB.put("a", "A");
        mapB.put("b", "B");
        Collection<String> diffs = ocEqualMapsUnordered.compare(mapA, mapB);
        assertCollectionEquals(diffs, "at '', objects missing in actual: c");
        diffs = ocContainsMapsUnordered.compare(mapA, mapB);
        assertCollectionEquals(diffs, "at '', objects missing in actual: c");
    }

    @Test
    public void testMapsMissingExpected() {
        Map<String, String> mapA = new LinkedHashMap<>();
        mapA.put("a", "A");
        mapA.put("b", "B");
        Map<String, String> mapB = new LinkedHashMap<>();
        mapB.put("a", "A");
        mapB.put("b", "B");
        mapB.put("c", "C");
        Collection<String> diffs = ocEqualMapsOrdered.compare(mapA, mapB);
        assertCollectionEquals(diffs, "at '', objects missing in expected: c");
        diffs = ocEqualMapsUnordered.compare(mapA, mapB);
        assertCollectionEquals(diffs, "at '', objects missing in expected: c");
    }

    @Test
    public void testMapsContains() {
        Map<String, String> mapA = new LinkedHashMap<>();
        mapA.put("a", "A");
        mapA.put("b", "B");
        Map<String, String> mapB = new LinkedHashMap<>();
        mapB.put("a", "A");
        mapB.put("b", "B");
        mapB.put("c", "C");
        assertEmpty(ocContainsMapsOrdered.compare(mapA, mapB));
        assertEmpty(ocContainsMapsUnordered.compare(mapA, mapB));
    }

    @Test
    public void testMapsMoreExpectedThanActual() {
        Map<String, String> mapA = new LinkedHashMap<>();
        mapA.put("a", "A");
        mapA.put("b", "B");
        mapA.put("c", "C");
        Map<String, String> mapB = new LinkedHashMap<>();
        mapB.put("a", "A");
        mapB.put("b", "B");
        Collection<String> diffs = ocContainsMapsUnordered.compare(mapA, mapB);
        assertCollectionEquals(diffs, "at '', objects missing in actual: c");
    }

    @Test
    public void testMapsValuesNotEqual() {
        Map<String, String> mapA = new LinkedHashMap<>();
        mapA.put("a", "A");
        mapA.put("b", "B");
        Map<String, String> mapB = new LinkedHashMap<>();
        mapB.put("a", "A");
        mapB.put("b", "CC");
        Collection<String> diffs = ocEqualMapsOrdered.compare(mapA, mapB);
        assertCollectionEquals(diffs, "at '['b']', expected B, but found CC");
        diffs = ocContainsMapsUnordered.compare(mapA, mapB);
        assertCollectionEquals(diffs, "at '['b']', expected B, but found CC");
    }

    private void assertEmpty(Collection<?> c) {
        assertTrue("collection not empty", c.isEmpty());
    }

    private void assertCollectionEquals(Collection<String> c, String... entries) {
        assertEquals("Collection length", entries.length, c.size());
        Iterator<String> iter = c.iterator();
        for (String expected : entries) {
            if (iter.hasNext()) {
                String actual = iter.next();
                assertEquals(expected, actual);
            }
        }
    }
}
