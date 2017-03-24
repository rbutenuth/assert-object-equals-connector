package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.assertTrue;

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

    private PatternBasedOptionsFactory createFactory(String... patternStrings) {
        PathPatternParser parser = new PathPatternParser();
        ArrayList<PathPattern> patterns = new ArrayList<PathPattern>();
        for (String ps : patternStrings) {
            patterns.add(parser.parse(ps));
        }
        return new PatternBasedOptionsFactory(patterns);
    }
}
