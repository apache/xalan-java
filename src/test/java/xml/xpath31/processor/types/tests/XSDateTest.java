/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSDate;

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
public class XSDateTest {
	
	private static final List<String> VALUE_SOURCE_LIST = new ArrayList<String>();
	
	@BeforeClass
    public static void setUpBeforeClass() throws Exception {
		VALUE_SOURCE_LIST.add("2025-01-11");
		VALUE_SOURCE_LIST.add("2024-03-07");
		VALUE_SOURCE_LIST.add("2018-06-23");
		VALUE_SOURCE_LIST.add("1993-08-14");
		VALUE_SOURCE_LIST.add("1975-11-30");
	}
	
	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		// no op
	}

    @Test
    public void constructor() throws TransformerException {
        final ResultSequence input = new ResultSequence();
        input.add(XSDate.parseDate("1982-10-01"));

        final ResultSequence sequence = new XSDate().constructor(input);

        assertNotNull(sequence);
        assertEquals(1, sequence.size());
        assertEquals("1982-10-01", ((XSDate)sequence.item(0)).stringValue());
    }

    @Test
    public void parseDate() throws TransformerException {
    	int size1 = VALUE_SOURCE_LIST.size();
    	for (int idx = 0; idx < size1; idx++) {
    		String strVal = VALUE_SOURCE_LIST.get(idx); 
    		final XSDate dateTime = XSDate.parseDate(strVal);

    		final LocalDate localDate = LocalDate.parse(strVal);
    		assertEquals(localDate.getYear(), dateTime.year());
    		assertEquals(localDate.getMonth().getValue(), dateTime.month());
    		assertEquals(localDate.getDayOfMonth(), dateTime.day());
    	}
    }

    @Test
    public void typeName() {
        assertEquals("date", new XSDate().typeName());
    }

    @Test
    public void stringType() {
        assertEquals("xs:date", new XSDate().stringType());
    }

    @Test
    public void lt() throws TransformerException {
        final XSDate march = XSDate.parseDate("2000-03-21");
        final XSDate april = XSDate.parseDate("2000-04-21");
        assertTrue(march.lt(april));
    }

    @Test
    public void gt() throws TransformerException {
        final XSDate march = XSDate.parseDate("2000-03-21");
        final XSDate april = XSDate.parseDate("2000-04-21");
        assertTrue(april.gt(march));
    }

    @Test
    public void stringValue() throws TransformerException {
    	int size1 = VALUE_SOURCE_LIST.size();
    	for (int idx = 0; idx < size1; idx++) {
    		String strVal = VALUE_SOURCE_LIST.get(idx);

    		assertEquals(strVal, XSDate.parseDate(strVal).stringValue());
    	}
    }
}