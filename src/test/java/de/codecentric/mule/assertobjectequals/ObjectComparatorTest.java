package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	private Path p;
	private Collection<String> diffs;

	@Before
	public void setUp() {
		ocEqualMapsOrdered = new ObjectComparator(createFactory(false, true));
		ocContainsMapsOrdered = new ObjectComparator(createFactory(true, true));
		ocEqualMapsUnordered = new ObjectComparator(createFactory(false, false));
		ocContainsMapsUnordered = new ObjectComparator(createFactory(true, false));
		diffs = new ArrayList<>();
		p = new Path();
	}

	@After
	public void tearDown() {
		ocEqualMapsOrdered = null;
		ocContainsMapsOrdered = null;
		ocEqualMapsUnordered = null;
		ocContainsMapsUnordered = null;
		diffs = null;
		p = null;
	}

	private ObjectCompareOptionsFactory createFactory(final boolean containsCheckOnly, final boolean mapOrderRelevant) {
		return new ObjectCompareOptionsFactory() {
			@Override
			public ObjectCompareOptions createOptions(Path aPath) {
				return new ObjectCompareOptions(containsCheckOnly, mapOrderRelevant);
			}
		};
	}

	@Test
	public void testNullNull() {
		assertFalse(ocEqualMapsOrdered.compare(p, null, null, diffs));
		assertEmpty(diffs);
	}

	@Test
	public void testStringStringEqual() {
		assertFalse(ocEqualMapsOrdered.compare(p, "foo", "foo", diffs));
		assertEmpty(diffs);
	}

	@Test
	public void testNullString() {
		assertTrue(ocEqualMapsOrdered.compare(p, null, "foo", diffs));
		assertCollectionEquals(diffs, "at , expected is null, actual foo");
	}

	@Test
	public void testStringNull() {
		assertTrue(ocEqualMapsOrdered.compare(p, "foo", null, diffs));
		assertCollectionEquals(diffs, "at , expected foo, actual is null");
	}

	@Test
	public void testStringStringNotEqual() {
		assertTrue(ocEqualMapsOrdered.compare(p, "foo", "bar", diffs));
		assertCollectionEquals(diffs, "at , expected foo, but found bar");
	}

	@Test
	public void testEmptyListListEqual() {
		List<String> list = Arrays.asList();
		assertFalse(ocEqualMapsOrdered.compare(p, list, list, diffs));
		assertEmpty(diffs);
	}

	@Test
	public void testListListEqual() {
		List<String> list = Arrays.asList("a", "b", "c");
		assertFalse(ocEqualMapsOrdered.compare(p, list, list, diffs));
		assertEmpty(diffs);
	}

	@Test
	public void testListSizeNotEqual() {
		List<String> listA = Arrays.asList("a", "b", "c");
		List<String> listB = Arrays.asList("a", "b");
		assertTrue(ocEqualMapsOrdered.compare(p, listA, listB, diffs));
		assertCollectionEquals(diffs, "at , expected size 3, actual 2");
	}

	@Test
	public void testListsNotEqual() {
		List<String> listA = Arrays.asList("a", "b", "c");
		List<String> listB = Arrays.asList("a", "c", "d");
		assertTrue(ocEqualMapsOrdered.compare(p, listA, listB, diffs));
		assertCollectionEquals(diffs, "at [1], expected b, but found c", "at [2], expected c, but found d");
	}

	@Test
	public void testEmptyMapsEqual() {
		Map<String, String> mapA = new LinkedHashMap<>();
		Map<String, String> mapB = new LinkedHashMap<>();
		assertFalse(ocEqualMapsOrdered.compare(p, mapA, mapB, diffs));
		assertFalse(ocEqualMapsUnordered.compare(p, mapA, mapB, diffs));
		assertFalse(ocContainsMapsOrdered.compare(p, mapA, mapB, diffs));
		assertFalse(ocContainsMapsUnordered.compare(p, mapA, mapB, diffs));
		assertEmpty(diffs);
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
		assertFalse(ocEqualMapsOrdered.compare(p, mapA, mapB, diffs));
		assertFalse(ocContainsMapsOrdered.compare(p, mapA, mapB, diffs));
		assertEmpty(diffs);
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
		assertFalse(ocContainsMapsUnordered.compare(p, mapA, mapB, diffs));
		assertTrue(ocEqualMapsOrdered.compare(p, mapA, mapB, diffs));
		assertCollectionEquals(diffs, "at , expect key b, actual c");
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
		assertTrue(ocEqualMapsUnordered.compare(p, mapA, mapB, diffs));
		assertCollectionEquals(diffs, "at , objects missing in actual: c");
		diffs.clear();
		assertTrue(ocContainsMapsUnordered.compare(p, mapA, mapB, diffs));
		assertCollectionEquals(diffs, "at , objects missing in actual: c");
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
		assertTrue(ocEqualMapsOrdered.compare(p, mapA, mapB, diffs));
		assertCollectionEquals(diffs, "at , objects missing in expected: c");
		diffs.clear();
		assertTrue(ocEqualMapsUnordered.compare(p, mapA, mapB, diffs));
		assertCollectionEquals(diffs, "at , objects missing in expected: c");
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
		assertFalse(ocContainsMapsOrdered.compare(p, mapA, mapB, diffs));
		assertFalse(ocContainsMapsUnordered.compare(p, mapA, mapB, diffs));
		assertEmpty(diffs);
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
		assertTrue(ocContainsMapsUnordered.compare(p, mapA, mapB, diffs));
		assertCollectionEquals(diffs, "at , objects missing in actual: c");
	}

	@Test
	public void testMapsValuesNotEqual() {
		Map<String, String> mapA = new LinkedHashMap<>();
		mapA.put("a", "A");
		mapA.put("b", "B");
		Map<String, String> mapB = new LinkedHashMap<>();
		mapB.put("a", "A");
		mapB.put("b", "CC");
		assertTrue(ocEqualMapsOrdered.compare(p, mapA, mapB, diffs));
		assertCollectionEquals(diffs, "at ['b'], expected B, but found CC");
		diffs.clear();
		assertTrue(ocContainsMapsUnordered.compare(p, mapA, mapB, diffs));
		assertCollectionEquals(diffs, "at ['b'], expected B, but found CC");
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
