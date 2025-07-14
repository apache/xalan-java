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
package org.apache.xalan.tests.w3c.xslt3.insn;

import org.apache.xalan.tests.w3c.xslt3.W3CXslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XSL 3.0 transformation 
 * tests for xsl:apply-templates instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslApplyTemplatesTests extends W3CXslTransformTestsUtil {     

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	m_xslTransformTestSetFilePath = W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "insn/apply-templates/_apply-templates-test-set.xml";
    	m_resultSubFolderName = "insn";
    	m_testResultFileName = "_apply-templates-test-set_result.xml";
    	
    	// XSLT20+ test using mode = '#current'. Not yet implemented with Xalan-J.
    	m_skipped_tests_list.add("conflict-resolution-0801");
    	// XSLT20+ test using mode = '#default'. Not yet implemented with Xalan-J.
    	m_skipped_tests_list.add("conflict-resolution-0802");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
    }
    
    @Test
    public void runXslApplyTemplateTests() {    	
       runXslTestSet();
    }

}
