/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.tests.xpath3;

import org.apache.xalan.tests.util.XSLTestConstants;
import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XPath 3.1 function fn:collection test cases.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FnCollectionTests extends XslTransformTestsUtil {        
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "fn_collection/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "fn_collection/gold/";        

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {    	
    	m_xsl_test_dir_base_uri = "file:/" + XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "fn_collection/";
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {        
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
        
        m_xsl_test_dir_base_uri = null;
    }

    @Test
    public void xslFnCollectionTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnCollectionTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnCollectionTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnCollectionTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";
        
        m_fileComparisonType = XSLTestConstants.JSON;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnCollectionTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnCollectionTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";
        
        m_fileComparisonType = XSLTestConstants.JSON;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnCollectionTest7() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";
        
        m_fileComparisonType = XSLTestConstants.JSON;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
