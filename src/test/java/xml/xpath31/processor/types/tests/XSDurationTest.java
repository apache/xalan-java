/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xml.xpath31.processor.types.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSDuration;

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
        assertEquals("PT0S", new XSDuration(0).stringValue());
        assertEquals("PT12S", new XSDuration(12).stringValue());
        assertEquals("PT34S", new XSDuration(34).stringValue());
        assertEquals("PT51S", new XSDuration(51).stringValue());
        assertEquals("PT59S", new XSDuration(59).stringValue());
        assertEquals("PT1M", new XSDuration(60).stringValue());        
        assertEquals("PT1M", new XSDuration(60).stringValue());
        assertEquals("PT1M30S", new XSDuration(90).stringValue());
        assertEquals("PT5M", new XSDuration(300).stringValue());
        assertEquals("PT8M20S", new XSDuration(500).stringValue());
        assertEquals("PT1H", new XSDuration(3600).stringValue());        
        assertEquals("PT23H53M20S", new XSDuration(86_000).stringValue());
        assertEquals("P1D", new XSDuration(ONE_DAY_SECONDS).stringValue());
        assertEquals("P1DT1H", new XSDuration(90_000).stringValue());
        assertEquals("P1DT2H", new XSDuration(93_600).stringValue());
        assertEquals("P4DT7H8M", new XSDuration(371_280).stringValue());        
        assertEquals("P4DT9H8M20S", new XSDuration(378_500).stringValue());
        assertEquals("P5DT18H53M20S", new XSDuration(500_000).stringValue());
        assertEquals("P7D", new XSDuration(604_800).stringValue());
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

}