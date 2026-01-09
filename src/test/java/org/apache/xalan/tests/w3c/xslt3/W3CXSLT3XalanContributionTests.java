/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.tests.w3c.xslt3;

import org.apache.xalan.tests.w3c.xslt3.xalan.fn.XslFnAbsTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.fn.XslFnAdjustDateToTimezoneTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.fn.XslFnAdjustDatetimeToTimezoneTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.fn.XslFnAdjustTimeToTimezoneTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.fn.XslFnAnalyzeStringTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.fn.XslFnApplyTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.fn.XslFnJsonToXmlTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.insn.XslIterateTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.insn.XslPerformSortTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.xpath_array.XslArrayTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.xpath_map.XslMapTests;
import org.apache.xalan.tests.w3c.xslt3.xalan.xpath_op.XslNodeComparisonTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Apache Xalan XSL 3 test driver entry point, to run Xalan 
 * W3C XSLT 3.0 transformation tests.
 * 
 * Please note: XSL test cases comprising within this class, have been 
 * submitted to, W3C XSLT 3.0 test suite's repos and are currently being 
 * reviewed by XSLT 3.0 recommendation team.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ XslFnAbsTests.class, XslFnAdjustDateToTimezoneTests.class, XslFnAdjustDatetimeToTimezoneTests.class,
	            XslFnAdjustTimeToTimezoneTests.class, XslFnAnalyzeStringTests.class, XslNodeComparisonTests.class,
	            XslPerformSortTests.class, XslFnApplyTests.class, XslFnJsonToXmlTests.class, XslIterateTests.class,
	            XslMapTests.class, XslArrayTests.class })
public class W3CXSLT3XalanContributionTests {

}
