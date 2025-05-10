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

/**
 * A top-level JUnit 4 test suite class for Xalan-J's XSL 3 specification 
 * implementation. This JUnit tests class is an entry point, to invoke all of
 * the Xalan-J XSL 3 tests supported by this class.
 * 
 * All the XSLT and XPath tests supported by this top-level class, are
 * further split between two JUnit sub test suites  Xsl3TestSuite1 and 
 * Xsl3TestSuite2 that together form the contents of this test suite 
 * (this has been done to reduce the complexity of this XSL test suite 
 * implementation and improve this test suite's run-time performance).
 * 
 * For all the XSL tests supported by this test suite, the XSL transformation's 
 * expected output files (i.e, the gold files) are sensitive to the whitespace 
 * contents available within those files.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ Xsl3TestSuite1.class, Xsl3TestSuite2.class})
public class AllXalanXSLT3Tests {

}
