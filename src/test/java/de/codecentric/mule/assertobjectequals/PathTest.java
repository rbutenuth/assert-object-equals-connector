package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import org.junit.Test;

public class PathTest {

    @Test
    public void emptyRoot() {
        Path p = new Path();
        assertEquals("", p.toString());
        assertTrue(p.isRoot());
        assertFalse(p.isList());
        assertFalse(p.isMap());
    }

    @Test
    public void rootWithListEntry() {
        Path p = new Path().listEntry(42, 44);
        assertFalse(p.isRoot());
        assertTrue(p.isList());
        assertFalse(p.isMap());
        assertEquals("[42]", p.toString());
    }

    @Test
    public void rootWithMapEntry() {
        Path p = new Path().mapEntry("bar");
        assertFalse(p.isRoot());
        assertFalse(p.isList());
        assertTrue(p.isMap());
        assertEquals("['bar']", p.toString());
    }

    @Test
    public void rootWithListAndMapEntry() {
        Path p = new Path().listEntry(42, 44).mapEntry("bar");
        assertFalse(p.isRoot());
        assertEquals("[42]['bar']", p.toString());
    }

    @Test(expected = NullPointerException.class)
    public void nullKey() {
        new Path().mapEntry(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeListSize() {
        new Path().listEntry(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listIndexLessThanZeroSize() {
        new Path().listEntry(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listIndexOutOfRange() {
        new Path().listEntry(1, 1);
    }

    @Test(expected = IllegalStateException.class)
    public void rootHasNoPredecessor() {
        new Path().getPredecessor();
    }
}
