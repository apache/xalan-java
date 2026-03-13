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
package org.apache.xalan.tests.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import xml.xpath31.processor.types.tests.XPath3DecimalFormatTest;
import xml.xpath31.processor.types.tests.XSAnyURITest;
import xml.xpath31.processor.types.tests.XSDateTest;
import xml.xpath31.processor.types.tests.XSDateTimeTest;
import xml.xpath31.processor.types.tests.XSDayTimeDurationTest;
import xml.xpath31.processor.types.tests.XSDecimalTest;
import xml.xpath31.processor.types.tests.XSDoubleTest;
import xml.xpath31.processor.types.tests.XSDurationTest;
import xml.xpath31.processor.types.tests.XSNumericTypeTest;
import xml.xpath31.processor.types.tests.XSTimeTest;

/**
 * A JUnit 4 test suite for Xalan-J's, XSL 3 specification 
 * implementation.
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ XSAnyURITest.class, XSDateTest.class, XSDateTimeTest.class,
	            XSDecimalTest.class, XSDoubleTest.class, XSNumericTypeTest.class,
	            XSTimeTest.class, XPath3DecimalFormatTest.class, XSDayTimeDurationTest.class,
	            XSDurationTest.class } )
public class XalanXPath3TypesTestSuite {

}
