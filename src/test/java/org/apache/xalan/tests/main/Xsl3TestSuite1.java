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

import org.apache.xalan.tests.xpath3.FnAbsTests;
import org.apache.xalan.tests.xpath3.FnAnalyzeStringTests;
import org.apache.xalan.tests.xpath3.FnApplyTests;
import org.apache.xalan.tests.xpath3.FnBaseUriTests;
import org.apache.xalan.tests.xpath3.FnDataTests;
import org.apache.xalan.tests.xpath3.FnDateTimeTests;
import org.apache.xalan.tests.xpath3.FnDeepEqualTests;
import org.apache.xalan.tests.xpath3.FnDefaultCollation;
import org.apache.xalan.tests.xpath3.FnDocTests;
import org.apache.xalan.tests.xpath3.FnDocumentUriTests;
import org.apache.xalan.tests.xpath3.FnForEachTests;
import org.apache.xalan.tests.xpath3.FnJsonDocTests;
import org.apache.xalan.tests.xpath3.FnJsonToXmlTests;
import org.apache.xalan.tests.xpath3.FnLangTests;
import org.apache.xalan.tests.xpath3.FnMapFindTests;
import org.apache.xalan.tests.xpath3.FnMapMerge;
import org.apache.xalan.tests.xpath3.FnParseJsonTests;
import org.apache.xalan.tests.xpath3.FnPositionTests;
import org.apache.xalan.tests.xpath3.FnQNameTests;
import org.apache.xalan.tests.xpath3.FnResolveQNameTests;
import org.apache.xalan.tests.xpath3.FnRootTests;
import org.apache.xalan.tests.xpath3.FnXmlToJsonTests;
import org.apache.xalan.tests.xpath3.FuncBooleanValuesTests;
import org.apache.xalan.tests.xpath3.XPathArrayTests;
import org.apache.xalan.tests.xpath3.XPathArrowOpTests;
import org.apache.xalan.tests.xpath3.XPathCastTests;
import org.apache.xalan.tests.xpath3.XPathExprFunctionCallSuffixTests;
import org.apache.xalan.tests.xpath3.XPathGeneralComparisonOpTests;
import org.apache.xalan.tests.xpath3.XPathInlineFunctionExprTests;
import org.apache.xalan.tests.xpath3.XPathMapOtherTests;
import org.apache.xalan.tests.xpath3.XPathMapTests;
import org.apache.xalan.tests.xpath3.XPathNamedFunctionReferenceTests;
import org.apache.xalan.tests.xpath3.XPathNumericArithmeticTests;
import org.apache.xalan.tests.xpath3.XPathStringTests;
import org.apache.xalan.tests.xpath3.XPathValueComparisonTests;
import org.apache.xalan.tests.xpath3.XsConstructorFunctionTests;
import org.apache.xalan.tests.xpath3.XsDateTimeArithmeticTests;
import org.apache.xalan.tests.xpath3.XsDateTimeTests;
import org.apache.xalan.tests.xpath3.XsDurationSubtypes;
import org.apache.xalan.tests.xpath3.XsTimeWithArithmeticTests;
import org.apache.xalan.tests.xslt3.XalanJiraIssueTests;
import org.apache.xalan.tests.xslt3.XmlDotComXslHigherOrderFunctionTests;
import org.apache.xalan.tests.xslt3.XslAttributeValidationTests;
import org.apache.xalan.tests.xslt3.XslCopyValidationTests;
import org.apache.xalan.tests.xslt3.XslCopyofValidationTests;
import org.apache.xalan.tests.xslt3.XslElementValidationTests;
import org.apache.xalan.tests.xslt3.XslEvaluateTests;
import org.apache.xalan.tests.xslt3.XslForEachGroupWithCollationTests;
import org.apache.xalan.tests.xslt3.XslFunctionTests;
import org.apache.xalan.tests.xslt3.XslHigherOrderFunctionTests;
import org.apache.xalan.tests.xslt3.XslImportSchemaTests;
import org.apache.xalan.tests.xslt3.XslLiteralResultElementValidationTests;
import org.apache.xalan.tests.xslt3.XslRecursiveFunctionTests;
import org.apache.xalan.tests.xslt3.XslTunnelParameterTests;
import org.apache.xalan.tests.xslt3.XslValueofTests;
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
@SuiteClasses({ FnDocTests.class, FnDataTests.class, XslRecursiveFunctionTests.class,
                XslFunctionTests.class, XslHigherOrderFunctionTests.class, XsDateTimeTests.class,
                XPathValueComparisonTests.class, XPathInlineFunctionExprTests.class, 
                FnForEachTests.class, XsConstructorFunctionTests.class,
                FnAbsTests.class, XPathStringTests.class, XsDateTimeArithmeticTests.class,
                XsTimeWithArithmeticTests.class, XsDurationSubtypes.class,
                FnDeepEqualTests.class, XslImportSchemaTests.class, FnDateTimeTests.class,
                XmlDotComXslHigherOrderFunctionTests.class, FnDefaultCollation.class,
                FnBaseUriTests.class, FnDocumentUriTests.class, FnResolveQNameTests.class,
                FnQNameTests.class, XPathMapTests.class, XPathArrayTests.class, FnLangTests.class, 
                FnRootTests.class, XPathCastTests.class, XPathArrowOpTests.class, FnMapMerge.class,
                FnParseJsonTests.class, FnJsonDocTests.class, FnJsonToXmlTests.class,
                FnXmlToJsonTests.class, FnAnalyzeStringTests.class, XPathMapOtherTests.class,
                XalanJiraIssueTests.class, XPathNamedFunctionReferenceTests.class, FnApplyTests.class,
                XPathGeneralComparisonOpTests.class, FnMapFindTests.class, XPathNumericArithmeticTests.class,
                XslTunnelParameterTests.class, FuncBooleanValuesTests.class, XslElementValidationTests.class,
                XslLiteralResultElementValidationTests.class, XslAttributeValidationTests.class,
                XPathExprFunctionCallSuffixTests.class, XslEvaluateTests.class, XslValueofTests.class,
                XslCopyofValidationTests.class, XslCopyValidationTests.class, FnPositionTests.class,
                XslForEachGroupWithCollationTests.class })
public class Xsl3TestSuite1 {

}
