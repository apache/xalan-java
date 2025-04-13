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
 * XSLT 3.0 stylesheet test cases to test, xsl:try and xsl:catch 
 * instructions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslTryCatchTests extends XSLTransformTestsUtil {
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "xsl_try_catch/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "xsl_try_catch/gold/";      


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
    public void xslTryCatchTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest4() {
    	 String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl"; 
         String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
         
         String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";
         
         runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest5() {
    	 String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl"; 
         String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
         
         String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";
         
         runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest6() {
    	 String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl"; 
         String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
         
         String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";
         
         runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest7() {
    	 String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl"; 
         String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl";
         
         String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";
         
         runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest8() {
    	 String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test8.xsl"; 
         String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test8.xsl";
         
         String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test8.out";
         
         runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest9() {
    	 String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
         String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test9.xsl";
         
         String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test9.out";
         
         runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest10() {
    	 String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xml"; 
         String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test10.xsl";
         
         String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test10.out";
         
         setXmlValidationProperty(true);
         
         runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslTryCatchTest11() {
    	 String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xml"; 
         String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test11.xsl";
         
         String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test11.out";
         
         setXmlValidationProperty(true);
         
         runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
