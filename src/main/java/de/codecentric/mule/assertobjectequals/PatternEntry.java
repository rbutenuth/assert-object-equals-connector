package de.codecentric.mule.assertobjectequals;

import java.util.regex.Pattern;

public class PatternEntry {
    enum PatternEntryType {
        MAP, LIST, WILDCARD_ONE, WILDCARD_ANY
    }

    private final PatternEntry.PatternEntryType type;
    private Pattern keyPattern;
    private Integer listIndex;

    private PatternEntry(PatternEntry.PatternEntryType type) {
        this.type = type;
    }

    public static PatternEntry createMap(Pattern keyPattern) {
        PatternEntry pe = new PatternEntry(PatternEntryType.MAP);
        pe.keyPattern = keyPattern == null ? Pattern.compile(".*") : keyPattern;
        return pe;
    }

    /**
     * @param listIndex
     *            0 or positive value: Count from beginning, negative value:
     *            count from end (-1 is last), <code>null</code>: match any list
     *            entry.
     * @return Created entry.
     */
    public static PatternEntry createList(Integer listIndex) {
        PatternEntry pe = new PatternEntry(PatternEntryType.LIST);
        pe.listIndex = listIndex;
        return pe;
    }

    public static PatternEntry createWildcardAny() {
        PatternEntry pe = new PatternEntry(PatternEntryType.WILDCARD_ANY);
        return pe;
    }

    public static PatternEntry createWildcardOne() {
        PatternEntry pe = new PatternEntry(PatternEntryType.WILDCARD_ONE);
        return pe;
    }

    public PatternEntry.PatternEntryType getType() {
        return type;
    }

    public Pattern getKeyPattern() {
        if (type != PatternEntryType.MAP) {
            throw new IllegalStateException("type is " + type);
        }
        return keyPattern;
    }

    /**
     * @return 0 or positive value: Count from beginning, negative value: count
     *         from end (-1 is last), <code>null</code>: match any list entry.
     */
    public Integer getListIndex() {
        if (type != PatternEntryType.LIST) {
            throw new IllegalStateException("type is " + type);
        }
        return listIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
        case LIST:
            sb.append('[');
            if (listIndex == null) {
                sb.append('#');
            } else {
                sb.append(Integer.toString(listIndex));
            }
            sb.append(']');
            break;
        case MAP:
            sb.append("['");
            for (char ch : keyPattern.toString().toCharArray()) {
                if (ch == '\'') {
                    sb.append("''");
                } else {
                    sb.append(ch);
                }
            }
            sb.append("']");
            break;
        case WILDCARD_ANY:
            sb.append('*');
            break;
        case WILDCARD_ONE:
            sb.append('?');
            break;
        }
        return sb.toString();
    }
}