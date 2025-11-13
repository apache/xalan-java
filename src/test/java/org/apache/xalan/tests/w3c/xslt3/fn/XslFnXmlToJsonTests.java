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
 * tests for XPath function fn:xml-to-json.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslFnXmlToJsonTests extends W3CXslTransformTestsUtil {     

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	m_xslTransformTestSetFilePath = W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "fn/xml-to-json/_xml-to-json-test-set.xml";
    	m_resultSubFolderName = "fn";
    	
    	m_testResultFileName = "_xml-to-json-test-set_result.xml";
    	
    	// Xalan-J has bugs with following, W3C XSLT 3.0 test cases.
    	// Skipping these XSL test cases for now.
    	
    	m_skipped_tests_list.add("xml-to-json-C001");
    	m_skipped_tests_list.add("xml-to-json-C002");
    	m_skipped_tests_list.add("xml-to-json-C003");
    	m_skipped_tests_list.add("xml-to-json-C004");
    	m_skipped_tests_list.add("xml-to-json-C005");
    	m_skipped_tests_list.add("xml-to-json-C006");
    	m_skipped_tests_list.add("xml-to-json-C007");
    	m_skipped_tests_list.add("xml-to-json-C008");
    	m_skipped_tests_list.add("xml-to-json-C009");
    	m_skipped_tests_list.add("xml-to-json-C010");
    	m_skipped_tests_list.add("xml-to-json-C011");
    	m_skipped_tests_list.add("xml-to-json-C012");
    	m_skipped_tests_list.add("xml-to-json-C013");
    	m_skipped_tests_list.add("xml-to-json-C014");
    	m_skipped_tests_list.add("xml-to-json-C015");
    	m_skipped_tests_list.add("xml-to-json-C016");
    	m_skipped_tests_list.add("xml-to-json-C017");
    	m_skipped_tests_list.add("xml-to-json-C018");
    	m_skipped_tests_list.add("xml-to-json-C019");
    	m_skipped_tests_list.add("xml-to-json-C020");
    	m_skipped_tests_list.add("xml-to-json-C021");
    	m_skipped_tests_list.add("xml-to-json-C022");
    	m_skipped_tests_list.add("xml-to-json-C023");
    	m_skipped_tests_list.add("xml-to-json-C024");
    	m_skipped_tests_list.add("xml-to-json-C100");
    	m_skipped_tests_list.add("xml-to-json-C101");
    	m_skipped_tests_list.add("xml-to-json-C102");
    	m_skipped_tests_list.add("xml-to-json-C103");
    	m_skipped_tests_list.add("xml-to-json-C104");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
    }

    @Test
    public void runXslFnXmlToJsonTests() {    	
    	runXslJsonToXmlTestSet();
    }

}
