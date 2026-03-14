/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types.tests;

import static org.apache.xpath.objects.XObject.CLASS_XS_DAYTIME_DURATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSInteger;

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
public class XSDayTimeDurationTest {

    private static final int ONE_DAY_SECONDS = 86_400;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	// no op	
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
		// no op
	}

    @Test
    public void constructor() throws TransformerException {
        final ResultSequence input = new ResultSequence();
        input.add(new XSDayTimeDuration(86_000));

        final ResultSequence sequence = new XSDayTimeDuration().constructor(input);

        assertNotNull(sequence);
        assertEquals(1, sequence.size());
        assertEquals("PT23H53M20S", ((XSDayTimeDuration)sequence.item(0)).stringValue());
    }

    @Test
    public void parseDayTimeDuration1() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(0);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT0S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(0, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration2() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(12);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT12S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(12, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration3() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(34);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT34S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(34, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration4() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(51);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT51S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(51, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration5() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(59);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT59S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(59, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration6() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(60);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT1M");

        assertTrue(expected.equals(parsedVal));

        assertEquals(60, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration7() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(90);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT1M30S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(90, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration8() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(300);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT5M");

        assertTrue(expected.equals(parsedVal));

        assertEquals(300, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration9() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(500);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT8M20S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(500, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration10() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(3600);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT1H");

        assertTrue(expected.equals(parsedVal));

        assertEquals(3600, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration11() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(86_000);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("PT23H53M20S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(86_000, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration12() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(ONE_DAY_SECONDS);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("P1D");

        assertTrue(expected.equals(parsedVal));

        assertEquals(ONE_DAY_SECONDS, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration13() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(90_000);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("P1DT1H");

        assertTrue(expected.equals(parsedVal));

        assertEquals(90_000, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration14() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(93_600);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("P1DT2H");

        assertTrue(expected.equals(parsedVal));

        assertEquals(93_600, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration15() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(371_280);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("P4DT7H8M");

        assertTrue(expected.equals(parsedVal));

        assertEquals(371_280, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration16() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(378_500);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("P4DT9H8M20S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(378_500, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration17() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(500_000);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("P5DT18H53M20S");

        assertTrue(expected.equals(parsedVal));

        assertEquals(500_000, parsedVal.value(), 0);
    }
    
    @Test
    public void parseDayTimeDuration18() throws TransformerException {
        final XSDuration expected = new XSDayTimeDuration(604_800);
        final XSDuration parsedVal = XSDayTimeDuration.parseDayTimeDuration("P7D");

        assertTrue(expected.equals(parsedVal));

        assertEquals(604_800, parsedVal.value(), 0);
    }

    @Test
    public void parseDayTimeDurationUnsupportedFormat() {
    	try {
    	    XSDayTimeDuration.parseDayTimeDuration("");
    	}
    	catch (TransformerException ex) {
    		// This test case, passes if this exception occurs
    		assertTrue(true);
    	}
    }

    @Test
    public void typeName() {
        assertEquals("dayTimeDuration", new XSDayTimeDuration().typeName());
    }

    @Test
    public void stringType() {
        assertEquals("xs:dayTimeDuration", new XSDayTimeDuration().stringType());
    }
    
    @Test
    public void stringValue() {        
        strValueAssertHelper("PT0S", 0);                
        strValueAssertHelper("PT12S", 12);
        strValueAssertHelper("PT34S", 34);        
        strValueAssertHelper("PT51S", 51);
        strValueAssertHelper("PT59S", 59);
        strValueAssertHelper("PT1M", 60);
        strValueAssertHelper("PT1M30S", 90);
        strValueAssertHelper("PT5M", 300);
        strValueAssertHelper("PT8M20S", 500);
        strValueAssertHelper("PT1H", 3600);
        strValueAssertHelper("PT23H53M20S", 86_000);
        strValueAssertHelper("P1D", ONE_DAY_SECONDS);
        strValueAssertHelper("P1DT1H", 90_000);
        strValueAssertHelper("P1DT2H", 93_600);
        strValueAssertHelper("P4DT7H8M", 371_280);
        strValueAssertHelper("P4DT9H8M20S", 378_500);
        strValueAssertHelper("P5DT18H53M20S", 500_000);
        strValueAssertHelper("P7D", 604_800);
    }

    @Test
    public void add() {
        final XSDayTimeDuration oneMinute = new XSDayTimeDuration(60);
        final XSDayTimeDuration fiveMinute = new XSDayTimeDuration(300);

        assertEquals(360, oneMinute.add(fiveMinute).value(), 0);
    }

    @Test
    public void subtract() {
        final XSDayTimeDuration oneMinute = new XSDayTimeDuration(60);
        final XSDayTimeDuration fiveMinute = new XSDayTimeDuration(300);

        assertEquals(240.0, fiveMinute.subtract(oneMinute).value(), 0);
    }

    @Test
    public void mult() throws TransformerException {
        final XSDayTimeDuration oneMinute = new XSDayTimeDuration(60);

        assertEquals(120.0, oneMinute.mult(new XSInteger("2")).value(), 0);

        assertEquals(60.0, oneMinute.value(), 0);

        assertEquals(300.0, oneMinute.mult(new XSInteger("5")).value(), 0);
    }

    @Test
    public void div() throws TransformerException {
        final XSDayTimeDuration oneMinute = new XSDayTimeDuration(60);

        assertEquals(30.0, oneMinute.div(new XSInteger("2")).value(), 0);

        assertEquals(60.0, oneMinute.value(), 0);

        assertEquals(20.0, oneMinute.div(new XSInteger("3")).value(), 0);
    }

    @Test
    public void getType() {
        assertEquals(CLASS_XS_DAYTIME_DURATION, new XSDayTimeDuration().getType());
    }
    
    /**
     * Test case helper method to assert 'success' or 'failure'.
     * 
     * @param expected								xs:dayTimeDuration string value
     * @param seconds								Supplied, integer valued 'second'.
     */
    private void strValueAssertHelper(String expected, int seconds) {
    	
    	assertEquals(expected, new XSDayTimeDuration(seconds).stringValue());
    	
    	if (seconds < ONE_DAY_SECONDS) {
            // Up to the one-day point, XSDayTimeDuration uses the same string format as java.time.Duration
            assertEquals(
            		"XSDayTimeDuration should do the same string format as java.time.Duration",
                    new XSDayTimeDuration(seconds).stringValue(),
                    Duration.of(seconds, ChronoUnit.SECONDS).toString()                    
            );
        }
    }
}