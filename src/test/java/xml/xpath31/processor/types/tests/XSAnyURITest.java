/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types.tests;

import static org.apache.xpath.objects.XObject.CLASS_XS_ANY_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSAnyURI;

/**
 * Xalan-J XPath 3.1 data type xs:anyURI test cases.
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
public class XSAnyURITest {
	
	private static final List<String> VALUE_SOURCE_LIST = new ArrayList<String>();
	
	@BeforeClass
    public static void setUpBeforeClass() throws Exception {
		VALUE_SOURCE_LIST.add("domain.tld");
		VALUE_SOURCE_LIST.add("https://domain.tld");
		VALUE_SOURCE_LIST.add("http://[2001:db8::1]/");
		VALUE_SOURCE_LIST.add("mailto:java-net@java.sun.com");
		VALUE_SOURCE_LIST.add("news:comp.lang.java");
		VALUE_SOURCE_LIST.add("urn:isbn:096139210x");
		VALUE_SOURCE_LIST.add("file:///foo/bar");
		VALUE_SOURCE_LIST.add("../../relative/path");
    }
	
	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		// no op
	}
	
	@Test
	public void validUriStrings() {		
		int size1 = VALUE_SOURCE_LIST.size();
		try {
			for (int idx = 0; idx < size1; idx++) {
				String str1 = VALUE_SOURCE_LIST.get(idx);
				XSAnyURI value1 = new XSAnyURI(str1);
				XSAnyURI value2 = new XSAnyURI(URI.create(str1));
			}
		}
		catch (Exception ex) {
			// This test will fail here, if this exception occurs
			assertTrue(false);
		}
	}
	
	@Test
    public void constructor() throws TransformerException {
        final ResultSequence input = new ResultSequence();
        input.add(new XSAnyURI("https://website.tld"));

        final ResultSequence sequence = new XSAnyURI().constructor(input);

        assertNotNull(sequence);
        assertEquals(1, sequence.size());
        assertEquals("https://website.tld", ((XSAnyURI)sequence.item(0)).stringValue());
    }

    @Test
    public void typeName() {
        assertEquals("anyURI", new XSAnyURI().typeName());
    }

    @Test
    public void stringType() {
        assertEquals("xs:anyURI", new XSAnyURI().stringType());
    }

    @Test
    public void stringValue() {
        assertEquals("https://website.tld", new XSAnyURI("https://website.tld").stringValue());
    }

    @Test
    public void testEquals() {
        assertTrue(new XSAnyURI("https://website.tld").equals(new XSAnyURI("https://website.tld")));
    }

    @Test
    public void eq() throws TransformerException {
        assertTrue(new XSAnyURI("https://website.tld").eq(new XSAnyURI("https://website.tld")));
    }

    @Test
    public void lt() throws TransformerException {
        assertTrue(new XSAnyURI("https://aaaa.domain.tld").lt(new XSAnyURI("https://bbbb.domain.tld")));
    }

    @Test
    public void gt() throws TransformerException {
        assertTrue(new XSAnyURI("https://bbbb.domain.tld").gt(new XSAnyURI("https://aaaa.domain.tld")));
    }

    @Test
    public void getType() {
        assertEquals(CLASS_XS_ANY_URI, new XSAnyURI().getType());
    }

}
