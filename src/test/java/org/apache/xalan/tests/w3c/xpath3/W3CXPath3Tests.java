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

import org.apache.xalan.tests.w3c.xpath3.prod.XPath3ArrowPostfixTests;
import org.apache.xalan.tests.w3c.xpath3.prod.XPath3AxisStepAbbrTests;
import org.apache.xalan.tests.w3c.xpath3.prod.XPath3AxisStepTests;
import org.apache.xalan.tests.w3c.xpath3.prod.XPath3CastableExprTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Xalan-J XSL 3 test driver entry point, to run W3C 
 * XPath 3.1 test cases.
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ XPath3ArrowPostfixTests.class, XPath3CastableExprTests.class, XPath3AxisStepTests.class, XPath3AxisStepAbbrTests.class })
public class W3CXPath3Tests {

}
