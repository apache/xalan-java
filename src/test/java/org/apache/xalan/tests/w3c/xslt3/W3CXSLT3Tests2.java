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

import org.apache.xalan.tests.w3c.xslt3.attr.XslDisableOutputEscapingAttrTests;
import org.apache.xalan.tests.w3c.xslt3.attr.XslExpandTextAttrTests;
import org.apache.xalan.tests.w3c.xslt3.attr.XslModeTests;
import org.apache.xalan.tests.w3c.xslt3.attr.XslTunnelAttrTests;
import org.apache.xalan.tests.w3c.xslt3.attr.XslXPathDefaultNamespaceAttrTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslCharacterMapTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslPackageTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslStripSpaceTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslTemplateTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslAxesTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslCastableTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslExpressionTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslNodeTestTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslPathTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslPredicateTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnDeepEqualTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnFormatNumberTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnJsonToXmlTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnKeyTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnTypeAvailableTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnXmlToJsonTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslAttributeTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslConstructNodeTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslCopyTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslElementTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslMessageTests;
import org.apache.xalan.tests.w3c.xslt3.misc.XslBuiltInTemplateTests;
import org.apache.xalan.tests.w3c.xslt3.misc.XslRegexSyntaxTests;
import org.apache.xalan.tests.w3c.xslt3.misc.XslRegexTests;
import org.apache.xalan.tests.w3c.xslt3.misc.XslWhitespaceTests;
import org.apache.xalan.tests.w3c.xslt3.type.XslNodeTests;
import org.apache.xalan.tests.w3c.xslt3.type.XslStringTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Xalan-J XSL 3 test driver entry point, to run W3C 
 * XSLT 3.0 transformation tests.
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ XslCopyTests.class, XslElementTests.class, 	             
	            XslAxesTests.class, XslCastableTests.class, XslExpressionTests.class, 
	            XslNodeTestTests.class, XslPathTests.class, XslPredicateTests.class, XslAttributeTests.class, XslCharacterMapTests.class,
	            XslNodeTests.class, XslStringTests.class, XslFnDeepEqualTests.class, 
	            XslFnFormatNumberTests.class, XslRegexTests.class, XslRegexSyntaxTests.class,
	            XslXPathDefaultNamespaceAttrTests.class, XslExpandTextAttrTests.class, XslFnJsonToXmlTests.class,
	            XslBuiltInTemplateTests.class, XslModeTests.class, XslDisableOutputEscapingAttrTests.class, XslTunnelAttrTests.class,
	            XslStripSpaceTests.class, XslTemplateTests.class, XslFnXmlToJsonTests.class, XslMessageTests.class,
	            XslConstructNodeTests.class, XslWhitespaceTests.class, XslPackageTests.class, XslFnKeyTests.class,
	            XslFnTypeAvailableTests.class })
public class W3CXSLT3Tests2 {

}
