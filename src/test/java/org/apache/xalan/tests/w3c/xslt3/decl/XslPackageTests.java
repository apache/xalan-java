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
package org.apache.xalan.tests.w3c.xslt3.decl;

import org.apache.xalan.tests.w3c.xslt3.W3CXslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XSL 3.0 transformation 
 * tests for xsl:package instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslPackageTests extends W3CXslTransformTestsUtil {     

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	m_xslTransformTestSetFilePath = W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "decl/package/_package-test-set.xml";
    	m_resultSubFolderName = "decl";
    	
    	m_testResultFileName = "_package-test-set_result.xml";
    	
    	/**
    	 * All of the following W3C XSLT 3.0 skipped test cases, 
    	 * specify like <initial-mode name="#default" select="42"/>,
    	 * which isn't correct XSLT logic, because within XSLT 3.0, 
    	 * xsl:mode doesn't specify an attribute "select".
    	 * 
    	 * We need to seek clarification about these issues, from
    	 * W3C XSLT 3.0 WG.
    	 */
    	m_skipped_tests_list.add("package-001c");
    	m_skipped_tests_list.add("package-001d");
    	m_skipped_tests_list.add("package-001e");
    	m_skipped_tests_list.add("package-001f");
    	m_skipped_tests_list.add("package-001g");
    	m_skipped_tests_list.add("package-001h");
    	m_skipped_tests_list.add("package-001i");
    	m_skipped_tests_list.add("package-001j");
    	m_skipped_tests_list.add("package-001j-contra");
    	m_skipped_tests_list.add("package-001k");
    	m_skipped_tests_list.add("package-001l");
    	m_skipped_tests_list.add("package-001m");
    	m_skipped_tests_list.add("package-001n");
    	m_skipped_tests_list.add("package-001o");
    	m_skipped_tests_list.add("package-001p");
    	m_skipped_tests_list.add("package-001q");
    	m_skipped_tests_list.add("package-001r");
    	m_skipped_tests_list.add("package-001s");
    	m_skipped_tests_list.add("package-001t");   	
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
    }

    @Test
    public void runXslPackageTests() {    	
       runXslTestSet();
    }

}
