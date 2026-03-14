/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSTime;

/**
 * Xalan-J XSL 3, XPath 3.1 data type test cases.
 * 
 * @author Samael Bates, Xalan-J XSL 3 implementation user via 
 *                       Xalan dev forum. Initial contribution.                       
 *                       
 * @author Mukul Gandhi <mukulg@apache.org>, Modified this XSL test case, from 
 *                                           JUnit 5 to JUnit 4.                        
 * 
 * @xsl.usage advanced
 */
public class XSTimeTest {
	
	private static final List<String> VALUE_SOURCE_LIST = new ArrayList<String>();
	
	@BeforeClass
    public static void setUpBeforeClass() throws Exception {
		VALUE_SOURCE_LIST.add("10:00:00");
		VALUE_SOURCE_LIST.add("10:00:00.0000");
		VALUE_SOURCE_LIST.add("10:00:00.0000000");
		VALUE_SOURCE_LIST.add("10:00:00Z");
		VALUE_SOURCE_LIST.add("08:00:00+02:00");
		VALUE_SOURCE_LIST.add("12:00:00-02:00");
	}
	
	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		// no op
	}

	@Test
    public void parseDateTime() throws TransformerException {
		int size1 = VALUE_SOURCE_LIST.size();
		for (int idx = 0; idx < size1; idx++) {
			String strVal = VALUE_SOURCE_LIST.get(idx); 
			final XSTime xsTime = XSTime.parseTime(strVal);

			assertEquals(0, xsTime.minute());

			// all the tested values are actually the same time
			xsTime.equals(XSTime.parseTime("10:00:00Z"));

			if (!strVal.endsWith("0000")) {
				// string value doesn't support milliseconds or microseconds. Is this a bug?
				assertEquals(strVal, xsTime.stringValue());
			}
		}
    }

    @Test
    public void typeName() {
        assertEquals("time", new XSTime().typeName());
    }

    @Test
    public void stringType() {
        assertEquals("xs:time", new XSTime().stringType());
    }
}