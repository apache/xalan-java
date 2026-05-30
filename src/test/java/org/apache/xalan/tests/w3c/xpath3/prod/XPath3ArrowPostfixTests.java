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
package org.apache.xalan.tests.w3c.xpath3.prod;

import org.apache.xalan.tests.w3c.xpath3.W3CXPath3TestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XPath 3.1 test cases
 * for XPath 3.1 operator arrow postfix.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3ArrowPostfixTests extends W3CXPath3TestsUtil { 

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {    	    	
    	m_xslTransformTestSetFilePath = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "prod/ArrowPostfix.xml";
        m_resultSubFolderName = "prod";
    	
    	m_testResultFileName = "arrow_postfix_result.xml";
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        
        m_skipped_tests_list.clear();
    }

    @Test
    public void runXslArrowPostfixTests() {
    	runXPathTestSetAndProduceResult();		
    }

}
