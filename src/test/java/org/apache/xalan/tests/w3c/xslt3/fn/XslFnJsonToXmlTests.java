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
package org.apache.xalan.tests.w3c.xslt3.fn;

import org.apache.xalan.tests.w3c.xslt3.W3CXslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XSL 3.0 transformation 
 * tests for XPath function fn:json-to-xml.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslFnJsonToXmlTests extends W3CXslTransformTestsUtil {     

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	m_xslTransformTestSetFilePath = W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "fn/json-to-xml/_json-to-xml-test-set.xml";
    	m_resultSubFolderName = "fn";
    	
    	m_testResultFileName = "_json-to-xml-test-set_result.xml";
    	
    	// Xalan-J has bugs with following, W3C XSLT 3.0 test cases.
    	// Skipping these XSL test cases for now.
    	
    	m_skipped_tests_list.add("json-to-xml-error-001");
    	m_skipped_tests_list.add("json-to-xml-error-002");
    	m_skipped_tests_list.add("json-to-xml-error-003");
    	m_skipped_tests_list.add("json-to-xml-error-004");
    	m_skipped_tests_list.add("json-to-xml-error-005");
    	m_skipped_tests_list.add("json-to-xml-error-006");
    	m_skipped_tests_list.add("json-to-xml-error-007");
    	m_skipped_tests_list.add("json-to-xml-error-008");
    	m_skipped_tests_list.add("json-to-xml-error-009");
    	m_skipped_tests_list.add("json-to-xml-error-010");
    	m_skipped_tests_list.add("json-to-xml-error-011");
    	m_skipped_tests_list.add("json-to-xml-error-012");
    	m_skipped_tests_list.add("json-to-xml-error-013");
    	m_skipped_tests_list.add("json-to-xml-error-014");
    	m_skipped_tests_list.add("json-to-xml-error-015");
    	m_skipped_tests_list.add("json-to-xml-error-016");
    	m_skipped_tests_list.add("json-to-xml-error-017");
    	m_skipped_tests_list.add("json-to-xml-error-018");
    	m_skipped_tests_list.add("json-to-xml-error-019");
    	m_skipped_tests_list.add("json-to-xml-error-020");
    	m_skipped_tests_list.add("json-to-xml-error-021");
    	m_skipped_tests_list.add("json-to-xml-error-022");
    	m_skipped_tests_list.add("json-to-xml-error-023");
    	m_skipped_tests_list.add("json-to-xml-error-024");
    	m_skipped_tests_list.add("json-to-xml-error-025");
    	m_skipped_tests_list.add("json-to-xml-error-026");
    	m_skipped_tests_list.add("json-to-xml-error-027");
    	m_skipped_tests_list.add("json-to-xml-error-028");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
    }

    @Test
    public void runXslFnJsonToXmlTests() {    	
    	runXslJsonToXmlTestSet();
    }

}
