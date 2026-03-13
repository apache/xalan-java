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

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xml.xpath31.processor.types.XPath3DecimalFormat;

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
public class XPath3DecimalFormatTest {

    private static final Locale initialLocale = Locale.getDefault();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // the test data assumes that english is the default locale
        Locale.setDefault(Locale.ENGLISH);
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	// reset the locale
        Locale.setDefault(initialLocale);
	}

    @Test
    public void performStrFormatting() {
        assertEquals("1234567.89", new XPath3DecimalFormat("#.##").performStrFormatting(Double.valueOf(1234567.89)));   // double
        assertEquals("189.5", new XPath3DecimalFormat("#.##").performStrFormatting(Float.valueOf(189.5f)));             // float
        assertEquals("189", new XPath3DecimalFormat("#.##").performStrFormatting(Integer.valueOf(189)));                // int
        assertEquals("189", new XPath3DecimalFormat("#.##").performStrFormatting(Long.valueOf(189L)));                  // long
        
        assertEquals("1,234,567.89", new XPath3DecimalFormat("0,000.00").performStrFormatting(Double.valueOf(1234567.89)));     // double
        assertEquals("0,189.50", new XPath3DecimalFormat("0,000.00").performStrFormatting(Float.valueOf(189.5f)));              // float
        assertEquals("0,189.00", new XPath3DecimalFormat("0,000.00").performStrFormatting(Integer.valueOf(189)));               // int
        assertEquals("0,189.00", new XPath3DecimalFormat("0,000.00").performStrFormatting(Long.valueOf(189L)));                 // long
        
        assertEquals("1,234,567.89", new XPath3DecimalFormat("#,###.##").performStrFormatting(Double.valueOf(1234567.89)));     // double
        assertEquals("9,364.5", new XPath3DecimalFormat("#,###.##").performStrFormatting(Float.valueOf(9_364.5f)));             // float
        assertEquals("9,364", new XPath3DecimalFormat("#,###.##").performStrFormatting(Integer.valueOf(9_364)));                // int
        assertEquals("9,364", new XPath3DecimalFormat("#,###.##").performStrFormatting(Long.valueOf(9_364L)));                  // long
        
        // Scientific Notation
        assertEquals("1.23456789E6", new XPath3DecimalFormat("###.000000E0").performStrFormatting(Double.valueOf(1.23456789E6)));
    }
}