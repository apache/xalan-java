/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types.tests;

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

/**
 * Xalan-J XPath 3.1 data type xs:duration test cases.
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
public class XSDurationTest {

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
        input.add(new XSDuration(86_000));

        final ResultSequence sequence = new XSDuration().constructor(input);

        assertNotNull(sequence);
        assertEquals(1, sequence.size());
        assertEquals("PT23H53M20S", ((XSDuration)sequence.item(0)).stringValue());
    }

    @Test
    public void typeName() {
        assertEquals("XSDuration always returns 'duration' for this method", "duration", new XSDuration().typeName());
    }

    @Test
    public void stringType() {
        assertEquals("XSDuration always returns 'xs:duration' for this method", "xs:duration", new XSDuration().stringType());
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
    public void lt() {
        final XSDuration oneMinute = new XSDuration(60);
        final XSDuration fiveMinute = new XSDuration(300);

        assertTrue(oneMinute.lt(fiveMinute));
    }

    @Test
    public void gt() {
        final XSDuration oneMinute = new XSDuration(60);
        final XSDuration fiveMinute = new XSDuration(300);

        assertTrue(fiveMinute.gt(oneMinute));
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