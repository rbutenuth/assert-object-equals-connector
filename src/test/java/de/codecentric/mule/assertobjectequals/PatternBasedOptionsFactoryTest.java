package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumSet;

import org.junit.Test;

public class PatternBasedOptionsFactoryTest {

    @Test
    public void emptyNoOptions() {
        PatternBasedOptionsFactory f = createFactory();
        EnumSet<PathOption> options = f.createOptions(EnumSet.noneOf(PathOption.class), new Path());
        assertTrue(options.isEmpty());
    }

    @Test
    public void emptyWithOptions() {
        PatternBasedOptionsFactory f = createFactory("check_map_order");
        EnumSet<PathOption> options = f.createOptions(EnumSet.noneOf(PathOption.class), new Path());
        assertEquals(1, options.size());
        assertTrue(options.contains(PathOption.CHECK_MAP_ORDER));
    }

    @Test
    public void emptyStartOverrideInSupPath() {
        PatternBasedOptionsFactory f = createFactory("['foo']* check_map_order");
        EnumSet<PathOption> emptyRootOptions = f.createOptions(EnumSet.noneOf(PathOption.class), new Path());
        assertEquals(0, emptyRootOptions.size());

        EnumSet<PathOption> noMatchNodeOptions = f.createOptions(EnumSet.noneOf(PathOption.class), new Path().mapEntry("bar"));
        assertEquals(0, noMatchNodeOptions.size());

        EnumSet<PathOption> matchNodeOptions = f.createOptions(EnumSet.noneOf(PathOption.class), new Path().mapEntry("foo"));
        assertEquals(1, matchNodeOptions.size());
        assertTrue(matchNodeOptions.contains(PathOption.CHECK_MAP_ORDER));

        EnumSet<PathOption> matchSubNodeOptions = f.createOptions(EnumSet.noneOf(PathOption.class),
                new Path().mapEntry("foo").listEntry(2, 42));
        assertEquals(1, matchSubNodeOptions.size());
        assertTrue(matchSubNodeOptions.contains(PathOption.CHECK_MAP_ORDER));
    }

    private PatternBasedOptionsFactory createFactory(String... patternStrings) {
        PathPatternParser parser = new PathPatternParser();
        ArrayList<PathPattern> patterns = new ArrayList<PathPattern>();
        for (String ps : patternStrings) {
            patterns.add(parser.parse(ps));
        }
        return new PatternBasedOptionsFactory(EnumSet.noneOf(PathOption.class), patterns);
    }
}
