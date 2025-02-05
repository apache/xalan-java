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

import org.apache.xalan.tests.util.XSLConstants;
import org.apache.xalan.tests.util.XSLTransformTestsUtil;
import org.apache.xalan.tests.util.XslTestsErrorHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XPath 3.1 test cases for function fn:tokenize.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FnTokenizeTests extends XSLTransformTestsUtil {
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "fn_tokenize/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "fn_tokenize/gold/";

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
    public void xslFnTokenizeTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_a.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslFnTokenizeTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest7() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest8() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test8.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test8.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest9() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test9.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test9.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest10() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test10.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test10.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test10.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest11() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test11.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test11.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test11.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest12() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test12.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test12.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test12.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest13() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test13.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test13.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test13.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest14() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test1.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test1.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest15() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test2.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test2.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest16() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test3.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test3.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest17() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test4.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test4.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest18() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test5.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test5.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest19() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test6.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test6.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest20() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test7.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test7.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test7.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest21() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_sample_strings.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test8.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test8.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest22() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_sample_strings.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test9.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test8.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest23() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_sample_strings.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test10.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "negative_test.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, new XslTestsErrorHandler());   
    }
    
    @Test
    public void xslFnTokenizeTest24() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_sample_strings.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test11.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "negative_test.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, new XslTestsErrorHandler());   
    }
    
    @Test
    public void xslFnTokenizeTest25() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_sample_strings.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test12.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test8.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest26() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_sample_strings.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test13.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "fn_tokenize_test8.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslFnTokenizeTest27() {
    	String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_sample_strings.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "fn_tokenize_test14.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "negative_test.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, new XslTestsErrorHandler());   
    }

}
