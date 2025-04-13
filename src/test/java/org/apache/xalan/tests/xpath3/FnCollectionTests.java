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
import org.apache.xalan.tests.util.XSLTransformTestsUtil;
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
public class FnCollectionTests extends XSLTransformTestsUtil {        
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "fn_collection/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "fn_collection/gold/";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // no op
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {        
        xmlDocumentBuilderFactory = null;
        xmlDocumentBuilder = null;
        xslTransformerFactory = null;
    }

    @Test
    public void xslFnCollectionTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";
        
        String localBaseUriPrefix = XSLTestConstants.LOCAL_BASE_URI_PREFIX_OF_TESTS1; 
        
        runXslTransformAndAssertOutputWithXslParamBaseUri(xmlFilePath, xslFilePath, goldFilePath, null, localBaseUriPrefix);
    }
    
    @Test
    public void xslFnCollectionTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";
        
        String localBaseUriPrefix = XSLTestConstants.LOCAL_BASE_URI_PREFIX_OF_TESTS1; 
        
        runXslTransformAndAssertOutputWithXslParamBaseUri(xmlFilePath, xslFilePath, goldFilePath, null, localBaseUriPrefix);
    }
    
    @Test
    public void xslFnCollectionTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";                
        
        String localBaseUriPrefix = XSLTestConstants.LOCAL_BASE_URI_PREFIX_OF_TESTS1; 
        
        runXslTransformAndAssertOutputWithXslParamBaseUri(xmlFilePath, xslFilePath, goldFilePath, null, localBaseUriPrefix);
    }
    
    @Test
    public void xslFnCollectionTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";                
        
        String localBaseUriPrefix = XSLTestConstants.LOCAL_BASE_URI_PREFIX_OF_TESTS1; 
        
        runXslTransformAndAssertOutputWithXslParamBaseUri(xmlFilePath, xslFilePath, goldFilePath, null, localBaseUriPrefix);
    }
    
    @Test
    public void xslFnCollectionTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";                
        
        String localBaseUriPrefix = XSLTestConstants.LOCAL_BASE_URI_PREFIX_OF_TESTS1; 
        
        runXslTransformAndAssertOutputWithXslParamBaseUri(xmlFilePath, xslFilePath, goldFilePath, null, localBaseUriPrefix);
    }
    
    @Test
    public void xslFnCollectionTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";                
        
        String localBaseUriPrefix = XSLTestConstants.LOCAL_BASE_URI_PREFIX_OF_TESTS1; 
        
        runXslTransformAndAssertOutputWithXslParamBaseUri(xmlFilePath, xslFilePath, goldFilePath, null, localBaseUriPrefix);
    }
    
    @Test
    public void xslFnCollectionTest7() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";                
        
        String localBaseUriPrefix = XSLTestConstants.LOCAL_BASE_URI_PREFIX_OF_TESTS1; 
        
        runXslTransformAndAssertOutputWithXslParamBaseUri(xmlFilePath, xslFilePath, goldFilePath, null, localBaseUriPrefix);
    }

}
