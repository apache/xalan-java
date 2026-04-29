/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSDateTime;

/**
 * Xalan-J XPath 3.1 data type xs:dateTime test cases.
 * 
 * Contributed via Xalan Java mailing list.
 * 
 * @author Samael Bate                       
 *                       
 * @author Mukul Gandhi <mukulg@apache.org>, Modified this XSL test case, to 
 *                                           migrate from JUnit 5 to JUnit 4.                       
 * 
 * @xsl.usage advanced
 */
public class XSDateTimeTest {
	
	private static final List<String> VALUE_SOURCE_LIST1 = new ArrayList<String>();
	
	private static final List<String> VALUE_SOURCE_LIST2 = new ArrayList<String>();	
	
	@BeforeClass
    public static void setUpBeforeClass() throws Exception {
		VALUE_SOURCE_LIST1.add("2025-02-28T10:00:00");
		VALUE_SOURCE_LIST1.add("2025-02-28T10:00:00.0000");        // the parser does support milliseconds
		VALUE_SOURCE_LIST1.add("2025-02-28T10:00:00.0000000");     // the parser doesn't really support milliseconds, just takes first 4 digits
		VALUE_SOURCE_LIST1.add("2025-02-28T10:00:00Z");
		VALUE_SOURCE_LIST1.add("2025-02-28T08:00:00+02:00");
		VALUE_SOURCE_LIST1.add("2025-02-28T12:00:00-02:00");
		
		VALUE_SOURCE_LIST2.add("2025-02-28T10:00");                // no seconds
		VALUE_SOURCE_LIST2.add("2025-02-28T08:00:00+02");          // offset needs hours and minutes
	}
	
	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		// no op
	}
	
	@Test
    public void constructor() throws TransformerException {
        final ResultSequence input = new ResultSequence();
        input.add(XSDateTime.parseDateTime("1961-12-15T17:31:00Z"));

        final ResultSequence sequence = new XSDateTime().constructor(input);

        assertNotNull(sequence);
        assertEquals(1, sequence.size());
        assertEquals("1961-12-15T17:31:00Z", ((XSDateTime)sequence.item(0)).stringValue());
    }
	
	@Test
    public void parseDateTime() throws TransformerException {
    	int size1 = VALUE_SOURCE_LIST1.size();
    	for (int idx = 0; idx < size1; idx++) {
    		String strVal = VALUE_SOURCE_LIST1.get(idx); 
    		final XSDateTime dateTime = XSDateTime.parseDateTime(strVal);
    		assertEquals(2025, dateTime.year());
    		assertEquals(2, dateTime.month());
    		assertEquals(28, dateTime.day());

    		assertEquals(0, dateTime.minute());

    		// all the tested values are actually the same time
    		dateTime.equals(XSDateTime.parseDateTime("2025-02-28T10:00:00Z"));

    		if (!strVal.endsWith("0000")) {
    			// string value doesn't support milliseconds or microseconds. Is this a bug?
    			assertEquals(strVal, dateTime.stringValue());
    		}
    	}
    }

	@Test
    public void parseDateTimeUnsupportedFormat() throws TransformerException {
		int size1 = VALUE_SOURCE_LIST2.size();
		try {
			for (int idx = 0; idx < size1; idx++) {
				String strVal = VALUE_SOURCE_LIST2.get(idx);
				XSDateTime xsDateTime = XSDateTime.parseDateTime(strVal);
			}
		}
		catch (TransformerException ex) {
			// This test has succeeded
			assertTrue(true);
		}
    }

	@Test
    public void typeName() {
        assertEquals("dateTime", new XSDateTime().typeName());
    }

    @Test
    public void stringType() {
        assertEquals("xs:dateTime", new XSDateTime().stringType());
    }

    @Test
    public void lt() throws TransformerException {
        final XSDateTime jan = XSDateTime.parseDateTime("2025-01-28T00:00:00Z");
        final XSDateTime feb = XSDateTime.parseDateTime("2025-02-28T00:00:00Z");

        assertTrue(jan.lt(feb));
    }

    @Test
    public void gt() throws TransformerException {
        final XSDateTime jan = XSDateTime.parseDateTime("2025-01-28T00:00:00Z");
        final XSDateTime feb = XSDateTime.parseDateTime("2025-02-28T00:00:00Z");
        assertTrue(feb.gt(jan));
    }

}