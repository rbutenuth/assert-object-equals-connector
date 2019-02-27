package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import java.util.EnumSet;
import java.util.regex.Pattern;

import org.junit.Test;

public class PathPatternTest {
    private EnumSet<PathOption> options = EnumSet.noneOf(PathOption.class);

    @Test(expected = IllegalStateException.class)
    public void listPatternEntryGetKeyPatternFails() {
        PatternEntry pe = PatternEntry.createList(Integer.valueOf(1));
        pe.getKeyPattern();
    }

    @Test(expected = IllegalStateException.class)
    public void keyPatternEntryGetListFails() {
        PatternEntry pe = PatternEntry.createMap(Pattern.compile("foo"));
        pe.getListIndex();
    }

    @Test
    public void emptyPathMatchesEmptyPattern() {
        PathPattern pp = new PathPattern(new PatternEntry[0], options);
        assertTrue(pp.matches(new Path()));
    }

    @Test
    public void emptyPathMatchesWildcardAny() {
        PathPattern pp = new PathPattern(new PatternEntry[] { PatternEntry.createWildcardAny() }, options);
        assertTrue(pp.matches(new Path()));
    }

    @Test
    public void emptyPathDoesNotMatcheWildcardOne() {
        PathPattern pp = new PathPattern(new PatternEntry[] { PatternEntry.createWildcardOne() }, options);
        assertFalse(pp.matches(new Path()));
    }

    @Test
    public void emptyPathDoesNotMatchListOrMapPattern() {
        PathPattern pList = new PathPattern(new PatternEntry[] { PatternEntry.createList(null) }, options);
        assertFalse(pList.matches(new Path()));
        PathPattern pMap = new PathPattern(new PatternEntry[] { PatternEntry.createMap(null) }, options);
        assertFalse(pMap.matches(new Path()));
    }

    @Test
    public void pathDoesNotMatchEmptyPattern() {
        PathPattern pp = new PathPattern(new PatternEntry[0], options);
        assertFalse(pp.matches(new Path().listEntry(1, 10)));
        assertFalse(pp.matches(new Path().mapEntry("foo")));
    }

    @Test
    public void matchListEntry() {
        PathPattern pList = new PathPattern(new PatternEntry[] { PatternEntry.createList(1) }, options);
        assertTrue(pList.matches(new Path().listEntry(1, 10)));
    }

    @Test
    public void matchListEntryFromEnd() {
        PathPattern pList = new PathPattern(new PatternEntry[] { PatternEntry.createList(-1) }, options);
        assertTrue(pList.matches(new Path().listEntry(9, 10)));
    }

    @Test
    public void matchListEntryAnyIndex() {
        PathPattern pList = new PathPattern(new PatternEntry[] { PatternEntry.createList(null) }, options);
        assertTrue(pList.matches(new Path().listEntry(9, 10)));
    }

    @Test
    public void matchListEntryWrongIndex() {
        PathPattern pList = new PathPattern(new PatternEntry[] { PatternEntry.createList(1) }, options);
        assertFalse(pList.matches(new Path().listEntry(3, 10)));
    }

    @Test
    public void matchMapEntryAny() {
        PathPattern pMap = new PathPattern(new PatternEntry[] { PatternEntry.createMap(null) }, options);
        assertTrue(pMap.matches(new Path().mapEntry("egal")));
    }

    @Test
    public void matchMapEntryPatternMatches() {
        PathPattern pMap = new PathPattern(new PatternEntry[] { PatternEntry.createMap(Pattern.compile("abc")) }, options);
        assertTrue(pMap.matches(new Path().mapEntry("abc")));
    }

    @Test
    public void matchMapEntryPatternMatchesNot() {
        PathPattern pMap = new PathPattern(new PatternEntry[] { PatternEntry.createMap(Pattern.compile("abc")) }, options);
        assertFalse(pMap.matches(new Path().mapEntry("abcd")));
    }
}
