/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types.tests;

import static org.junit.Assert.assertNull;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSNumericType;

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
public class XSNumericTypeTest {
	
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
        assertNull("XSNumericType always returns null for this method", new XSNumericType().constructor(new ResultSequence()));
    }

    @Test
    public void typeName() {
        assertNull("XSNumericType always returns null for this method", new XSNumericType().typeName());
    }

    @Test
    public void stringType() {
        assertNull("XSNumericType always returns null for this method", new XSNumericType().stringType());
    }

    @Test
    public void stringValue() {
        assertNull("XSNumericType always returns null for this method", new XSNumericType().stringValue());
    }
    
}