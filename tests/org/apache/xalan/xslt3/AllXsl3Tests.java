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
package org.apache.xalan.xslt3;

import org.apache.xalan.xpath3.FnAbsTests;
import org.apache.xalan.xpath3.FnIndexOfTests;
import org.apache.xalan.xpath3.FnStringJoinTests;
import org.apache.xalan.xpath3.FnTokenizeTests;
import org.apache.xalan.xpath3.FnUnparsedTextTests;
import org.apache.xalan.xpath3.RangeExprTests;
import org.apache.xalan.xpath3.SequenceTests;
import org.apache.xalan.xpath3.StringTests;
import org.apache.xalan.xpath3.XsConstructorFunctions;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A JUnit 4 test suite, for XSLT 3.0 and XPath 3.1 tests.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 *  For all the XSLT and XPath tests supported by this test suite, 
 *  the XSLT transformation expected output files (i.e, the gold files) are 
 *  sensitive to the whitespace contents available within those files.
 */
@RunWith(Suite.class)
@SuiteClasses({ AnalyzeStringTests.class, AttributeTests.class, GroupingTests.class,
                GroupingWithSortTests.class, RtfMigrationTests.class, QuantifiedExprTests.class, 
                FnUnparsedTextTests.class, FnTokenizeTests.class, FnStringJoinTests.class,
                FnAbsTests.class, StringTests.class, XsConstructorFunctions.class, 
                FnIndexOfTests.class, SequenceTests.class, RangeExprTests.class, 
                W3c_xslt30_IterateTests.class, W3c_xslt30_AxesTests.class, XslIterateTests.class })
public class AllXsl3Tests {

}
