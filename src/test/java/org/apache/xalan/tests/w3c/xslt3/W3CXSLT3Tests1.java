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

import org.apache.xalan.tests.w3c.xslt3.attr.XslAsAttrTests;
import org.apache.xalan.tests.w3c.xslt3.attr.XslAvtAttrTests;
import org.apache.xalan.tests.w3c.xslt3.attr.XslMatchAttrTests;
import org.apache.xalan.tests.w3c.xslt3.attr.XslSelectAttrTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslAttributeSetTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslContextItemTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslFunctionTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslGlobalContextItemTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslParamTests;
import org.apache.xalan.tests.w3c.xslt3.decl.XslVariableTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslForTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslHigherOrderFunctionTests;
import org.apache.xalan.tests.w3c.xslt3.expr.XslMathTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnCopyOfTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnPositionTests;
import org.apache.xalan.tests.w3c.xslt3.fn.XslFnRootTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslAnalyzeStringTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslApplyTemplatesTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslCallTemplateTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslChooseTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslDocumentTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslIterateTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslSequenceTests;
import org.apache.xalan.tests.w3c.xslt3.insn.XslSortTests;
import org.apache.xalan.tests.w3c.xslt3.type.XslDateTests;
import org.apache.xalan.tests.w3c.xslt3.type.XslMapsTests;
import org.apache.xalan.tests.w3c.xslt3.type.XslNamespaceTests;
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
@SuiteClasses({ XslFnCopyOfTests.class, XslSelectAttrTests.class, XslHigherOrderFunctionTests.class, 
	            XslNamespaceTests.class, XslMapsTests.class, XslAttributeSetTests.class, 
	            XslSortTests.class, XslMathTests.class, XslFunctionTests.class, XslContextItemTests.class,
	            XslDocumentTests.class, XslGlobalContextItemTests.class, XslParamTests.class, XslCallTemplateTests.class, XslVariableTests.class, 
	            XslAsAttrTests.class, XslAvtAttrTests.class, XslChooseTests.class, XslForTests.class,
	            XslDateTests.class, XslFnPositionTests.class, XslSequenceTests.class, XslMatchAttrTests.class, XslApplyTemplatesTests.class,	            
	            XslIterateTests.class, XslAnalyzeStringTests.class, XslFnRootTests.class })
public class W3CXSLT3Tests1 {

}
