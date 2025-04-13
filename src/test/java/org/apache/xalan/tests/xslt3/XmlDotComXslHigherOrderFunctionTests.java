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
import org.apache.xalan.tests.util.XSLTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XSLT 3.0 test cases, to test XPath 3.1 higher order functions (HOF).
 * 
 * These XSLT test cases, are for XSLT and XPath examples available within 
 * following XSLT and XPath HOF article published on www.xml.com : 
 * https://www.xml.com/articles/2023/12/05/xml-path-language-xpath-higher-order-functions/.
 * 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XmlDotComXslHigherOrderFunctionTests extends XSLTransformTestsUtil {
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + 
    		                                                                                        "xml_dot_com_higher_order_functions_tests/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + 
    		                                                                                      "xml_dot_com_higher_order_functions_tests/gold/";      


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
    public void xslXmlDotComHofTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml2.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml2.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

    @Test
    public void xslXmlDotComHofTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml2.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml3.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml6.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest7() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl7.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl7.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest8() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml4.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl8.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test8.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest9() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml5.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl9.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test9.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest10() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xml6.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl10.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test10.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslXmlDotComHofTest11() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl11.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "xsl11.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test11.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
}
