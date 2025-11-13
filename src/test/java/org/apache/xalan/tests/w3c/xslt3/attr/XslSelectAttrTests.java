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
package org.apache.xalan.tests.w3c.xslt3.attr;

import org.apache.xalan.tests.w3c.xslt3.W3CXslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XSL 3.0 transformation 
 * tests for XSLT 'select' attribute.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslSelectAttrTests extends W3CXslTransformTestsUtil {     

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	m_xslTransformTestSetFilePath = W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "attr/select/_select-test-set.xml";
    	m_resultSubFolderName = "attr";
    	
    	m_testResultFileName = "_select-test-set_result.xml";
    	
    	// Xalan-J has bugs with following, W3C XSLT 3.0 test cases.
    	// Skipping these XSL test cases for now.
    	
    	m_skipped_tests_list.add("select-1701");    	
    	m_skipped_tests_list.add("select-1702");
    	m_skipped_tests_list.add("select-1705");
    	m_skipped_tests_list.add("select-2005");
    	m_skipped_tests_list.add("select-2006");
    	m_skipped_tests_list.add("select-2007");
    	m_skipped_tests_list.add("select-2013");
    	m_skipped_tests_list.add("select-2014");
    	m_skipped_tests_list.add("select-2025");
    	m_skipped_tests_list.add("select-2026");
    	m_skipped_tests_list.add("select-2028");
    	m_skipped_tests_list.add("select-2029");
    	m_skipped_tests_list.add("select-2030");
    	m_skipped_tests_list.add("select-2031");
    	m_skipped_tests_list.add("select-2032");
    	m_skipped_tests_list.add("select-2033");
    	m_skipped_tests_list.add("select-2034");
    	m_skipped_tests_list.add("select-2035");
    	m_skipped_tests_list.add("select-2102");
    	m_skipped_tests_list.add("select-2201");
    	m_skipped_tests_list.add("select-2202");
    	m_skipped_tests_list.add("select-2203");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
    }

    @Test
    public void runXslSelectAttrTests() {    	
       runXslTestSet();
    }

}
