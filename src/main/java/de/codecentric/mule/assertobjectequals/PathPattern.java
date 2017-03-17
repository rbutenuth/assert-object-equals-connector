package de.codecentric.mule.assertobjectequals;

import java.util.Arrays;

/**
 * Can match against a {@link Path}.
 */
public class PathPattern {
	private PatternEntry[] entries;

	/**
	 * @param entries Entries, array will be copied to avoid state leakage.
	 */
	public PathPattern(PatternEntry[] entries) {
		this.entries = Arrays.copyOf(entries, entries.length);
	}

	/**
	 * @return number of {@link PatternEntry}s
	 */
	public int size() {
		return entries.length;
	}

	public PatternEntry getEntry(int index) {
		return entries[index];
	}

	/**
	 * @param path Path to match against.
	 * @return Does it match?
	 */
	public boolean matches(Path path) {
		return matches(path, entries.length - 1);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (PatternEntry entry : entries) {
			sb.append(entry.toString());
		}
		return sb.toString();
	}

	private boolean matches(Path path, int start) {
		if (start == -1) {
			return path.isRoot();
		} else {
			PatternEntry pe = entries[start];
			if (path.isRoot()) {
				// WILDCARD_ANY matches the empty path
				return pe.getType() == PatternEntry.PatternEntryType.WILDCARD_ANY && start == 0;
			}
			switch (pe.getType()) {
			case LIST:
				return matchesList(path, pe) && matches(path.getPredecessor(), start - 1);
			case MAP:
				return matchesMap(path, pe) && matches(path.getPredecessor(), start - 1);
			case WILDCARD_ONE:
				return matches(path.getPredecessor(), start - 1);
			case WILDCARD_ANY:
				return matchesWildcardAny(path, start);
			default:
				throw new IllegalStateException("Unknown enum constant");
			}

		}
	}

	private boolean matchesList(Path path, PatternEntry pe) {
		if (!path.isList()) {
			return false;
		}
		if (pe.getListIndex() == null) {
			return true;
		}
		int index = pe.getListIndex();
		if (index >= 0 && path.getIndex() == index) {
			return true;
		}
		if (index < 0 && path.getIndex() == path.getListSize() + index) {
			return true;
		}
		return false;
	}

	private boolean matchesMap(Path path, PatternEntry pe) {
		if (!path.isMap()) {
			return false;
		}
		return pe.getKeyPattern().matcher(path.getKey()).matches();
	}

	private boolean matchesWildcardAny(Path path, int start) {
		// wildcard matches nothing
		if (matches(path, start - 1)) {
			return true;
		}
		// wildcard matches one path element
		if (matches(path.getPredecessor(), start - 1)) {
			return true;
		}
		// wildcard matches more than path element (apply wildcard again)
		if (matches(path.getPredecessor(), start)) {
			return true;
		}
		return false;
	}
}
