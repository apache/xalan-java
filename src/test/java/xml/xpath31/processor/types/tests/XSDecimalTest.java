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

import static org.apache.xpath.objects.XObject.CLASS_XS_DECIMAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.apache.xpath.objects.ResultSequence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XSDecimal;

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
public class XSDecimalTest {
	
	@BeforeClass
    public static void setUpBeforeClass() throws Exception {
		// no op
	}
	
	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		// no op
	}

    @Test
    public void stringType() {
        assertEquals("xs:decimal", new XSDecimal().stringType());
    }

    @Test
    public void typeName() {
        assertEquals("decimal", new XSDecimal().typeName());
    }

    @Test
    public void stringValue() {
        new XSDecimal("1.3").stringValue();
    }

    @Test
    public void constructor() {
        final ResultSequence input = new ResultSequence();
        input.add(new XSDecimal("1.3"));

        final ResultSequence sequence = new XSDecimal().constructor(input);

        assertNotNull(sequence);
        assertEquals(1, sequence.size());
        assertEquals("1.3", ((XSDecimal)sequence.item(0)).stringValue());
    }

    @Test
    public void zero() {
        assertFalse(new XSDecimal("1.3").zero());
    }

    @Test
    public void doubleValue() {
        assertEquals(1.3, new XSDecimal("1.3").doubleValue(), 0);
    }

    @Test
    public void getValue() {
        assertEquals(BigDecimal.valueOf(1.3), new XSDecimal("1.3").getValue());
    }

    @Test
    public void testEquals() {
        assertTrue(new XSDecimal("1.3").equals(new XSDecimal("1.3")));
        assertTrue(new XSDecimal("1337.890625").equals(new XSDecimal("1337.890625")));

        assertFalse(new XSDecimal("0.1").equals(new XSDecimal("0.11111111111")));
        assertFalse(new XSDecimal("1.5").equals(new XSDecimal("15")));
        assertFalse(new XSDecimal("8.6").equals(new XSDecimal("65646.874")));
        assertFalse(new XSDecimal("256.684").equals(new XSDecimal("68.96864")));
    }

    @Test
    public void lt() {
        assertTrue(new XSDecimal("1.3").lt(new XSDecimal("2")));
        assertTrue(new XSDecimal("1.3").lt(new XSDecimal("1.4")));
        assertTrue(new XSDecimal("1.3").lt(new XSDecimal("1.3000000000000000000001")));
    }

    @Test
    public void gt() {
        assertTrue(new XSDecimal("1.3").gt(new XSDecimal("1")));
        assertTrue(new XSDecimal("1.3").gt(new XSDecimal("1.2")));
        assertTrue(new XSDecimal("1.3").gt(new XSDecimal("1.2999999999999999")));
    }

    @Test
    public void getType() {
        assertEquals(CLASS_XS_DECIMAL, new XSDecimal().getType());
    }
}