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
package org.apache.xalan.tests.w3c.xpath3.map;

import org.apache.xalan.tests.w3c.xpath3.W3CXPath3TestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XPath 3.1 test cases
 * for XPath 3.1 maps.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3MapTests extends W3CXPath3TestsUtil {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {    	    	                
        
        m_xsl_test_set_base_dir = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "map/";
    	
        m_test_set_fileArr = new String[] { "contains.xml", "get.xml", "keys.xml", "remove.xml", "for-each.xml" };
        
        m_resultSubFolderName = "map";
        
        m_test_set_result_fileArr = new String[] { "contains_result.xml", "get_result.xml", "keys_result.xml", 
                                                   "remove_result.xml", "for-each_result.xml" };
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        
        m_skipped_tests_list.clear();
    }

    @Test
    public void runXslMapTests() {
    	runXPathTestSetCollectionAndProduceResult();		
    }

}
