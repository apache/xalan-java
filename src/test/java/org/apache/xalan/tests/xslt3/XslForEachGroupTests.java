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
import org.apache.xalan.tests.util.XslTestsErrorHandler;
import org.apache.xalan.tests.util.XslTransformTestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XSLT 3.0 stylesheet test cases, to test xsl:for-each-group instruction. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslForEachGroupTests extends XslTransformTestsUtil {
    
    private static final String XSL_TRANSFORM_INPUT_DIRPATH = XSLTestConstants.XSL_TRANSFORM_INPUT_DIRPATH_PREFIX + "xsl_for_each_group/";
    
    private static final String XSL_TRANSFORM_GOLD_DIRPATH = XSLTestConstants.XSL_TRANSFORM_GOLD_DIRPATH_PREFIX + "xsl_for_each_group/gold/";

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
    public void xslForEachGroupTest1() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_a.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test1.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest2() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_b.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test2.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslForEachGroupTest3() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_c.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test3.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test3.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);   
    }
    
    @Test
    public void xslForEachGroupTest4() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_a.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test4.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test4.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);      
    }
    
    @Test
    public void xslForEachGroupTest5() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_a.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test5.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test5.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest6() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_c.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test6.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test6.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest7() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_a.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test7.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test7.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest8() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_c.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test8.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test8.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest9() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_c.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test9.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test9.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest10() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_d.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test10.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test10.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest11() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_e.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test11.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test11.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest12() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_f.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test12.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test12.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest13() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test13.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test13.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest14() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_2.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test14.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test14.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest15() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_3.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test15.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test15.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest16() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_3.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test16.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test16.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest17() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_4.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test17.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test17.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest18() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_5.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test18.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test18.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest19() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_4.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test19.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test19.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest20() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_4.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test20.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test20.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, 
                                                          new XslTestsErrorHandler());
    }
    
    @Test
    public void xslForEachGroupTest21() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_5.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test21.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test21.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, 
                                                            new XslTestsErrorHandler());
    }
    
    @Test
    public void xslForEachGroupTest22() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_3.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test22.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test22.out";                
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest23() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test2_3.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test23.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test23.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, 
                                                            new XslTestsErrorHandler());
    }
    
    @Test
    public void xslForEachGroupTest24() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_c.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test24.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test24.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest25() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_g.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test25.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test25.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest26() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_h.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test26.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test26.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest27() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test1_i.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test27.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test27.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest28() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test28.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test28.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test28.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest29() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test29.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test29.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test29.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest30() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test30.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test30.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test30.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest31() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test31.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test31.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test31.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest32() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test32.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test32.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test32.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest33() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test33.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test33.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test33.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest34() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test34.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test34.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test34.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest35() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test35.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test35.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test35.out";
        
        m_fileComparisonType = XSLTestConstants.JSON;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest36() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test36.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test36.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test36.out";
        
        m_fileComparisonType = XSLTestConstants.JSON;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest37() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test37.xsl"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "test37.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "test37.out";
        
        m_fileComparisonType = XSLTestConstants.JSON;
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest38() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "for_each_group_ending_with.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "for_each_group_ending_with.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "for_each_group_grp_ending_with.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest39() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "for_each_group_adjacent.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "for_each_group_adjacent.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "for_each_group_adjacent.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest40() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "composite_grouping_key_test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "composite_grouping_key1.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "for_each_group_composite_key.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }
    
    @Test
    public void xslForEachGroupTest41() {
        String xmlFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "composite_grouping_key_test1.xml"; 
        String xslFilePath = XSL_TRANSFORM_INPUT_DIRPATH + "composite_grouping_key2.xsl";
        
        String goldFilePath = XSL_TRANSFORM_GOLD_DIRPATH + "for_each_group_composite_key.out";
        
        runXslTransformAndAssertOutput(xmlFilePath, xslFilePath, goldFilePath, null);
    }

}
