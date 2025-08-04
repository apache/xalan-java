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

import org.apache.xalan.tests.xpath3.FnAvgTests;
import org.apache.xalan.tests.xpath3.FnCodepointEqualTests;
import org.apache.xalan.tests.xpath3.FnCodepointsToStringTests;
import org.apache.xalan.tests.xpath3.FnCompareTests;
import org.apache.xalan.tests.xpath3.FnContainsTests;
import org.apache.xalan.tests.xpath3.FnContainsTokenTests;
import org.apache.xalan.tests.xpath3.FnDistinctValuesTests;
import org.apache.xalan.tests.xpath3.FnDocAvailableTests;
import org.apache.xalan.tests.xpath3.FnEmptyTests;
import org.apache.xalan.tests.xpath3.FnFilterTests;
import org.apache.xalan.tests.xpath3.FnFoldLeftTests;
import org.apache.xalan.tests.xpath3.FnFoldRightTests;
import org.apache.xalan.tests.xpath3.FnForEachPairTests;
import org.apache.xalan.tests.xpath3.FnFunctionArityTests;
import org.apache.xalan.tests.xpath3.FnFunctionNameTests;
import org.apache.xalan.tests.xpath3.FnIndexOfTests;
import org.apache.xalan.tests.xpath3.FnMaxTests;
import org.apache.xalan.tests.xpath3.FnMinTests;
import org.apache.xalan.tests.xpath3.FnNormalizeUnicodeTests;
import org.apache.xalan.tests.xpath3.FnParseXmlFragmentTests;
import org.apache.xalan.tests.xpath3.FnParseXmlTests;
import org.apache.xalan.tests.xpath3.FnRoundTests;
import org.apache.xalan.tests.xpath3.FnSortTests;
import org.apache.xalan.tests.xpath3.FnStartsWithEndsWithTests;
import org.apache.xalan.tests.xpath3.FnStringJoinTests;
import org.apache.xalan.tests.xpath3.FnStringToCodepointsTests;
import org.apache.xalan.tests.xpath3.FnSubstringBeforeAndAfterTests;
import org.apache.xalan.tests.xpath3.FnTokenizeTests;
import org.apache.xalan.tests.xpath3.FnTransformTests;
import org.apache.xalan.tests.xpath3.FnUnparsedTextLinesTests;
import org.apache.xalan.tests.xpath3.FnUnparsedTextTests;
import org.apache.xalan.tests.xpath3.XPathArithmeticOnDurationValuesTests;
import org.apache.xalan.tests.xpath3.XPathBuiltinFunctionNamespceTests;
import org.apache.xalan.tests.xpath3.XPathDefaultNamespaceMiscTests;
import org.apache.xalan.tests.xpath3.XPathDefaultNamespaceTests;
import org.apache.xalan.tests.xpath3.XPathDynamicFunctionCallMiscTests;
import org.apache.xalan.tests.xpath3.XPathDynamicFunctionCallTests;
import org.apache.xalan.tests.xpath3.XPathForExprTests;
import org.apache.xalan.tests.xpath3.XPathIdivTests;
import org.apache.xalan.tests.xpath3.XPathIfExprTests;
import org.apache.xalan.tests.xpath3.XPathInstanceOfExprTests;
import org.apache.xalan.tests.xpath3.XPathLetExprTests;
import org.apache.xalan.tests.xpath3.XPathMathFunctionTests;
import org.apache.xalan.tests.xpath3.XPathNodeComparisonTests;
import org.apache.xalan.tests.xpath3.XPathNodeSequenceCombinationTests;
import org.apache.xalan.tests.xpath3.XPathOpLiteralSequenceTests;
import org.apache.xalan.tests.xpath3.XPathQuantifiedExprTests;
import org.apache.xalan.tests.xpath3.XPathRangeExprTests;
import org.apache.xalan.tests.xpath3.XPathSequenceConstructorMiscTests;
import org.apache.xalan.tests.xpath3.XPathSequenceConstructorTests;
import org.apache.xalan.tests.xpath3.XPathSequenceFunctionTests;
import org.apache.xalan.tests.xpath3.XPathSequenceTraversalTests;
import org.apache.xalan.tests.xpath3.XPathSimpleMapOperatorTests;
import org.apache.xalan.tests.xpath3.XPathStringConcatExprTests;
import org.apache.xalan.tests.xpath3.XsDurationComponentExtractionFunctionTests;
import org.apache.xalan.tests.xpath3.XsGregorianTypeTests;
import org.apache.xalan.tests.xslt3.XslAnalyzeStringTests;
import org.apache.xalan.tests.xslt3.XslAttributeTests;
import org.apache.xalan.tests.xslt3.XslCharacterMapTests;
import org.apache.xalan.tests.xslt3.XslContextItemTests;
import org.apache.xalan.tests.xslt3.XslExpandTextTests;
import org.apache.xalan.tests.xslt3.XslForEachGroupMiscTests;
import org.apache.xalan.tests.xslt3.XslForEachGroupTests;
import org.apache.xalan.tests.xslt3.XslForEachGroupWithSortTests;
import org.apache.xalan.tests.xslt3.XslForkTests;
import org.apache.xalan.tests.xslt3.XslFunctionMiscTests;
import org.apache.xalan.tests.xslt3.XslIncludeMiscTests;
import org.apache.xalan.tests.xslt3.XslIncludeTests;
import org.apache.xalan.tests.xslt3.XslInitialTemplateTests;
import org.apache.xalan.tests.xslt3.XslIterateTests;
import org.apache.xalan.tests.xslt3.XslJsonValidationTests;
import org.apache.xalan.tests.xslt3.XslMergeTests;
import org.apache.xalan.tests.xslt3.XslMethodOutputJsonTests;
import org.apache.xalan.tests.xslt3.XslResultDocumentTests;
import org.apache.xalan.tests.xslt3.XslRtfMigrationTests;
import org.apache.xalan.tests.xslt3.XslSequenceInstTests;
import org.apache.xalan.tests.xslt3.XslSourceDocumentTests;
import org.apache.xalan.tests.xslt3.XslTemplateAttributeAsTests;
import org.apache.xalan.tests.xslt3.XslTemplateTests;
import org.apache.xalan.tests.xslt3.XslTryCatchTests;
import org.apache.xalan.tests.xslt3.XslVariableAttributeAsTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A JUnit 4 test suite for Xalan-J's, XSL 3 specification 
 * implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ XslAnalyzeStringTests.class, XslAttributeTests.class, XslForEachGroupTests.class,
                XslForEachGroupWithSortTests.class, XslRtfMigrationTests.class, XPathQuantifiedExprTests.class, 
                FnUnparsedTextTests.class, FnTokenizeTests.class, FnStringJoinTests.class,                 
                FnIndexOfTests.class, XPathSequenceTraversalTests.class, XPathRangeExprTests.class, XslIterateTests.class,                 
                FnFilterTests.class, XPathDynamicFunctionCallTests.class, XPathIfExprTests.class, 
                XPathForExprTests.class, XPathLetExprTests.class, FnDistinctValuesTests.class,
                XPathMathFunctionTests.class, XPathBuiltinFunctionNamespceTests.class,
                XPathSequenceConstructorTests.class, XPathStringConcatExprTests.class, 
                XsDurationComponentExtractionFunctionTests.class, XPathArithmeticOnDurationValuesTests.class,
                XPathNodeComparisonTests.class, XPathSimpleMapOperatorTests.class, FnFoldLeftTests.class,
                FnFoldRightTests.class, FnForEachPairTests.class, FnSortTests.class, FnCodepointsToStringTests.class,
                FnStringToCodepointsTests.class, FnCompareTests.class, FnCodepointEqualTests.class,
                XPathSequenceFunctionTests.class, FnParseXmlTests.class, FnParseXmlFragmentTests.class,
                XslTemplateTests.class, FnAvgTests.class, FnMaxTests.class, FnMinTests.class, FnContainsTokenTests.class,
                XslVariableAttributeAsTests.class, XPathInstanceOfExprTests.class, XslTemplateAttributeAsTests.class,
                FnRoundTests.class, XslSequenceInstTests.class, XPathNodeSequenceCombinationTests.class,
                FnDocAvailableTests.class, FnUnparsedTextLinesTests.class, XslMethodOutputJsonTests.class,
                XsGregorianTypeTests.class, XslMergeTests.class, XslSourceDocumentTests.class,
                XslForkTests.class, XslResultDocumentTests.class, XPathIdivTests.class,
                FnTransformTests.class, FnStartsWithEndsWithTests.class, FnContainsTests.class,
                FnSubstringBeforeAndAfterTests.class, XslTryCatchTests.class, XslCharacterMapTests.class,
                XslInitialTemplateTests.class, XslContextItemTests.class, XPathOpLiteralSequenceTests.class,
                XPathSequenceConstructorMiscTests.class, XslForEachGroupMiscTests.class, XPathDynamicFunctionCallMiscTests.class,
                FnEmptyTests.class, FnFunctionNameTests.class, FnFunctionArityTests.class, XPathDefaultNamespaceTests.class,
                XPathDefaultNamespaceMiscTests.class, XslFunctionMiscTests.class, XslIncludeTests.class, 
                XslIncludeMiscTests.class, FnNormalizeUnicodeTests.class, XslExpandTextTests.class,
                XslJsonValidationTests.class })
public class XalanXsl3TestSuite2 {

}
