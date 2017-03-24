package de.codecentric.mule.assertobjectequals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

public class PatternBasedOptionsFactory implements ObjectCompareOptionsFactory {
    private Collection<PathPattern> patterns;
    
    public PatternBasedOptionsFactory(Collection<PathPattern> patterns) {
        this.patterns = new ArrayList<>(patterns);
    }
    
    @Override
    public EnumSet<PathOption> createOptions(EnumSet<PathOption> inherited, Path path) {
        EnumSet<PathOption> options = EnumSet.copyOf(inherited);
        
        return options;
    }


}
