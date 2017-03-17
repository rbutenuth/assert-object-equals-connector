package de.codecentric.mule.assertobjectequals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parses <code>String</code> patterns into a {@link PathPattern}. The <code>String</code> must contain
 * zero to <code>n</code> parts. The parts can have the following syntax:
 * <ul>
 * <li><code>?</code>: Wildcard one, matches one element in a path</li>
 * <li><code>*</code>: Wildcard any, matches zero to <code>n</code> elements in a path</li>
 * <li><code>[#]</code>: List wildcard, matches a list entry with any index</li>
 * <li><code>[0]</code>: Matches a list entry with the given number. 0 or positive numbers: Count from beginning, negative number: Cound from end (-1 is last element)</li>
 * <li><code>['.*']</code>: Matches a map entry where the key must match the given regular expression. If you need a ' in the expression, just write ''. The example '.*' matches all keys.</li>
 * </ul>
 */
public class PathPatternParser {
	private static class State {
		State(String input) {
			this.input = input;
		}

		final String input;
		int position;

		boolean eof() {
			return position == input.length();
		}

		char peek() {
			return input.charAt(position);
		}

		char peek(int delta) {
			return input.charAt(position + delta);
		}

		char next() {
			if (eof()) {
				throw new IllegalStateException("unexpected end of path pattern");
			}
			return input.charAt(position++);
		}

		void nextExpected(char c) {
			if (c != peek()) {
				throw new IllegalArgumentException("Expect '" + c + "' at position " + position + " but found '" + peek() + "'");
			}
			next();
		}

		int getPosition() {
			return position;
		}
	}

	public PathPattern parse(String input) {
		List<PatternEntry> entries = new ArrayList<>();
		State state = new State(input);
		while (!state.eof()) {
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
				throw new IllegalArgumentException("Unknown character '" + state.peek() + "' at position " + state.getPosition());
			}
		}
		return new PathPattern(entries.toArray(new PatternEntry[entries.size()]));
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
					throw new IllegalArgumentException("' must be followed by ' or ], not '" + state.peek() + "' at position " + state.getPosition());
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
}
