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
 * XSL 3 stylesheet test cases for xsl:result-document instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslResultDocumentTests extends XslTransformTestsUtil {
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "xsl_result_document/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "xsl_result_document/gold/";      


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
    public void xslResultDocumentTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        xslTransformInpPath = XSL_TRANSFORM_INPUT_DIRPATH;
        
        String goldFileName1 = "credits.xml";
        String goldFileName2 = "debits.xml";
        
        String goldFilePath1 = XSL_TRANSFORM_GOLD_DIRPATH + goldFileName1;
        String goldFilePath2 = XSL_TRANSFORM_GOLD_DIRPATH + goldFileName2;
        
        try {
        	String goldFileContentStr1 = getFileContentAsString(goldFilePath1);
        	String goldFileContentStr2 = getFileContentAsString(goldFilePath2);

        	String[] goldFileStrArr = new String[] { goldFileContentStr1, goldFileContentStr2 };        	            
        	String[] goldFileNameArr = new String[] { goldFileName1, goldFileName2 };
        	
            runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFileStrArr, 
            		                                                              goldFileNameArr, null);                        
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    @Test
    public void xslResultDocumentTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        xslTransformInpPath = XSL_TRANSFORM_INPUT_DIRPATH;
        
        fileComparisonType = XSLTestConstants.TEXT;
        
        String goldFileName1 = "credits.txt";
        String goldFileName2 = "debits.txt";
        
        String goldFilePath1 = XSL_TRANSFORM_GOLD_DIRPATH + goldFileName1;
        String goldFilePath2 = XSL_TRANSFORM_GOLD_DIRPATH + goldFileName2;
        
        try {
        	String goldFileContentStr1 = getFileContentAsString(goldFilePath1);
        	String goldFileContentStr2 = getFileContentAsString(goldFilePath2);

        	String[] goldFileStrArr = new String[] { goldFileContentStr1, goldFileContentStr2 };        	            
        	String[] goldFileNameArr = new String[] { goldFileName1, goldFileName2 };
        	
            runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFileStrArr, 
            		                                                              goldFileNameArr, null);                        
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    @Test
    public void xslResultDocumentTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl";
        
        xslTransformInpPath = XSL_TRANSFORM_INPUT_DIRPATH;
        
        fileComparisonType = XSLTestConstants.JSON;
        
        String goldFileName1 = "result1.json";
        
        String goldFilePath1 = XSL_TRANSFORM_GOLD_DIRPATH + goldFileName1;

        
        try {
        	String goldFileContentStr1 = getFileContentAsString(goldFilePath1);

        	String[] goldFileStrArr = new String[] { goldFileContentStr1 };        	            
        	String[] goldFileNameArr = new String[] { goldFileName1 };
        	
            runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFileStrArr, 
            		                                                              goldFileNameArr, null);                        
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    @Test
    public void xslResultDocumentTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
        
        xslTransformInpPath = XSL_TRANSFORM_INPUT_DIRPATH;
        
        fileComparisonType = XSLTestConstants.JSON;
        
        String goldFileName1 = "result2.json";
        
        String goldFilePath1 = XSL_TRANSFORM_GOLD_DIRPATH + goldFileName1;

        
        try {
        	String goldFileContentStr1 = getFileContentAsString(goldFilePath1);

        	String[] goldFileStrArr = new String[] { goldFileContentStr1 };        	            
        	String[] goldFileNameArr = new String[] { goldFileName1 };
        	
            runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFileStrArr, 
            		                                                              goldFileNameArr, null);                        
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    @Test
    public void xslResultDocumentTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
        
        xslTransformInpPath = XSL_TRANSFORM_INPUT_DIRPATH;
        
        fileComparisonType = XSLTestConstants.JSON;
        
        String goldFileName1 = "result3.json";
        
        String goldFilePath1 = XSL_TRANSFORM_GOLD_DIRPATH + goldFileName1;

        
        try {
        	String goldFileContentStr1 = getFileContentAsString(goldFilePath1);

        	String[] goldFileStrArr = new String[] { goldFileContentStr1 };        	            
        	String[] goldFileNameArr = new String[] { goldFileName1 };
        	
            runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFileStrArr, 
            		                                                              goldFileNameArr, null);                        
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    @Test
    public void xslResultDocumentTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "transactions.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
        
        xslTransformInpPath = XSL_TRANSFORM_INPUT_DIRPATH;
        
        fileComparisonType = XSLTestConstants.HTML;
        
        String goldFileName1 = "transactions.html";
        
        String goldFilePath1 = XSL_TRANSFORM_GOLD_DIRPATH + goldFileName1;
        
        try {
        	String goldFileContentStr1 = getFileContentAsString(goldFilePath1);

        	String[] goldFileStrArr = new String[] { goldFileContentStr1 };        	            
        	String[] goldFileNameArr = new String[] { goldFileName1 };
        	
            runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFileStrArr, 
            		                                                              goldFileNameArr, null);                        
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }

}
