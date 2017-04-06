package de.codecentric.mule.assertobjectequals;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Parses <code>String</code> patterns into a {@link PathPattern}. The
 * <code>String</code> must contain zero to <code>n</code> parts. The parts can
 * have the following syntax:
 * <ul>
 * <li><code>?</code>: Wildcard one, matches one element in a path</li>
 * <li><code>*</code>: Wildcard any, matches zero to <code>n</code> elements in
 * a path</li>
 * <li><code>[#]</code>: List wildcard, matches a list entry with any index</li>
 * <li><code>[0]</code>: Matches a list entry with the given number. 0 or
 * positive numbers: Count from beginning, negative number: Cound from end (-1
 * is last element)</li>
 * <li><code>['.*']</code>: Matches a map entry where the key must match the
 * given regular expression. If you need a ' in the expression, just write ''.
 * The example '.*' matches all keys.</li>
 * </ul>
 */
public class PathPatternParser {
    static class State {
        final String input;
        int position;

        State(String input) {
            this.input = input;
        }

        boolean eof() {
            return position == input.length();
        }

        char peek() {
            assertNotEof();
            return input.charAt(position);
        }

        char peek(int delta) {
            return input.charAt(position + delta);
        }

        char next() {
            assertNotEof();
            return input.charAt(position++);
        }

        void nextExpected(char c) {
            if (c != peek()) {
                throw new IllegalArgumentException(
                        "Expect '" + c + "' at position " + position + " but found '" + peek() + "'");
            }
            next();
        }

        private void assertNotEof() {
            if (eof()) {
                throw new IllegalArgumentException("unexpected end of path pattern");
            }
        }

        int getPosition() {
            return position;
        }

        @Override
        public String toString() {
            if (eof()) {
                return "EOF";
            } else {
                StringBuilder sb = new StringBuilder(2 * input.length() + 3);
                sb.append(input).append(System.lineSeparator());
                for (int i = 0; i < position; i++) {
                    sb.append(' ');
                }
                sb.append('^');
                return sb.toString();
            }
        }
    }

    public PathPattern parse(String input) {
        State state = new State(input);
        PatternEntry[] entries = parseEntries(state);
        EnumSet<PathOption> options = parseOptions(state);
        return new PathPattern(entries, options);
    }

    private PatternEntry[] parseEntries(State state) {
        List<PatternEntry> entries = new ArrayList<>();
        skipWhitespace(state);
        while (!state.eof() && isPathStartCharacter(state.peek())) {
            switch (state.peek()) {
            case '?':
                entries.add(PatternEntry.createWildcardOne());
                state.next();
                break;
            case '*':
                entries.add(PatternEntry.createWildcardAny());
                state.next();
                break;
            case '[':
                state.next();
                entries.add(listOrMap(state));
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown character '" + state.peek() + "' at position " + state.getPosition());
            }
        }
        return entries.toArray(new PatternEntry[entries.size()]);
    }

    private boolean isPathStartCharacter(char ch) {
        return ch == '?' || ch == '*' || ch == '[';
    }

    private PatternEntry listOrMap(State state) {
        if (state.peek() == '\'') {
            return map(state);
        } else {
            return list(state);
        }
    }

    private PatternEntry map(State state) {
        state.nextExpected('\'');
        StringBuilder sb = new StringBuilder();
        while (!(state.peek() == '\'' && state.peek(1) == ']')) {
            if (state.peek() == '\'') {
                state.next(); // skip first '
                if (state.peek() == '\'') {
                    sb.append(state.next()); // skip second '
                } else {
                    throw new IllegalArgumentException("' must be followed by ' or ], not '" + state.peek()
                            + "' at position " + state.getPosition());
                }
            } else {
                sb.append(state.next());
            }
        }
        state.nextExpected('\'');
        state.nextExpected(']');
        return PatternEntry.createMap(Pattern.compile(sb.toString()));
    }

    private PatternEntry list(State state) {
        if (state.peek() == '#') {
            state.next();
            state.nextExpected(']');
            return PatternEntry.createList(null);
        }
        StringBuilder sb = new StringBuilder();
        if (state.peek() == '-') {
            sb.append(state.next());
        }
        while (state.peek() != ']') {
            sb.append(state.next());
        }
        state.next();
        int index = Integer.parseInt(sb.toString());
        return PatternEntry.createList(index);
    }

    private EnumSet<PathOption> parseOptions(State state) {
        EnumSet<PathOption> options = EnumSet.noneOf(PathOption.class);
        while (!state.eof()) {
            String word = readNextWord(state);
            if (StringUtils.isEmpty(word)) {
                if (!state.eof()) {
                    throw new IllegalArgumentException("'" + state.peek() + "' is not valid as start of option.");
                }
            } else {
                options.add(stringToOption(word));
            }
        }
        return options;
    }

    private PathOption stringToOption(String word) {
        try {
            return Enum.valueOf(PathOption.class, word.toUpperCase());
        } catch (IllegalArgumentException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Illegal path option \"").append(word).append("\", valid options are: ");
            for (PathOption option : PathOption.values()) {
                sb.append(option.toString()).append(", ");
            }
            String message = sb.substring(0, sb.length() - 2);
            throw new IllegalArgumentException(message);
        }
    }

    private String readNextWord(State state) {
        skipWhitespace(state);
        StringBuilder sb = new StringBuilder();
        while (!state.eof() && (Character.isLetterOrDigit(state.peek()) || state.peek() == '_')) {
            sb.append(state.next());
        }
        return sb.toString();
    }

    private void skipWhitespace(State state) {
        while (!state.eof() && Character.isWhitespace(state.peek())) {
            state.next();
        }
    }
}
