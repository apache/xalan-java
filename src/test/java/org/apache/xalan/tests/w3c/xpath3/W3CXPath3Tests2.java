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
package org.apache.xalan.tests.w3c.xpath3;

import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnContainsTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnContainsTokenTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnFunction1Tests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnFunction2Tests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnFunction3Tests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnFunction4Tests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnLowerCaseTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnMatchesTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnReplaceTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnStringJoinTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnTailTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnTokenizeTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnUpperCaseTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FnXmlInscopePrefixesTests;
import org.apache.xalan.tests.w3c.xpath3.fn.XPath3FunctionStringTests;
import org.apache.xalan.tests.w3c.xpath3.map.XPath3MapTests;
import org.apache.xalan.tests.w3c.xpath3.math.XPath3FunctionMathTests;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3IsSameNodeTests;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3OpTestsFive;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3OpTestsFour;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3OpTestsOne;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3OpTestsSix;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3OpTestsThreePart1;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3OpTestsThreePart2;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3OpTestsTwo;
import org.apache.xalan.tests.w3c.xpath3.op.XPath3QnameEqualTests;
import org.apache.xalan.tests.w3c.xpath3.prod.XPath3ForClauseTests;
import org.apache.xalan.tests.w3c.xpath3.prod.XPath3IfExprTests;
import org.apache.xalan.tests.w3c.xpath3.prod.XPath3LetClauseTests;
import org.apache.xalan.tests.w3c.xpath3.prod.XPath3OrExprTests;
import org.apache.xalan.tests.w3c.xpath3.prod.XPath3QuantifiedExprTests;
import org.apache.xalan.tests.w3c.xpath3.xs.XPath3XsType1Tests;
import org.apache.xalan.tests.w3c.xpath3.xs.XPath3XsType2Tests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Xalan-J XSL 3 test driver entry point, to run W3C 
 * XPath 3.1 test cases (the second set of XPath 3.1 test cases).
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ XPath3IfExprTests.class, XPath3FunctionMathTests.class, XPath3FnTailTests.class, XPath3QuantifiedExprTests.class, XPath3ForClauseTests.class, 
	            XPath3LetClauseTests.class, XPath3MapTests.class, XPath3XsType1Tests.class, XPath3OpTestsOne.class, XPath3OpTestsTwo.class, 
	            XPath3OpTestsThreePart1.class, XPath3OpTestsThreePart2.class, XPath3OpTestsFour.class, XPath3IsSameNodeTests.class, XPath3OpTestsFive.class, 
	            XPath3OpTestsSix.class, XPath3FnFunction1Tests.class, XPath3FnFunction2Tests.class, XPath3FnFunction3Tests.class, XPath3FnFunction4Tests.class,
	            XPath3OrExprTests.class, XPath3FnMatchesTests.class, XPath3FnReplaceTests.class, XPath3FnTokenizeTests.class, XPath3QnameEqualTests.class,
	            XPath3FnContainsTests.class, XPath3FnContainsTokenTests.class, XPath3FnXmlInscopePrefixesTests.class,
	            XPath3FnLowerCaseTests.class, XPath3FnUpperCaseTests.class, XPath3FunctionStringTests.class, XPath3FnStringJoinTests.class,
	            XPath3XsType2Tests.class })
public class W3CXPath3Tests2 {

}
