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

import org.apache.xalan.xpath3.BuiltinFunctionsNamespceTests;
import org.apache.xalan.xpath3.DynamicFunctionCallTests;
import org.apache.xalan.xpath3.FnAvgTests;
import org.apache.xalan.xpath3.FnCodepointEqualTests;
import org.apache.xalan.xpath3.FnCodepointsToStringTests;
import org.apache.xalan.xpath3.FnCompareTests;
import org.apache.xalan.xpath3.FnContainsTokenTests;
import org.apache.xalan.xpath3.FnDistinctValuesTests;
import org.apache.xalan.xpath3.FnFilterTests;
import org.apache.xalan.xpath3.FnFoldLeftTests;
import org.apache.xalan.xpath3.FnFoldRightTests;
import org.apache.xalan.xpath3.FnForEachPairTests;
import org.apache.xalan.xpath3.FnIndexOfTests;
import org.apache.xalan.xpath3.FnMaxTests;
import org.apache.xalan.xpath3.FnMinTests;
import org.apache.xalan.xpath3.FnParseXmlFragmentTests;
import org.apache.xalan.xpath3.FnParseXmlTests;
import org.apache.xalan.xpath3.FnRoundTests;
import org.apache.xalan.xpath3.FnSortTests;
import org.apache.xalan.xpath3.FnStringJoinTests;
import org.apache.xalan.xpath3.FnStringToCodepointsTests;
import org.apache.xalan.xpath3.FnTokenizeTests;
import org.apache.xalan.xpath3.FnUnparsedTextTests;
import org.apache.xalan.xpath3.ForExprTests;
import org.apache.xalan.xpath3.IfExprTests;
import org.apache.xalan.xpath3.InstanceOfExprTests;
import org.apache.xalan.xpath3.LetExprTests;
import org.apache.xalan.xpath3.NodeComparisonTests;
import org.apache.xalan.xpath3.QuantifiedExprTests;
import org.apache.xalan.xpath3.RangeExprTests;
import org.apache.xalan.xpath3.SequenceConstructorTests;
import org.apache.xalan.xpath3.SequenceFunctionTests;
import org.apache.xalan.xpath3.SequenceTests;
import org.apache.xalan.xpath3.SimpleMapOperatorTests;
import org.apache.xalan.xpath3.StringConcatExprTests;
import org.apache.xalan.xpath3.TrignometricAndExponentialFunctionTests;
import org.apache.xalan.xpath3.W3c_xslt30_fn_deep_equalTests;
import org.apache.xalan.xpath3.XPathArithmeticOnDurationValuesTests;
import org.apache.xalan.xpath3.XsDurationComponentExtractionFunctionTests;
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
@RunWith(Suite.class)
@SuiteClasses({ XslAnalyzeStringTests.class, AttributeTests.class, GroupingTests.class,
                GroupingWithSortTests.class, RtfMigrationTests.class, QuantifiedExprTests.class, 
                FnUnparsedTextTests.class, FnTokenizeTests.class, FnStringJoinTests.class,                 
                FnIndexOfTests.class, SequenceTests.class, RangeExprTests.class, 
                W3c_xslt30_IterateTests.class, W3c_xslt30_AxesTests.class, 
                W3c_xslt30_fn_deep_equalTests.class, XslIterateTests.class,                 
                FnFilterTests.class, DynamicFunctionCallTests.class, IfExprTests.class, 
                ForExprTests.class, LetExprTests.class, FnDistinctValuesTests.class,
                TrignometricAndExponentialFunctionTests.class, BuiltinFunctionsNamespceTests.class,
                SequenceConstructorTests.class, StringConcatExprTests.class, 
                XsDurationComponentExtractionFunctionTests.class, XPathArithmeticOnDurationValuesTests.class,
                NodeComparisonTests.class, SimpleMapOperatorTests.class, FnFoldLeftTests.class,
                FnFoldRightTests.class, FnForEachPairTests.class, FnSortTests.class, FnCodepointsToStringTests.class,
                FnStringToCodepointsTests.class, FnCompareTests.class, FnCodepointEqualTests.class,
                SequenceFunctionTests.class, FnParseXmlTests.class, FnParseXmlFragmentTests.class,
                TemplateTests.class, FnAvgTests.class, FnMaxTests.class, FnMinTests.class, FnContainsTokenTests.class,
                XslVariableAttributeAsTests.class, InstanceOfExprTests.class, XslTemplateAttributeAsTests.class,
                FnRoundTests.class, XslSequenceInstTests.class })
public class Xsl3TestSuite2 {

}
