/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types.tests;

import static org.apache.xpath.objects.XObject.CLASS_XS_DOUBLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSDouble;

/**
 * Xalan-J XPath 3.1 data type xs:double test cases.
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
public class XSDoubleTest {
	
	@BeforeClass
    public static void setUpBeforeClass() throws Exception {
		// no op
	}
	
	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		// no op
	}

    @Test
    public void parseDouble() throws TransformerException {
        assertEquals(new XSDouble("61.64"), XSDouble.parseDouble("61.64"));
        assertEquals(new XSDouble("2.03698"), XSDouble.parseDouble("2.03698"));
        assertEquals(new XSDouble("694.62"), XSDouble.parseDouble("694.62"));
    }

    @Test
    public void constructor() throws TransformerException {
        final ResultSequence input = new ResultSequence();
        input.add(new XSDouble("1.3"));

        final ResultSequence sequence = new XSDouble().constructor(input);

        assertNotNull(sequence);
        assertEquals(1, sequence.size());
        assertEquals("1.3", ((XSDouble)sequence.item(0)).stringValue());
    }

    @Test
    public void typeName() {
        assertEquals("double", new XSDouble().typeName());
    }

    @Test
    public void stringType() {
        assertEquals("xs:double", new XSDouble().stringType());
    }

    @Test
    public void stringValue() throws TransformerException {
        assertEquals("89.4", new XSDouble("89.4").stringValue());
    }

    @Test
    public void negativeZero() throws TransformerException {
        assertTrue(new XSDouble("-0").negativeZero());
        assertTrue(new XSDouble("-0.0").negativeZero());
    }

    @Test
    public void doubleValue() throws TransformerException {
        assertEquals(1.3, new XSDouble("1.3").doubleValue(), 0);
    }

    @Test
    public void nan() {
        assertTrue(new XSDouble(Double.NaN).nan());
    }

    @Test
    public void infinite() throws TransformerException {
        assertTrue(new XSDouble("INF").infinite());
        assertTrue(new XSDouble("-INF").infinite());
        assertTrue(new XSDouble(Double.POSITIVE_INFINITY).infinite());
        assertTrue(new XSDouble(Double.NEGATIVE_INFINITY).infinite());
    }

    @Test
    public void zero() throws TransformerException {
        assertTrue(new XSDouble("0").zero());
        assertTrue(new XSDouble("0.0").zero());
        assertTrue(new XSDouble("0.00").zero());
    }

    @Test
    public void testEquals() throws TransformerException {
        assertTrue(new XSDouble("1.3").equals(new XSDouble("1.3")));
        assertTrue(new XSDouble("1337.890625").equals(new XSDouble("1337.890625")));

        assertFalse(new XSDouble("0.1").equals(new XSDouble("0.11111111111")));
        assertFalse(new XSDouble("1.5").equals(new XSDouble("15")));
        assertFalse(new XSDouble("8.6").equals(new XSDouble("65646.874")));
        assertFalse(new XSDouble("256.684").equals(new XSDouble("68.96864")));
    }

    @Test
    public void lt() throws TransformerException {
        assertTrue(new XSDouble("1.3").lt(new XSDouble("2")));
        assertTrue(new XSDouble("1.3").lt(new XSDouble("1.4")));
        assertTrue(new XSDouble("1.3").lt(new XSDouble("1.30001")));
    }

    @Test
    public void gt() throws TransformerException {
        assertTrue(new XSDouble("1.3").gt(new XSDouble("1")));
        assertTrue(new XSDouble("1.3").gt(new XSDouble("1.2")));
        assertTrue(new XSDouble("1.3").gt(new XSDouble("1.2999999999999999")));
    }

    @Test
    public void getType() {
        assertEquals(CLASS_XS_DOUBLE, new XSDouble().getType());
    }
}