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
package org.apache.xalan.tests.xslt3;

import org.apache.xalan.tests.util.XSLTestConstants;
import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XSL 3 stylesheet test cases, to test XSL transformation with 
 * an initial named template.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslInitialTemplateTests extends XslTransformTestsUtil {        
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "xsl_initial_template/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "xsl_initial_template/gold/";

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
    public void xslInitialTemplateTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";
        
        m_initTemplateName = "main";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslInitialTemplateTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";
        
        // This XSL stylesheet test case doesn't specify an initial 
        // template's name. Therefore, this XSL stylesheet transformation
        // shall take place as usual.
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslInitialTemplateTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";
        
        m_initTemplateName = "main";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslInitialTemplateTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";
        
        m_initTemplateName = "main";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslInitialTemplateTest5() {
        String xmlFilePath = null;        
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";
        
        m_initTemplateName = "main";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslInitialTemplateTest6() {
        String xmlFilePath = null;        
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";
        
        m_initTemplateName = "main";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
