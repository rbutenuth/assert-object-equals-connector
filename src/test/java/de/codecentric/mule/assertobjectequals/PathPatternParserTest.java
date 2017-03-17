package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PathPatternParserTest {
	private PathPatternParser parser;

	@Before
	public void before() {
		parser = new PathPatternParser();
	}

	@After
	public void after() {
		parser = null;
	}

	@Test
	public void emtpyStringGivesEmptyPattern() {
		String pathAsString = "";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(0, pp.size());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void questionmarkGivesWildcardOne() {
		String pathAsString = "?";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.WILDCARD_ONE, pp.getEntry(0).getType());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void starGivesWildcardAny() {
		String pathAsString = "*";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.WILDCARD_ANY, pp.getEntry(0).getType());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void hashGivesListAny() {
		String pathAsString = "[#]";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.LIST, pp.getEntry(0).getType());
		assertNull(pp.getEntry(0).getListIndex());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void numberGivesListSpecific() {
		String pathAsString = "[42]";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.LIST, pp.getEntry(0).getType());
		assertEquals(42, (int) pp.getEntry(0).getListIndex());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void quotedEmptyTextGivesMap() {
		String pathAsString = "['']";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
		assertEquals("", pp.getEntry(0).getKeyPattern().pattern());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void quotedTextGivesMap() {
		String pathAsString = "['foo']";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
		assertEquals("foo", pp.getEntry(0).getKeyPattern().pattern());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void quotedTextWithEscapedCharsGivesMap() {
		String pathAsString = "['fo''o']";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
		assertEquals("fo'o", pp.getEntry(0).getKeyPattern().pattern());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void quotedTextWithEscapedCharsAtEndGivesMap() {
		String pathAsString = "['foo''']";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
		assertEquals("foo'", pp.getEntry(0).getKeyPattern().pattern());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void quotedTextWithEscapedCharsAtStartGivesMap() {
		String pathAsString = "['''bar']";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
		assertEquals("'bar", pp.getEntry(0).getKeyPattern().pattern());
		assertEquals(pathAsString, pp.toString());
	}

	@Test
	public void quotedTextWithDoubleEscapeCharsGivesMap() {
		String pathAsString = "['fo''''so']";
		PathPattern pp = parser.parse(pathAsString);
		assertEquals(1, pp.size());
		assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
		assertEquals("fo''so", pp.getEntry(0).getKeyPattern().pattern());
		assertEquals(pathAsString, pp.toString());
	}
}
