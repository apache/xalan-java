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