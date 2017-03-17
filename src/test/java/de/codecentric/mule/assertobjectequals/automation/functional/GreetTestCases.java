package de.codecentric.mule.assertobjectequals.automation.functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import de.codecentric.mule.assertobjectequals.AssertObjectEqualsConnector;

public class GreetTestCases extends AbstractTestCase<AssertObjectEqualsConnector> {

	public GreetTestCases() {
		super(AssertObjectEqualsConnector.class);
	}

	@Before
	public void setup() {
		// TODO
	}

	@After
	public void tearDown() {
		// TODO
	}

	@Test
	@Ignore
	public void verify() {
		// TODO
		//		java.lang.String expected = null;
		//		java.lang.String friend = null;
		//		assertEquals(getConnector().greet(friend), expected);
	}

}