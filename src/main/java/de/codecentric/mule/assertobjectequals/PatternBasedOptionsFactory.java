package de.codecentric.mule.assertobjectequals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

public class PatternBasedOptionsFactory implements ObjectCompareOptionsFactory {
    private EnumSet<PathOption> rootOptions;
    private Collection<PathPattern> patterns;

    public PatternBasedOptionsFactory(EnumSet<PathOption> rootOptions, Collection<PathPattern> patterns) {
        this.rootOptions = EnumSet.copyOf(rootOptions);
        this.patterns = new ArrayList<>(patterns);
    }

    @Override
    public EnumSet<PathOption> createOptions(EnumSet<PathOption> inherited, Path path) {
        for (PathPattern pp : patterns) {
            if (pp.matches(path)) {
                return EnumSet.copyOf(pp.getOptions());
            }
        }
        return EnumSet.copyOf(inherited == null ? rootOptions : inherited);
    }

    public EnumSet<PathOption> getRootOptions() {
        return EnumSet.copyOf(rootOptions);
    }
}
