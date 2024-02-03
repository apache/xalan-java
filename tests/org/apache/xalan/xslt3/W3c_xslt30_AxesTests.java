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
package org.apache.xalan.xslt3;

import org.apache.xalan.util.XSLTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XSLT 3.0 test cases for the XPath axes.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3c_xslt30_AxesTests extends XSLTransformTestsUtil {
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + 
                                                                                                 "w3c_xslt30_testsuite/axes/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + 
                                                                                                 "w3c_xslt30_testsuite/axes/gold/";

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
    public void xslAxesTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes001.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-001.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-002.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-003.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-004.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-005.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-006.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest7() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-007.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest8() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-008.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test8.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest9() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-009.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test9.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest10() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-010.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test10.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest11() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-011.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test11.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest12() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-012.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test12.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest13() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-013.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test13.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest14() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-014.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test14.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest15() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes002.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-015.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test15.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest16() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes003.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-031.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test16.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslAxesTest17() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes004.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "axes-032.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test17.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
