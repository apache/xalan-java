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
package org.apache.xalan.tests.w3c.xpath3.math;

import org.apache.xalan.tests.w3c.xpath3.W3CXPath3TestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XPath 3.1 test cases
 * for XPath 3.1 math functions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3FunctionMathTests extends W3CXPath3TestsUtil {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	
    	m_xsl_test_set_base_dir = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "map/";
    	
        m_test_set_fileArr = new String[] { "math-acos.xml", "math-asin.xml", "math-atan.xml", "math-atan2.xml", "math-cos.xml", 
        		                            "math-exp.xml", "math-exp10.xml", "math-log.xml", "math-log10.xml", "math-pi.xml", 
        		                            "math-pow.xml", "math-sin.xml", "math-sqrt.xml", "math-tan.xml" };
        
        m_resultSubFolderName = "math";
        
        m_test_set_result_fileArr = new String[] { "math-acos_result.xml", "math-asin_result.xml", "math-atan_result.xml", "math-atan2_result.xml", 
        		                                   "math-cos_result.xml", "math-exp_result.xml", "math-exp10_result.xml", "math-log_result.xml",
        		                                   "math-log10_result.xml", "math-pi_result.xml", "math-pow_result.xml", "math-sin_result.xml",
        		                                   "math-sqrt_result.xml", "math-tan_result.xml" };
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        
        m_skipped_tests_list.clear();
    }

    @Test
    public void runXslFunctionMathTests() {
    	runXPathTestSetCollectionAndProduceResult();		
    }

}
