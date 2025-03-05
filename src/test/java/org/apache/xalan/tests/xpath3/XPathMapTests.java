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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XPath 3.1 test cases, to test XPath 'map' expressions 
 * and 'map' functions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathMapTests extends XSLTransformTestsUtil {        
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "xpath_maps/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "xpath_maps/gold/";

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
    public void xslMapTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest7() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest8() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test8.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test8.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test8.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest9() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test9.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test9.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test9.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest10() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test10.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test10.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test10.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest11() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test11.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test11.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test11.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest12() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test12.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test12.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test12.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest13() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test13.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test13.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test13.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest14() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test14.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test14.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test14.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest15() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test15.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test15.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test15.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest16() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test16.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test16.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test16.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest17() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test17.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test17.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test17.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest18() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test18.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test18.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test18.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest19() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test19.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test19.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test19.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest20() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test20.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test20.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test20.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest21() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test21.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test21.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test21.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest22() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test22.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test22.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test22.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest23() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test23.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test23.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test23.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest24() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test24.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test24.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test24.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest25() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test25.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test25.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest26() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test26.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test26.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test26.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest27() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test27.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test27.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test26.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest28() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test28.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test28.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test27.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest29() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test29.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test29.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test29.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest30() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test30.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test30.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test30.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest31() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test31.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test31.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test31.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslMapTest32() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test32.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test32.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test32.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
