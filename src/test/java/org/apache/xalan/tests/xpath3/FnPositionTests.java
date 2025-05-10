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
 * XPath 3.1 function fn:position test cases.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FnPositionTests extends XslTransformTestsUtil {        
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "fn_position/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "fn_position/gold/";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // no op
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {        
        m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        m_xslTransformerFactory = null;
    }

    @Test
    public void xslFnPositionTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";
        
        m_fileComparisonType = XSLTestConstants.HTML;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnPositionTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";
        
        m_fileComparisonType = XSLTestConstants.HTML;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnPositionTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";
        
        m_fileComparisonType = XSLTestConstants.HTML;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnPositionTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";
        
        m_fileComparisonType = XSLTestConstants.HTML;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnPositionTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnPositionTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "grp6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
