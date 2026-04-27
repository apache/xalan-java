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
package org.apache.xalan.tests.w3c.xslt3.type;

import org.apache.xalan.tests.w3c.xslt3.W3CXslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XSL 3.0 transformation 
 * tests for XPath maps types.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslMapsTests extends W3CXslTransformTestsUtil {     

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	m_xslTransformTestSetFilePath = W3C_XSLT3_TESTS_META_DATA_DIR_HOME + "type/maps/_maps-test-set.xml";
    	m_resultSubFolderName = "type";
    	
    	m_testResultFileName = "_maps-test-set_result.xml";
    	
    	// Xalan xslt3.0 implementation goes to INF loop, with this test case.
    	m_skipped_tests_list.add("maps-011");
    	
    	// The following XSLT 3.0 test cases, use an XML namespace http://www.w3.org/2011/xpath-functions/map
    	// from earlier XSL 3 specification drafts. Skipping these XSL test cases for now.
    	m_skipped_tests_list.add("maps-906a");
    	m_skipped_tests_list.add("maps-906b");
    	m_skipped_tests_list.add("maps-906c");
    	m_skipped_tests_list.add("maps-906d");
    	m_skipped_tests_list.add("maps-906e");
    	m_skipped_tests_list.add("maps-906f");
    	m_skipped_tests_list.add("maps-906g");
    	m_skipped_tests_list.add("maps-906h");
    	m_skipped_tests_list.add("maps-906i");
    	m_skipped_tests_list.add("maps-906j");
    	m_skipped_tests_list.add("maps-906k");
    	m_skipped_tests_list.add("maps-906l");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
    }

    @Test
    public void runXslMapsTests() {    	
       runXslTestSet();
    }

}
