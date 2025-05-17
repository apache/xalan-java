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

import org.apache.xalan.tests.w3c.xslt3.decl.XslCharacterMapTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslFunctionTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslAxesTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslCastableTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslExpressionTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslForTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslMathTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslNodeTestTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslPathTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslPredicateTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslAnalyzeStringTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslApplyTemplatesTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslAttributeTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslCallTemplateTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslChooseTemplateTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslCopyTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslForEachGroupTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslIterateTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslSequenceTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslTryTests;
import org.apache.xalan.tests.w3c.xslt3.type.XslBooleanTests;
import org.apache.xalan.tests.w3c.xslt3.type.XslNodeTests;
import org.apache.xalan.tests.w3c.xslt3.type.XslStringTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Xalan-J XSL 3 test driver entry point, to run W3C XSL 3.0 
 * transformation tests.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ XslAnalyzeStringTests.class, XslApplyTemplatesTests.class, XslForEachGroupTests.class,
	            XslTryTests.class, XslAttributeTests.class, XslCallTemplateTests.class,
	            XslChooseTemplateTests.class, XslCopyTests.class, XslIterateTests.class, XslSequenceTests.class, 
	            XslFunctionTests.class, XslCharacterMapTests.class, XslAxesTests.class, 
	            XslCastableTests.class, XslExpressionTests.class, XslForTests.class, XslMathTests.class, 
	            XslNodeTestTests.class, XslPathTests.class, XslPredicateTests.class, XslBooleanTests.class, 
	            XslNodeTests.class, XslStringTests.class })
public class W3CXSLT3Tests {

}
