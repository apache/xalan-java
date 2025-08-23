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
 * XSL stylesheet test cases, to test xsl:for-each's 
 * xsl:sort instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslSortTests extends XslTransformTestsUtil {
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "xsl_sort/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "xsl_sort/gold/";      


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
    public void xslSortTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslSortTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";
                               
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);      
    }
    
    @Test
    public void xslSortTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest7() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest8() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test8.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test8.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test8.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest9() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test9.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test9.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test9.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest10() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test10.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test10.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test10.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest11() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test11.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test11.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest12() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test12.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test12.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest13() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test13.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test13.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest14() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test14.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test14.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest15() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test15.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test14.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest16() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test16.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test16.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest17() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test17.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test17.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest18() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test18.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test18.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test18.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest19() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test19.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test19.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest20() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test20.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test20.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest21() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test21.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test21.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslSortTest22() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test22.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test22.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test22.out";
        
        m_fileComparisonType = XSLTestConstants.JSON;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
